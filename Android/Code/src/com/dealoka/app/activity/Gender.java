package com.dealoka.app.activity;

import com.dealoka.app.R;
import com.dealoka.app.adapter.ListAdapter;
import com.dealoka.app.subs.SubsGender;
import com.dealoka.lib.General;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Gender extends Activity {
	public static Gender instance;
	private GenderListener gender_listener;
	private ListView lst_gender;
	private ListAdapter list_adapter;
	private String key_selected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.gender);
		setInitial();
		if(getIntent().getStringExtra("from").equals(Register.RegisterView)) {
			gender_listener = Register.instance;
		}
		if(General.isNotNull(getIntent().getStringExtra("key"))) {
			key_selected = getIntent().getStringExtra("key");
		}
	}
	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}
	public static interface GenderListener {
		public void didGenderSelected(final String key, final String value);
		public void didGenderCancelled();
	}
	public void SyncNewAdded() {
		prefetchListData();
	}
	public void SyncChanged() {
		prefetchListData();
	}
	public void SyncRemoved() {
		prefetchListData();
	}
	private void setInitial() {
		lst_gender = (ListView)findViewById(R.id.lst_gender);
		lst_gender.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setSelected(arg2);
			}
		});
		SubsGender.subscribeGender();
		populateData();
	}
	private void populateData() {
		list_adapter = new ListAdapter(Gender.instance, key_selected);
		list_adapter.setGenderData();
		lst_gender.setAdapter(list_adapter);
	}
	private void setSelected(int index) {
		gender_listener.didGenderSelected(SubsGender.gender_db.get(index).gender_value, SubsGender.gender_db.get(index).gender_name);
		finish();
	}
	private void prefetchListData() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				list_adapter.notifyDataSetChanged();
			}
		});
	}
}