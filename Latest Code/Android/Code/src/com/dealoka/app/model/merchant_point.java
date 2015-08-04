package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class merchant_point implements Parcelable {
	public float Latitude;
	public float Longitude;
	@SuppressLint("UseValueOf")
	public merchant_point(final JSONObject json) {
		try {
			Latitude = new Double(json.getDouble("Latitude")).floatValue();
			Longitude = new Double(json.getDouble("Longitude")).floatValue();
		}catch(JSONException ex) {}
	}
	public merchant_point(Parcel in) {
		Latitude = in.readFloat();
		Longitude = in.readFloat();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(Latitude);
		dest.writeFloat(Longitude);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public merchant_point createFromParcel(Parcel in) {
			return new merchant_point(in);
		}
		@Override
		public merchant_point[] newArray(int size) {
			return new merchant_point[size];
		}
	};
}