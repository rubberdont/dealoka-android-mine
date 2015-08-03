package com.dealoka.app.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.dealoka.lib.General;
import com.dealoka.lib.GeneralCalendar;

public class OfferRedeemWallet implements Parcelable, Serializable {
	private static final long serialVersionUID = 6753650542299456157L;
	public String id;
	public String unique_code;
	public String md5hash;
	public String merchant_code;
	public String claimed_state;
	public long claimed_time;
	public String image_prefix;
	public boolean validated;
	public boolean expired;
	public long start_time;
	public long end_time;
	public long redeem_time;
	public int day_multiplier;
	public String go_to_url;
	public String button_name;
	public boolean online_flag;
	public fk_offer_id c_fk_offer_id;
	public offer_rec c_offer_rec;
	public fk_merchant_id c_fk_merchant_id;
	public _id c_id;
	public OfferRedeemWallet(final String id, final String json_result) {
		init(id, json_result);
	}
	public OfferRedeemWallet(final String json_result) {
		try {
			final JSONObject json = new JSONObject(json_result);
			if(json.isNull("_id")) {
				return;
			}
			c_id = new _id(new JSONObject(json.getString("_id")));
			this.id = c_id.$value.replace("-", General.TEXT_BLANK).trim();
			init(id, json_result);
		}catch(JSONException ex) {}
	}
	private void init(final String id, final String json_result) {
		try {
			final JSONObject json = new JSONObject(json_result);
			if(json.isNull("_id")) {
				c_id = new _id();
			}else {
				c_id = new _id(new JSONObject(json.getString("_id")));
			}
			this.id = id.replace("-", General.TEXT_BLANK).trim();
			unique_code = json.getString("unique_code");
			md5hash = json.getString("md5hash");
			merchant_code = json.getString("merchant_code");
			claimed_state = json.getString("claimed_state");
			claimed_time = json.getLong("claimed_time");
			image_prefix = json.getString("image_prefix");
			start_time = json.getLong("start_time");
			end_time = json.getLong("end_time");
			redeem_time = json.getLong("redeem_time");
			day_multiplier = json.getInt("day_multiplier");
			if(json.isNull("go_to_url")) {
				go_to_url = General.TEXT_BLANK;
			}else {
				go_to_url = json.getString("go_to_url");
			}
			if(json.isNull("button_name")) {
				button_name = General.TEXT_BLANK;
			}else {
				button_name = json.getString("button_name");
			}
			if(json.isNull("online_flag")) {
				online_flag = false;
			}else {
				online_flag = json.getInt("online_flag") == 1 ? true : false;
			}
			c_fk_offer_id = new fk_offer_id(new JSONObject(json.getString("fk_offer_id")));
			c_offer_rec = new offer_rec(new JSONObject(json.getString("offer_rec")));
			c_fk_merchant_id = new fk_merchant_id(new JSONObject(json.getString("fk_merchant_id")));
			validated = claimed_state.equals("VALIDATED") ? true : false;
			final long next_day = redeem_time + (day_multiplier * 60 * 60 * 24 * 1000);
			if(GeneralCalendar.getUTCTimestamp() >= c_offer_rec.validity_end) {
				expired = true;
			}else if(GeneralCalendar.getUTCTimestamp() >= next_day) {
				expired = true;
			}else {
				expired = false;
			}
		}catch(JSONException ex) {}
	}
	protected OfferRedeemWallet(Parcel in) {
		id = in.readString();
		unique_code = in.readString();
		md5hash = in.readString();
		merchant_code = in.readString();
		claimed_state = in.readString();
		claimed_time = in.readLong();
		image_prefix = in.readString();
		validated = in.readByte() != 0;
		expired = in.readByte() != 0;
		start_time = in.readLong();
		end_time = in.readLong();
		redeem_time = in.readLong();
		day_multiplier = in.readInt();
		c_fk_offer_id = (fk_offer_id)in.readParcelable(fk_offer_id.class.getClassLoader());
		c_offer_rec = (offer_rec)in.readParcelable(offer_rec.class.getClassLoader());
		c_fk_merchant_id = (fk_merchant_id)in.readParcelable(fk_merchant_id.class.getClassLoader());
		c_id = (_id)in.readParcelable(_id.class.getClassLoader());
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(unique_code);
		dest.writeString(md5hash);
		dest.writeString(merchant_code);
		dest.writeString(claimed_state);
		dest.writeLong(claimed_time);
		dest.writeString(image_prefix);
		dest.writeByte((byte)(validated ? 1 : 0));
		dest.writeByte((byte)(expired ? 1 : 0));
		dest.writeLong(start_time);
		dest.writeLong(end_time);
		dest.writeLong(redeem_time);
		dest.writeInt(day_multiplier);
		dest.writeParcelable(c_fk_offer_id, flags);
		dest.writeParcelable(c_offer_rec, flags);
		dest.writeParcelable(c_fk_merchant_id, flags);
		dest.writeParcelable(c_id, flags);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public OfferRedeemWallet createFromParcel(Parcel in) {
			return new OfferRedeemWallet(in);
		}
		@Override
		public OfferRedeemWallet[] newArray(int size) {
			return new OfferRedeemWallet[size];
		}
	};
}