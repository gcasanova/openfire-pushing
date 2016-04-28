package com.i7.openfire.push.providers.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GCMResponse implements Serializable {
	private static final long serialVersionUID = 829892673235070126L;
	
	private long multicastId;
	private Integer success;
	private Integer failure;
	private Integer canonicalIds;
	private List<Map<String, String>> results;

	@JsonProperty("multicast_id")
	public long getMulticastId() {
		return multicastId;
	}
	public void setMulticastId(long multicastId) {
		this.multicastId = multicastId;
	}

	public Integer getSuccess() {
		return success;
	}
	public void setSuccess(Integer success) {
		this.success = success;
	}

	public Integer getFailure() {
		return failure;
	}
	public void setFailure(Integer failure) {
		this.failure = failure;
	}

	@JsonProperty("canonical_ids")
	public Integer getCanonicalIds() {
		return canonicalIds;
	}
	public void setCanonicalIds(Integer canonicalIds) {
		this.canonicalIds = canonicalIds;
	}

	public List<Map<String, String>> getResults() {
		return results;
	}
	public void setResults(List<Map<String, String>> results) {
		this.results = results;
	}
}
