package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class fk_offer_id implements Parcelable {
	public String $type;
	public String $value;
	public fk_offer_id(final JSONObject json) {
		try {
			$type = json.getString("$type");
			$value = json.getString("$value");
		}catch(JSONException ex) {}
	}
	public fk_offer_id(Parcel in) {
		$type = in.readString();
		$value = in.readString();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString($type);
		dest.writeString($value);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public fk_offer_id createFromParcel(Parcel in) {
			return new fk_offer_id(in);
		}
		@Override
		public fk_offer_id[] newArray(int size) {
			return new fk_offer_id[size];
		}
	};
}