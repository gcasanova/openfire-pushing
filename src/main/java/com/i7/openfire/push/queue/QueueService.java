package com.i7.openfire.push.queue;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i7.openfire.push.Startable;
import com.i7.openfire.push.config.DataConfig;
import com.i7.openfire.push.entities.PushTokenUpdate;
import com.i7.openfire.push.enums.Client;

import redis.clients.jedis.JedisCluster;

public class QueueService implements Startable {
	private static final Logger log = LoggerFactory.getLogger(QueueService.class);
	
	private static final String PUSH_QUEUE = "queue#push";
	private static final String PUSH_TOKEN_QUEUE = "queue#push#token";
	
	private JedisCluster jedis;
	
	@Override
	public void start() {
		log.debug("Starting RedisQueueService");
		jedis = DataConfig.getInstance().getJedis();
	}

	@Override
	public void stop() {
		log.debug("Stopping RedisQueueService");
		
		jedis.del(PUSH_QUEUE);
		jedis = null;
	}
	
	public void addPushToken(String oldToken, String newToken, Client client) {
		log.debug("Sending push token update to queue. Old token {}, new token {}, client {}", oldToken, newToken, client.getValue());
        
        try {
			jedis.lpush(PUSH_TOKEN_QUEUE, new ObjectMapper().writeValueAsString(new PushTokenUpdate(client.getValue(), oldToken, newToken)));
		} catch (JsonProcessingException e) {
			log.error("Push token update object parsing to String failed");
		}
	}
	
	public void addMessage(Message message) {
        log.debug("Sending new push message to queue: {}", message.toString());
        
        try {
			jedis.lpush(PUSH_QUEUE, new ObjectMapper().writeValueAsString(message));
		} catch (JsonProcessingException e) {
			log.error("Push notification message parsing to String failed: {}", message.toString());
		}
	}

	@SuppressWarnings("deprecation")
	public Message pollMessage() {
		log.debug("Polling push message from queue");
		
		List<String> list = jedis.blpop(PUSH_QUEUE);
		try {
			return new ObjectMapper().readValue(list.get(1), Message.class);
		} catch (IOException e) {
			log.error("Push notification message parsing to Message instance failed: {}", list.get(1));
		}
		return null;
	}
}
