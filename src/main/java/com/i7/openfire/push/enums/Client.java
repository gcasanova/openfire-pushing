package com.i7.openfire.push.enums;

public enum Client {
	ANDROID("android"), IOS("ios"), WEB("web");

	private String value;

	Client(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static Client findByValue(String value) {
		if (value != null) {
			for (Client b : Client.values()) {
				if (value.equalsIgnoreCase(b.value)) {
					return b;
				}
			}
		}
		return null;
	}
}
