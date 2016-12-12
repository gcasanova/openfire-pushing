package com.i7.openfire.push.config;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jivesoftware.util.JiveGlobals;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Properties {
	private static final int DEFAULT_WORKERS = 1;
	private static final int DEFAULT_GCM_TTL = 2419200;

	private static volatile Properties instance;
	private final AtomicInteger elasticNodeIndex = new AtomicInteger(1);
	
	private int workers;
	private int gcmTTL;
	private String gcmKey;
	private String gcmUrl;
    private int redisTimeOut;
    private int redisMaxRedirects;
    private Set<String> redisNodes;
    private String elasticSearchAppIndex;
    private List<String> elasticSearchNodes;

    private Properties() {
    	gcmUrl = JiveGlobals.getProperty(Conf.GCM_URL.toString());
    	gcmKey = JiveGlobals.getProperty(Conf.GCM_API_KEY.toString());
    	gcmTTL = JiveGlobals.getIntProperty(Conf.GCM_TTL.toString(), DEFAULT_GCM_TTL);
    	workers = JiveGlobals.getIntProperty(Conf.WORKERS.toString(), DEFAULT_WORKERS);
    	elasticSearchAppIndex = JiveGlobals.getProperty(Conf.ELASTIC_APP_INDEX.toString());
    	redisTimeOut = JiveGlobals.getIntProperty(Conf.REDIS_TIMEOUT.toString(), 0);
    	redisMaxRedirects = JiveGlobals.getIntProperty(Conf.REDIS_MAX_REDIRECTS.toString(), 0);
    	redisNodes = Sets.newHashSet(Arrays.asList(JiveGlobals.getProperty(Conf.REDIS_NODES.toString()).split(",")));
    	elasticSearchNodes = Lists.newArrayList(Arrays.asList(JiveGlobals.getProperty(Conf.ELASTIC_NODES.toString()).split(",")));
    }
    
    public static Properties getInstance() {
    	if (instance == null) {
    		synchronized (Properties.class) {
    			if (instance == null)
    				instance = new Properties();
			}
    	}
    	return instance;
    }
    
	public int getWorkers() {
		return workers;
	}
	
	public int getGcmTTL() {
		return gcmTTL;
	}

	public String getGcmKey() {
		return gcmKey;
	}

	public String getGcmUrl() {
		return gcmUrl;
	}

	public Set<String> getRedisNodes() {
		return redisNodes;
	}
	
	public int getRedisTimeOut() {
		return redisTimeOut;
	}

	public int getRedisMaxRedirects() {
		return redisMaxRedirects;
	}
	
	public String getElasticSearchURL() {
		String nodeIp = elasticSearchNodes.get(elasticNodeIndex.incrementAndGet() % elasticSearchNodes.size());
		return "http://" + nodeIp + ":9200/" + elasticSearchAppIndex + "/";
	}

	private enum Conf {
		GCM_TTL("i7.gcm.ttl"),
		GCM_URL("i7.gcm.url"),
		GCM_API_KEY("i7.gcm.api.key"),
        REDIS_NODES("i7.redis.nodes"),
        ELASTIC_NODES("i7.elastic.nodes"),
        REDIS_TIMEOUT("i7.redis.timeout"),
        WORKERS("i7.push.processor.workers"),
        ELASTIC_APP_INDEX("i7.elastic.app.index"),
        REDIS_MAX_REDIRECTS("i7.redis.max.redirects");

        private final String value;

        Conf(String key) {
            this.value = key;
        }

        @Override
        public String toString(){
            return this.value;
        }
    }
}
