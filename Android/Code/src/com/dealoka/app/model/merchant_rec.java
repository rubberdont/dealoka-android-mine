package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class merchant_rec implements Parcelable {
	public String merchant_name;
	public merchant_rec(final JSONObject json) {
		try {
			merchant_name = json.getString("merchant_name");
		}catch(JSONException ex) {}
	}
	public merchant_rec(Parcel in) {
		merchant_name = in.readString();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(merchant_name);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public merchant_rec createFromParcel(Parcel in) {
			return new merchant_rec(in);
		}
		@Override
		public merchant_rec[] newArray(int size) {
			return new merchant_rec[size];
		}
	};
}