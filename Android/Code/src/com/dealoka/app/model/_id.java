package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.lib.General;

import android.os.Parcel;
import android.os.Parcelable;

public class _id implements Parcelable {
	public String $type = General.TEXT_BLANK;
	public String $value = General.TEXT_BLANK;
	public _id() {}
	public _id(final JSONObject json) {
		try {
			$type = json.getString("$type");
			$value = json.getString("$value");
		}catch(JSONException ex) {}
	}
	protected _id(Parcel in) {
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
		public _id createFromParcel(Parcel in) {
			return new _id(in);
		}
		@Override
		public _id[] newArray(int size) {
			return new _id[size];
		}
	};
}