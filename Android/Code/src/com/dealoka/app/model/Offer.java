package com.dealoka.app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Offer implements Serializable {
	private static final long serialVersionUID = 1875954153552830370L;
	public String id;
	public String category;
	public String merchant_id;
	public String merchant_name;
	public float merchant_latitude;
	public float merchant_longitude;
	public String merchant_address;
	public long expiry;
	public String name;
	public String summary;
	public String image_prefix;
	public String image;
	public String conditions;
	public boolean main_button_render;
	public boolean imsi_check_flag;
	public ArrayList<Object> special_promos;
	public Offer(
			final String id,
			final String category,
			final String merchant_id,
			final String merchant_name,
			final float merchant_latitude,
			final float merchant_longitude,
			final String merchant_address,
			final long expiry,
			final String name,
			final String summary,
			final String image_prefix,
			final String image,
			final String conditions,
			final boolean main_button_render,
			final boolean imsi_check_flag,
			final ArrayList<Object> special_promos) {
		this.id = id;
		this.category = category;
		this.merchant_id = merchant_id;
		this.merchant_name = merchant_name;
		this.merchant_latitude = merchant_latitude;
		this.merchant_longitude = merchant_longitude;
		this.merchant_address = merchant_address;
		this.expiry = expiry;
		this.name = name;
		this.summary = summary;
		this.image_prefix = image_prefix;
		this.image = image;
		this.conditions = conditions;
		this.main_button_render = main_button_render;
		this.imsi_check_flag = imsi_check_flag;
		this.special_promos = new ArrayList<Object>(special_promos);
	}
}