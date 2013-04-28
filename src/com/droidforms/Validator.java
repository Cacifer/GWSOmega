package com.droidforms;

public abstract class Validator {
	private final String description;

	public Validator(String description) {
		this.description = description;
	}
	
	public abstract boolean isValid(String text);

	public String getDescription() {
		return description;
	}
}
