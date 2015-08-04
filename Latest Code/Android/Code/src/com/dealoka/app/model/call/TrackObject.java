package com.dealoka.app.model.call;

public class TrackObject {
	public String category;
	public float lng;
	public float lat;
	public String user_token;
	public String offer_id;
	public String action_type;
	public String merchant_id;
	public String gender;
	public BTSObject bts_object;
	public static class BTSObject {
		public float lng;
		public float lat;
		public float orient;
		public int signal;
		public int cid;
		public int lac;
		public int mcc;
		public int mnc;
		public BTSObject(
				final float lng,
				final float lat,
				final float orient,
				final int signal,
				final int cid,
				final int lac,
				final int mcc,
				final int mnc) {
			this.lng = lng;
			this.lat = lat;
			this.orient = orient;
			this.signal = signal;
			this.cid = cid;
			this.lac = lac;
			this.mcc = mcc;
			this.mnc = mnc;
		}
	}
	public TrackObject(
			final String category,
			final float lng,
			final float lat,
			final String user_token,
			final String offer_id,
			final String action_type,
			final String merchant_id,
			final String gender,
			final BTSObject bts_object) {
		this.category = category;
		this.lng = lng;
		this.lat = lat;
		this.user_token = user_token;
		this.offer_id = offer_id;
		this.action_type = action_type;
		this.merchant_id = merchant_id;
		this.gender = gender;
		this.bts_object = bts_object;
	}
}