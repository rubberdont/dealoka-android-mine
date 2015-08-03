package com.dealoka.app.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class fk_offer_rec implements Parcelable {
	public String summary;
	public String image;
	public String conditions;
	public String category;
	public long validity_end;
	public String name;
	public fk_offer_rec(final JSONObject json) {
		try {
			summary = json.getString("summary");
			image = json.getString("image");
			conditions = json.getString("conditions");
			category = json.getString("category");
			validity_end = json.getLong("validity_end");
			name = json.getString("name");
		}catch(JSONException ex) {}
	}
	public fk_offer_rec(Parcel in) {
		summary = in.readString();
		image = in.readString();
		conditions = in.readString();
		category = in.readString();
		validity_end = in.readLong();
		name = in.readString();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(summary);
		dest.writeString(image);
		dest.writeString(conditions);
		dest.writeString(category);
		dest.writeLong(validity_end);
		dest.writeString(name);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public fk_offer_rec createFromParcel(Parcel in) {
			return new fk_offer_rec(in);
		}
		@Override
		public fk_offer_rec[] newArray(int size) {
			return new fk_offer_rec[size];
		}
	};
}