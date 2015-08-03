package com.dealoka.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.subs.SubsCategory;
import com.dealoka.lib.General;

public class Category {
	public String id;
	public String category_value;
	public int current_count;
	public String category_name;
	public int total_count;
	public String category_image;
	public Category(final String id, final JSONObject json) {
		try {
			this.id = id;
			category_value = json.getString("category_value");
			current_count = json.getInt("current_count");
			category_name = json.getString("category_name");
			total_count = json.getInt("total_count");
			category_image = json.getString("category_image");
		}catch(JSONException ex) {}
	}
	public Category(final String id, final String category_name) {
		this.id = id;
		this.category_name = category_name;
	}
	public Category(
			final String id,
			final String category_value,
			final String category_name,
			final String category_image) {
		this.id = id;
		this.category_value = category_value;
		this.current_count = 0;
		this.category_name = category_name;
		this.total_count = 0;
		this.category_image = category_image;
	}
	public static void loadFromAssets() {
		try {
			final String content = General.readDataFromAssets(DealokaApp.getAppContext(), "category.json");
			JSONArray array = new JSONArray(content);
			for(int counter = 0; counter < array.length(); counter++) {
				JSONObject json = array.getJSONObject(counter);
				final Category category = new Category(General.TEXT_BLANK, json);
				SubsCategory.category_db.add(category);
			}
		}catch(JSONException ex) {}
	}
}