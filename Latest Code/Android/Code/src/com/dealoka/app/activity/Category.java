package com.dealoka.app.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dealoka.app.R;
import com.dealoka.app.adapter.ListAdapter;
import com.dealoka.app.subs.SubsCategory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Category extends Activity {
	public static Category instance;
	private CategoryListener category_listener;
	private ListView lst_category;
	private ListAdapter list_adapter;
	private List<String> key_selected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category);
		instance = this;
		if(getIntent().getStringExtra("from").equals(Register.RegisterView)) {
			category_listener = Register.instance;
		}
		if(getIntent().getExtras().getStringArrayList("key") != null) {
			key_selected = new ArrayList<String>(getIntent().getExtras().getStringArrayList("key"));
		}else {
			key_selected = new ArrayList<String>();
		}
		setInitial();
	}
	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}
	public static interface CategoryListener {
		public void didCategorySelected(final List<String> key, final List<String> value);
		public void didCategoryCancelled();
	}
	public void SyncNewAdded() {
		prefetchListData();
	}
	public void SyncNewChanges() {
		prefetchListData();
	}
	public void SyncChanged() {
		prefetchListData();
	}
	public void SyncRemoved() {
		prefetchListData();
	}
	private void setInitial() {
		lst_category = (ListView)findViewById(R.id.lst_category);
		lst_category.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setSelected(arg2);
			}
		});
		((Button)findViewById(R.id.btn_ok)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> name_selected = new ArrayList<String>();
				Iterator<com.dealoka.app.model.Category> iterator = SubsCategory.category_db.iterator();
				while(iterator.hasNext()) {
					com.dealoka.app.model.Category category = iterator.next();
					if(key_selected.contains(category.category_value)) {
						name_selected.add(category.category_name);
					}
				}
				category_listener.didCategorySelected(key_selected, name_selected);
				finish();
			}
		});
		populateData();
	}
	private void populateData() {
		list_adapter = new ListAdapter(Category.instance, key_selected);
		list_adapter.setCategoryData();
		lst_category.setAdapter(list_adapter);
	}
	private void setSelected(int index) {
		if(key_selected.contains(SubsCategory.category_db.get(index).category_value)) {
			key_selected.remove(SubsCategory.category_db.get(index).category_value);
		}else {
			key_selected.add(SubsCategory.category_db.get(index).category_value);
		}
		prefetchListData();
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