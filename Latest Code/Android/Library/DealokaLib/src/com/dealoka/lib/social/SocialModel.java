package com.dealoka.lib.social;

public class SocialModel {
	public static enum SocialEnum {
		Facebook ("FB"),
		GooglePlus ("G+"),
		Twitter ("Twt");
		private final String id;
		SocialEnum(String id) { this.id = id; }
	    public String getValue() { return id; }
	};
}