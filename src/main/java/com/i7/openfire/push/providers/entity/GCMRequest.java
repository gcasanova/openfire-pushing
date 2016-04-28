package com.i7.openfire.push.providers.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GCMRequest implements Serializable {
	private static final long serialVersionUID = -2747281134977828844L;
	
	private Integer timeToLive;
	private Map<String, String> data;
	private List<String> registrationIds;

	@JsonProperty("time_to_live")
	public Integer getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}
	
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	@JsonProperty("registration_ids")
	public List<String> getRegistrationIds() {
		return registrationIds;
	}
	public void setRegistrationIds(List<String> registrationIds) {
		this.registrationIds = registrationIds;
	}
}
