package com.dealoka.app.model;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class OfferGeo implements Parcelable, Serializable {
	private static final long serialVersionUID = -2195753324513060051L;
	public String id;
	public int total_count;
	public int current_count;
	public float distance;
	public String image_prefix;
	public String address;
	public int available;
	public int redeemed;
	public boolean main_button_render;
	public boolean imsi_check_flag;
	public fk_offer_id c_fk_offer_id;
	public offer_rec c_offer_rec;
	public fk_merchant_id c_fk_merchant_id;
	public merchant_rec c_merchant_rec;
	public merchant_point c_merchant_point;
	public float carrierLatitude;
	public float carrierLongitude;
	@SuppressLint("UseValueOf")
	public OfferGeo(final String id, final String json_result) {
		JSONObject json = null;
		try {
			this.id = id;
			json = new JSONObject(json_result);
		}catch(JSONException ex) {}
		try {
			address = json.getString("address");
		}catch(JSONException ex) {}
		try {
			total_count = json.getInt("total_count");
		}catch(JSONException ex) {}
		try {
			current_count = json.getInt("current_count");
		}catch(JSONException ex) {}
		try {
			distance = new Double(json.getDouble("distance")).floatValue();
		}catch(JSONException ex) {}
		try {
			image_prefix = json.getString("image_prefix");
		}catch(JSONException ex) {
			image_prefix = "http://img.linkertise.com/";
		}
		try {
			main_button_render = json.getInt("main_button_render") == 1 ? true : false;
		}catch(JSONException ex) {}
		try {
			if(json.isNull("imsi_check_flag")) {
				imsi_check_flag = true;
			}else {
				imsi_check_flag = json.getInt("imsi_check_flag") == 1 ? true : false;
			}
		}catch(JSONException ex) {}
		try {
			available = json.getInt("available");
		}catch(JSONException ex) {}
		try {
			redeemed = json.getInt("redeemed");
		}catch(JSONException ex) {}
		try {
			c_fk_offer_id = new fk_offer_id(new JSONObject(json.getString("fk_offer_id")));
		}catch(JSONException ex) {
			JSONObject idObj = new JSONObject();
			try {
				idObj.put("$type", "oid");
				idObj.put("$value", json.getString("_id"));
				c_fk_offer_id = new fk_offer_id(idObj);
			} catch (JSONException e) {e.printStackTrace();}
		}
		try {
			c_offer_rec = new offer_rec(new JSONObject(json.getString("offer_rec")));
		}catch(JSONException ex) {}
		try {
			c_fk_merchant_id = new fk_merchant_id(new JSONObject(json.getString("fk_merchant_id")));
		}catch(JSONException ex) {
			try {
				JSONObject idObj = json.getJSONObject("merchant_rec");
				idObj.put("$type", "oid");
				idObj.put("$value", json.getString("_id"));
				c_fk_merchant_id = new fk_merchant_id(idObj);
			} catch (JSONException e) {e.printStackTrace();}
		}
		try {
			c_merchant_rec = new merchant_rec(new JSONObject(json.getString("merchant_rec")));
		}catch(JSONException ex) {}
		try {
			c_merchant_point = new merchant_point(new JSONObject(json.getString("merchant_point")));
		}catch(JSONException ex) {}
		try{
			JSONObject location = json.getJSONObject("location");
			carrierLatitude = new Double(location.getString("Latitude")).floatValue();
			carrierLongitude = new Double(location.getString("Longitude")).floatValue();
		}catch(JSONException ex){}
	}
	public OfferGeo(Parcel in) {
		id = in.readString();
		address = in.readString();
		total_count = in.readInt();
		current_count = in.readInt();
		distance = in.readFloat();
		image_prefix = in.readString();
		main_button_render = in.readByte() != 0;
		imsi_check_flag = in.readByte() != 0;
		available = in.readInt();
		redeemed = in.readInt();
		c_fk_offer_id = (fk_offer_id)in.readParcelable(fk_offer_id.class.getClassLoader());
		c_offer_rec = (offer_rec)in.readParcelable(offer_rec.class.getClassLoader());
		c_fk_merchant_id = (fk_merchant_id)in.readParcelable(fk_merchant_id.class.getClassLoader());
		c_merchant_rec = (merchant_rec)in.readParcelable(merchant_rec.class.getClassLoader());
		c_merchant_point = (merchant_point)in.readParcelable(merchant_point.class.getClassLoader());
		carrierLatitude = in.readFloat();
		carrierLongitude = in.readFloat();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(address);
		dest.writeInt(total_count);
		dest.writeInt(current_count);
		dest.writeFloat(distance);
		dest.writeString(image_prefix);
		dest.writeByte((byte)(main_button_render ? 1 : 0));
		dest.writeByte((byte)(imsi_check_flag ? 1 : 0));
		dest.writeInt(available);
		dest.writeInt(redeemed);
		dest.writeParcelable(c_fk_offer_id, flags);
		dest.writeParcelable(c_offer_rec, flags);
		dest.writeParcelable(c_fk_merchant_id, flags);
		dest.writeParcelable(c_merchant_rec, flags);
		dest.writeParcelable(c_merchant_point, flags);
		dest.writeFloat(carrierLatitude);
		dest.writeFloat(carrierLongitude);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public OfferGeo createFromParcel(Parcel in) {
			return new OfferGeo(in);
		}
		@Override
		public OfferGeo[] newArray(int size) {
			return new OfferGeo[size];
		}
	};
}