package com.dealoka.app.model.call;

public class OfferValidateCode {
	public String offer_id;
	public String user_token;
	public String md5hash;
	public String unique_code;
	public TrackObject c_track_object;
	public OfferValidateCode(
			final String offer_id,
			final String user_token,
			final String md5hash,
			final String unique_code,
			final TrackObject c_track_object) {
		this.offer_id = offer_id;
		this.user_token = user_token;
		this.md5hash = md5hash;
		this.unique_code = unique_code;
		this.c_track_object = c_track_object;
	}
}