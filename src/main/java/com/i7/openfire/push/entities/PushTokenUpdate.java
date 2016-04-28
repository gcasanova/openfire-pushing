package com.i7.openfire.push.entities;

import java.io.Serializable;

public class PushTokenUpdate implements Serializable {
	private static final long serialVersionUID = -547540614610578990L;
	
	private String client;
	private String oldToken;
	private String newToken;
	
	public PushTokenUpdate(String client, String oldToken, String newToken) {
		this.client = client;
		this.oldToken = oldToken;
		this.newToken = newToken;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getOldToken() {
		return oldToken;
	}

	public void setOldToken(String oldToken) {
		this.oldToken = oldToken;
	}

	public String getNewToken() {
		return newToken;
	}

	public void setNewToken(String newToken) {
		this.newToken = newToken;
	}
}
