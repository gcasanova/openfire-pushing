package com.i7.openfire.push.providers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.i7.openfire.push.config.Properties;
import com.i7.openfire.push.enums.Client;
import com.i7.openfire.push.plugin.PushingPlugin;
import com.i7.openfire.push.providers.entity.GCMRequest;
import com.i7.openfire.push.providers.entity.GCMResponse;
import com.i7.openfire.push.queue.QueueService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class GCMService {
	private static final Logger log = LoggerFactory.getLogger(GCMService.class);
	
	public static final String USER_DEVICES_PREFIX = "user#devices#";
	
	private int ttl;
	private String url;
	private String apiKey;
	private QueueService queueService;
	
	private static GCMService instance;
	
	private GCMService() {
		ttl = Properties.getInstance().getGcmTTL();
		url = Properties.getInstance().getGcmUrl();
		apiKey = Properties.getInstance().getGcmKey();
		queueService = PushingPlugin.getInstance().getQueueService();
	}
	
	public static GCMService getInstance() {
		if (instance == null) {
			synchronized (GCMService.class) {
				if (instance == null) {
					instance = new GCMService();
				}
			}
		}
		return instance;
	}
	
	public void sendChatNotification(String sMessage, List<String> tokens) {
		GCMRequest req = new GCMRequest();
		req.setRegistrationIds(tokens);
		req.setTimeToLive(ttl);
		
		Map<String, String> data = Maps.newHashMap();
		data.put("message", sMessage);
		req.setData(data);
  	
		processRequest(req, tokens);
	}
	
	private void processRequest(GCMRequest req, List<String> tokens) {
		List<String> repeatTokens = doRequest(req, tokens);
		
		int i = 0;
		while (!repeatTokens.isEmpty()) {
			i++;
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			req.setRegistrationIds(repeatTokens);
			repeatTokens = doRequest(req, repeatTokens);
			
			if (i >= 3) {
				repeatTokens.clear();
			}
		}
	}
	
	private List<String> doRequest(GCMRequest req, List<String> tokens) {
		List<String> repeatTokens = Lists.newArrayList();
		
		try {
			HttpResponse<JsonNode> jsonNode = Unirest.post(url)
					  .header("Content-Type", "application/json")
					  .header("Authorization",  "key=" + apiKey)
					  .body(req)
				  .asJson();
			
			GCMResponse gcmResponse = new ObjectMapper().readValue(jsonNode.getRawBody(), GCMResponse.class);
			if (jsonNode.getStatus() == HttpStatus.SC_OK) {
				if (gcmResponse.getFailure() == 0 && gcmResponse.getCanonicalIds() == 0) {
					return repeatTokens;
				}
				
				List<Map<String, String>> results = gcmResponse.getResults();
				for (int i = 0; i < results.size(); i++) {
					if (results.get(i).get("message_id ") != null) {
						if (results.get(i).get("registration_id") != null) {
							// update GCM registration id
							queueService.addPushToken(tokens.get(i), results.get(i).get("registration_id"), Client.ANDROID);
						}
					} else {
						if (results.get(i).get("error").equals("Unavailable")) {
							repeatTokens.add(tokens.get(i));
						} else {
							queueService.addPushToken(tokens.get(i), null, Client.ANDROID);
						}
					}
				}
			}
		} catch (UnirestException | IOException e) {
			log.error("GCMService process failed: {}", e.getMessage());
			repeatTokens = tokens;
		}
		return repeatTokens;
	}
}
