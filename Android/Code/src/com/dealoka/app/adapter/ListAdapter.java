package com.dealoka.app.adapter;

import java.util.List;

import com.dealoka.app.R;
import com.dealoka.app.subs.SubsCategory;
import com.dealoka.app.subs.SubsGender;
import com.dealoka.lib.General;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
	private enum List_Adapter_Type {
		category_mode,
		gender_mode
	}
	private Activity activity;
	private String key_selected;
	private List<String> key_array_selected;
	private List_Adapter_Type list_adapter_type;
	private static LayoutInflater inflater = null;
	public ListAdapter(Activity activity, final String key_selected) {
		this.activity = activity;
		this.key_selected = key_selected;
		inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public ListAdapter(Activity activity, final List<String> key_array_selected) {
		this.activity = activity;
		this.key_array_selected = key_array_selected;
		inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void setCategoryData() {
		list_adapter_type = List_Adapter_Type.category_mode;
	}
	public void setGenderData() {
		list_adapter_type = List_Adapter_Type.gender_mode;
	}
	public int getCount() {
		switch(list_adapter_type) {
		case category_mode:
			return SubsCategory.category_db.size();
		case gender_mode:
			return SubsGender.gender_db.size();
		default:
			return 0;
		}
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	}
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if(convertView == null) {
			vi = inflater.inflate(R.layout.default_list, null);
		}
		String key = General.TEXT_BLANK;
		String text = General.TEXT_BLANK;
		switch(list_adapter_type) {
		case category_mode:
			key = SubsCategory.category_db.get(position).category_value;
			text = SubsCategory.category_db.get(position).category_name;
			break;
		case gender_mode:
			key = SubsGender.gender_db.get(position).gender_value;
			text = SubsGender.gender_db.get(position).gender_name;
			break;
		}
		((TextView)vi.findViewById(R.id.lbl_caption)).setText(text);
		if(key_array_selected == null) {
			if(key.equals(key_selected)) {
				((ImageView)vi.findViewById(R.id.tick)).setVisibility(View.VISIBLE);
			}else {
				((ImageView)vi.findViewById(R.id.tick)).setVisibility(View.GONE);
			}
		}else {
			if(key_array_selected.contains(key)) {
				((ImageView)vi.findViewById(R.id.tick)).setVisibility(View.VISIBLE);
			}else {
				((ImageView)vi.findViewById(R.id.tick)).setVisibility(View.GONE);
			}
		}
		return vi;
	}
}