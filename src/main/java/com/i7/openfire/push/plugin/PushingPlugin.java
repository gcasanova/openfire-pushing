package com.i7.openfire.push.plugin;

import java.io.File;

import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.i7.openfire.push.PushInterceptor;
import com.i7.openfire.push.PushProcessor;
import com.i7.openfire.push.queue.QueueService;

public class PushingPlugin implements Plugin {
	private static final Logger log = LoggerFactory.getLogger(PushingPlugin.class);

	private boolean enabled = true;
	private boolean shuttingDown = false;

	private UserManager userManager;
	private QueueService queueService;
	private PushProcessor pushProcessor;
	private PresenceManager presenceManager;
	private PushInterceptor pushInterceptor;

	private static PushingPlugin instance;

	public PushingPlugin() {
		instance = this;
	}

	public static PushingPlugin getInstance() {
		return instance;
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		log.debug("Initializing plugin");
		// enabled =
		// JiveGlobals.getBooleanProperty(ArchivingProperties.ENABLED.getValue(),
		// false);

		shuttingDown = false;
		userManager = XMPPServer.getInstance().getUserManager();
		presenceManager = XMPPServer.getInstance().getPresenceManager();
		
		queueService = new QueueService();
		queueService.start();
		
		pushProcessor = new PushProcessor(queueService);
		pushProcessor.start();

		pushInterceptor = new PushInterceptor(userManager, presenceManager, queueService);
		pushInterceptor.start();
	}

	@Override
	public void destroyPlugin() {
		log.debug("Destroying plugin");
		shuttingDown = true;
		
		pushInterceptor.stop();
		pushProcessor.stop();
		queueService.stop();

		pushInterceptor = null;
		pushProcessor = null;
		queueService = null;
		instance = null;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean isShuttingDown() {
		return shuttingDown;
	}
	
	public PushProcessor getPushProcessor() {
		return pushProcessor;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public PresenceManager getPresenceManager() {
		return presenceManager;
	}
	
	public QueueService getQueueService() {
		return queueService;
	}
}
