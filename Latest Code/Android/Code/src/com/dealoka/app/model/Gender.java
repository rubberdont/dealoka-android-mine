package com.dealoka.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.subs.SubsGender;
import com.dealoka.lib.General;

public class Gender {
	public String id;
	public String gender_value;
	public String gender_name;
	public Gender(final String id, final JSONObject json) {
		try {
			this.id = id;
			gender_value = json.getString("gender_value");
			gender_name = json.getString("gender_name");
		}catch(JSONException ex) {}
	}
	public Gender(final String id, final String gender_name) {
		this.id = id;
		this.gender_name = gender_name;
	}
	public Gender(
			final String id,
			final String gender_value,
			final String gender_name) {
		this.id = id;
		this.gender_value = gender_value;
		this.gender_name = gender_name;
	}
	public static void loadFromAssets() {
		try {
			final String content = General.readDataFromAssets(DealokaApp.getAppContext(), "gender.json");
			JSONArray array = new JSONArray(content);
			for(int counter = 0; counter < array.length(); counter++) {
				JSONObject json = array.getJSONObject(counter);
				final Gender gender = new Gender(General.TEXT_BLANK, json);
				SubsGender.gender_db.add(gender);
			}
		}catch(JSONException ex) {}
	}
}