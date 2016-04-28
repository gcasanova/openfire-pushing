package com.i7.openfire.push.entities;

import java.io.Serializable;

import com.i7.openfire.push.enums.Client;

public class Device implements Serializable {
	private static final long serialVersionUID = -6466059071602047467L;
	
	private String id;
	private String model;
	private String userId;
	private String nativeId;
	private String langCode;
	private String tokenAuth;
	private String appVersion;
	private String systemVersion;
	private String tokenThirdParty;
	
	private Client client;
	
	private boolean anonymous;
	
	private Long updatedAt;
	private Long createdAt;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNativeId() {
		return nativeId;
	}
	public void setNativeId(String nativeId) {
		this.nativeId = nativeId;
	}
	
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	public String getTokenAuth() {
		return tokenAuth;
	}
	public void setTokenAuth(String tokenAuth) {
		this.tokenAuth = tokenAuth;
	}
	
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	public String getSystemVersion() {
		return systemVersion;
	}
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	
	public String getTokenThirdParty() {
		return tokenThirdParty;
	}
	public void setTokenThirdParty(String tokenThirdParty) {
		this.tokenThirdParty = tokenThirdParty;
	}
	
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	
	public boolean isAnonymous() {
		return anonymous;
	}
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	public Long getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public Long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
}