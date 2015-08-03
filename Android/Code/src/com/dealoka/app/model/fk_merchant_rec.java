package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class fk_merchant_rec implements Parcelable {
	public String merchant_image_row;
	public String merchant_valid_code;
	public String merchant_name;
	public String merchant_image_large;
	public fk_merchant_rec(final JSONObject json) {
		try {
			merchant_image_row = json.getString("merchant_image_row");
			merchant_valid_code = json.getString("merchant_valid_code");
			merchant_name = json.getString("merchant_name");
			merchant_image_large = json.getString("merchant_image_large");
		}catch(JSONException ex) {}
	}
	public fk_merchant_rec(Parcel in) {
		merchant_image_row = in.readString();
		merchant_valid_code = in.readString();
		merchant_name = in.readString();
		merchant_image_large = in.readString();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(merchant_image_row);
		dest.writeString(merchant_valid_code);
		dest.writeString(merchant_name);
		dest.writeString(merchant_image_large);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public fk_merchant_rec createFromParcel(Parcel in) {
			return new fk_merchant_rec(in);
		}
		@Override
		public fk_merchant_rec[] newArray(int size) {
			return new fk_merchant_rec[size];
		}
	};
}