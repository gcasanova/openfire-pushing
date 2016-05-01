package com.i7.openfire.push;

import org.dom4j.Element;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import com.i7.openfire.push.enums.MessageStatus;
import com.i7.openfire.push.queue.QueueService;

public class PushInterceptor implements PacketInterceptor, Startable {
	private static final Logger log = LoggerFactory.getLogger(PushInterceptor.class);

	private UserManager userManager;
	private QueueService queueService;
	private PresenceManager presenceManager;

	public PushInterceptor(UserManager userManager, PresenceManager presenceManager, QueueService queueService) {
		this.userManager = userManager;
		this.queueService = queueService;
		this.presenceManager = presenceManager;
	}

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {

		// Ignore any packets that haven't already been processed.
		if (!processed || !incoming) {
			return;
		}

		if (packet instanceof Message) {
			Message message = (Message) packet;

			if (Message.Type.chat == message.getType()) {
				Element element = message.getChildElement("messageStatus", "com.i7.openfire");
				if (element != null) {
					String statusCode = element.getText();
					MessageStatus messageStatus = MessageStatus.findByValue(Integer.valueOf(statusCode));
					if (messageStatus == null || messageStatus.getValue() == MessageStatus.SENT.getValue()) {
						// new message detected!
						boolean available = false;
						try {
							User user = userManager.getUser(message.getTo().getNode());
							if (user != null)
								available = presenceManager.isAvailable(user);
						} catch (UserNotFoundException e) {
							log.debug("User {} not found", message.getTo().toString());
						}

						if (!available) {
							queueService.addMessage(message);
						}
					}
				} else {
					log.error("Message is missing messageStatus, message: {}", message.toString());
				}
			}
		}
	}

	@Override
	public void start() {
		InterceptorManager.getInstance().addInterceptor(this);
	}

	@Override
	public void stop() {
		InterceptorManager.getInstance().removeInterceptor(this);
		userManager = null;
		presenceManager = null;
	}
}
