package com.dealoka.app.model.call;

public class RedeemCouponCode {
	public String offer_id;
	public String user_token;
	public String phone_id;
	public String imsi;
	public String version;
	public String phone_type;
	public long special_coupon_id;
	public String offer_special_brand;
	public TrackObject c_track_object;
	public RedeemCouponCode(
			final String offer_id,
			final String user_token,
			final String phone_id,
			final String version,
			final String phone_type,
			final TrackObject c_track_object) {
		this.offer_id = offer_id;
		this.user_token = user_token;
		this.phone_id = phone_id;
		this.version = version;
		this.phone_type = phone_type;
		this.c_track_object = c_track_object;
	}
	public RedeemCouponCode(
			final String offer_id,
			final String user_token,
			final String phone_id,
			final String imsi,
			final String version,
			final String phone_type,
			final long special_coupon_id,
			final String offer_special_brand,
			final TrackObject c_track_object) {
		this.offer_id = offer_id;
		this.user_token = user_token;
		this.phone_id = phone_id;
		this.imsi = imsi;
		this.version = version;
		this.phone_type = phone_type;
		this.special_coupon_id = special_coupon_id;
		this.offer_special_brand = offer_special_brand;
		this.c_track_object = c_track_object;
	}
}