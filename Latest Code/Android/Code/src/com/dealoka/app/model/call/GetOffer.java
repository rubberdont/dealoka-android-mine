package com.dealoka.app.model.call;
public class GetOffer {
	public String user_token;
	public String category;
	public String phone_id;
	public float latitude;
	public float longitude;
	public int start;
	public int limit;
	public GetOffer(
			final String user_token,
			final String category,
			final String phone_id,
			final float latitude,
			final float longitude,
			final int start,
			final int limit) {
		this.user_token = user_token;
		this.category = category;
		this.phone_id = phone_id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.start = start;
		this.limit = limit;
	}
}