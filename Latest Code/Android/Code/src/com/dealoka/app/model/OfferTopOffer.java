package com.dealoka.app.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class OfferTopOffer implements Parcelable, Serializable {
	private static final long serialVersionUID = 3793693508574887396L;
	public String id;
	public String address;
	public String image_prefix;
	public boolean main_button_render;
	public int available;
	public int redeemed;
	public boolean imsi_check_flag;
	public offer_rec c_offer_rec;
	public fk_offer_id c_fk_offer_id;
	public fk_merchant_id c_fk_merchant_id;
	public merchant_rec c_merchant_rec;
	public merchant_point c_merchant_point;
	public OfferTopOffer(final String id, final String json_result) {
		try {
			this.id = id;
			JSONObject json = new JSONObject(json_result);
			address = json.getString("address");
			image_prefix = json.getString("image_prefix");
			main_button_render = json.getInt("main_button_render") == 1 ? true : false;
			available = json.getInt("available");
			redeemed = json.getInt("redeemed");
			if(json.isNull("imsi_check_flag")) {
				imsi_check_flag = true;
			}else {
				imsi_check_flag = json.getInt("imsi_check_flag") == 1 ? true : false;
			}
			c_offer_rec = new offer_rec(new JSONObject(json.getString("offer_rec")));
			c_fk_offer_id = new fk_offer_id(new JSONObject(json.getString("fk_offer_id")));
			c_fk_merchant_id = new fk_merchant_id(new JSONObject(json.getString("fk_merchant_id")));
			c_merchant_rec = new merchant_rec(new JSONObject(json.getString("merchant_rec")));
			c_merchant_point = new merchant_point(new JSONObject(json.getString("merchant_point")));
		}catch(JSONException ex) {}
	}
	public OfferTopOffer(Parcel in) {
		id = in.readString();
		address = in.readString();
		image_prefix = in.readString();
		main_button_render = in.readByte() != 0;
		imsi_check_flag = in.readByte() != 0;
		available = in.readInt();
		redeemed = in.readInt();
		c_offer_rec = (offer_rec)in.readParcelable(offer_rec.class.getClassLoader());
		c_fk_offer_id = (fk_offer_id)in.readParcelable(fk_offer_id.class.getClassLoader());
		c_fk_merchant_id = (fk_merchant_id)in.readParcelable(fk_merchant_id.class.getClassLoader());
		c_merchant_rec = (merchant_rec)in.readParcelable(merchant_rec.class.getClassLoader());
		c_merchant_point = (merchant_point)in.readParcelable(merchant_point.class.getClassLoader());
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(address);
		dest.writeString(image_prefix);
		dest.writeByte((byte)(main_button_render ? 1 : 0));
		dest.writeByte((byte)(imsi_check_flag ? 1 : 0));
		dest.writeInt(available);
		dest.writeInt(redeemed);
		dest.writeParcelable(c_offer_rec, flags);
		dest.writeParcelable(c_fk_offer_id, flags);
		dest.writeParcelable(c_fk_merchant_id, flags);
		dest.writeParcelable(c_merchant_rec, flags);
		dest.writeParcelable(c_merchant_point, flags);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public OfferTopOffer createFromParcel(Parcel in) {
			return new OfferTopOffer(in);
		}
		@Override
		public OfferTopOffer[] newArray(int size) {
			return new OfferTopOffer[size];
		}
	};
}