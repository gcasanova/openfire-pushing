package com.i7.openfire.push;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.i7.openfire.push.config.Properties;
import com.i7.openfire.push.enums.Client;
import com.i7.openfire.push.plugin.PushingPlugin;
import com.i7.openfire.push.providers.GCMService;
import com.i7.openfire.push.queue.QueueService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class PushProcessor implements Startable {
	private static final Logger log = LoggerFactory.getLogger(PushProcessor.class);

	private QueueService queueService;
	private ExecutorService executorService;
	
	public PushProcessor(QueueService queueService) {
		this.queueService = queueService;
	}

	@Override
	public void start() {
		if (PushingPlugin.getInstance().isEnabled()) {
			// Add as many workers as defined by configuration
			executorService = Executors.newFixedThreadPool(Properties.getInstance().getWorkers());
			for (int i = 0; i < Properties.getInstance().getWorkers(); i++) {
				executorService.submit(new WorkerThread(queueService));
			}
		}
	}

	@Override
	public void stop() {
		executorService.shutdown();
	}
	
	private class WorkerThread implements Runnable {
		
		private QueueService queueService;
		
		public WorkerThread(QueueService queueService) {
	        this.queueService = queueService;
	    }

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				log.debug("Starting working processor");
				
				Message message = queueService.pollMessage();
				log.debug("Processing push message {}", message.toString());
				
				try {
					List<String> gcmTokens = Lists.newArrayList();
					for (Entry<String, Client> entry : findTokens(message.getTo().toBareJID()).entrySet()) {
						// only android supported for now
						if (entry.getValue().getValue().equals(Client.ANDROID.getValue()))
							gcmTokens.add(entry.getKey());
					}
					
					if (!gcmTokens.isEmpty()) {
						GCMService.getInstance().sendChatNotification(new ObjectMapper().writeValueAsString(message), gcmTokens);
					}
				} catch (JsonProcessingException | JSONException | UnirestException e) {
					log.error("Unable to send chat notification message {}, due to error: {}", message.toString(), e.getMessage());
				}
				log.debug("Finishing working processor");
			}
		}
		
		private Map<String, Client> findTokens(String userId) throws JSONException, UnirestException {
			Map<String, Client> tokens = Maps.newHashMap();
			HttpResponse<JsonNode> jsonNode = Unirest.post(Properties.getInstance().getElasticSearchURL() + "device/_search")
					  .header("Content-Type", "application/json")
					  .body(new JSONObject().put("query", new JSONObject().put("term", new JSONObject().put("devices", new JSONObject().put("userId", userId)))))
				  .asJson();
			
			JSONArray array = jsonNode.getBody().getObject().getJSONObject("hits").getJSONArray("hits");
			if (array.length() > 0) {
				JSONObject source = null;
				for (int i = 0; i < array.length(); i++) {
					source = array.getJSONObject(i).getJSONObject("_source");
					if (source.getString("tokenThirdParty") != null)
						tokens.put(source.getString("tokenThirdParty"), Client.findByValue(source.getString("client")));
				}
			}
			return tokens;
		}
	}
}
