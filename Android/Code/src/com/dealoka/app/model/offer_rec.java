package com.dealoka.app.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class offer_rec implements Parcelable {
	public String name;
	public String summary;
	public long validity_end;
	public String image;
	public String conditions;
	public String category;
	public ArrayList<Object> special_promos;
	public offer_rec(final JSONObject json) {
		try {
			summary = json.getString("summary");
			image = json.getString("image");
			conditions = json.getString("conditions");
			category = json.getString("category");
			validity_end = json.getLong("validity_end");
			name = json.getString("name");
			special_promos = new ArrayList<Object>();
			if(!json.isNull("special_promos")) {
				JSONArray sp_a = json.getJSONArray("special_promos");
				for(int counter = 0; counter < sp_a.length(); counter++) {
					HashMap<String, Object> sp = new HashMap<String, Object>();
					sp.put("promo_id", sp_a.getJSONObject(counter).getLong("promo_id"));
					sp.put("offer_text", sp_a.getJSONObject(counter).getString("offer_text"));
					sp.put("offer_button_text", sp_a.getJSONObject(counter).getString("offer_button_text"));
					sp.put("offer_background_color", sp_a.getJSONObject(counter).getString("offer_background_color"));
					sp.put("offer_text_color", sp_a.getJSONObject(counter).getString("offer_text_color"));
					sp.put("offer_button_background_color", sp_a.getJSONObject(counter).getString("offer_button_background_color"));
					sp.put("offer_button_text_color", sp_a.getJSONObject(counter).getString("offer_button_text_color"));
					sp.put("offer_logo", sp_a.getJSONObject(counter).getString("offer_logo"));
					sp.put("offer_button_render", sp_a.getJSONObject(counter).getInt("offer_button_render") == 1 ? true : false);
					sp.put("offer_special_brand", sp_a.getJSONObject(counter).getString("offer_special_brand"));
					special_promos.add(sp);
				}
			}
		}catch(JSONException ex) {}
	}
	@SuppressWarnings("unchecked")
	protected offer_rec(Parcel in) {
		summary = in.readString();
		image = in.readString();
		conditions = in.readString();
		category = in.readString();
		validity_end = in.readLong();
		name = in.readString();
		special_promos = (ArrayList<Object>)in.readSerializable();
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
		dest.writeSerializable(special_promos);
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public offer_rec createFromParcel(Parcel in) {
			return new offer_rec(in);
		}
		@Override
		public offer_rec[] newArray(int size) {
			return new offer_rec[size];
		}
	};
}