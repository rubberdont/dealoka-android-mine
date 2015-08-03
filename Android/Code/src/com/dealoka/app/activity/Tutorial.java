package com.dealoka.app.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dealoka.lib.control.imageview.RecyclingImageView;
import com.dealoka.lib.General;
import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Tutorial extends Activity {
	public static Tutorial instance;
	private ViewPager vwp_tutorial;
	private LinearLayout lay_command;
	private Button btn_start;
	private RelativeLayout lay_point;
	private List<String> drawable;
	private ArrayList<ImageView> points;
	private DisplayImageOptions options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial);
		instance = this;
		setInitial();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}
	private void setInitial() {
		options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
			.cacheOnDisk(false)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();
		vwp_tutorial = (ViewPager)findViewById(R.id.vwp_tutorial);
		lay_command = (LinearLayout)findViewById(R.id.lay_command);
		btn_start = (Button)findViewById(R.id.btn_start);
		lay_point = (RelativeLayout)findViewById(R.id.lay_point);
		setEventListener();
		populateData();
	}
	private void setEventListener() {
		btn_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		vwp_tutorial.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				setPointSelected(points, arg0, points.size());
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	private void populateData() {
		drawable = new ArrayList<String>();
		final List<String> title = Arrays.asList(getResources().getStringArray(R.array.tutorial_title));
		for(int counter = 0; counter < title.size(); counter++) {
			drawable.add("drawable://" + General.getDrawable(R.drawable.class, "tutorial_" + (counter + 1)));
		}
		vwp_tutorial.setAdapter(new TutorialPagerAdapter());
		points = GlobalController.setDrawPoint(lay_point, DealokaApp.getAppContext(), title.size());
		setPointSelected(points, 0, title.size());
	}
	private void setPointSelected(ArrayList<ImageView> points_of, final int index, final int size) {
		if(size <= 0) {
			return;
		}
		if(index == (size - 1)) {
			lay_command.setVisibility(View.VISIBLE);
		}else {
			lay_command.setVisibility(View.GONE);
		}
		for(int counter = index; counter < size; counter++) {
			points_of.get(counter).setSelected(false);
		}
		for(int counter = 0; counter <= index; counter++) {
			points_of.get(counter).setSelected(true);
		}
	}
	@SuppressLint("InflateParams")
	private View setImage(final int index) {
		LayoutInflater layout_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layout_inflater.inflate(R.layout.tutorial_view, null);
		try {
			RecyclingImageView img_tutorial = (RecyclingImageView)view.findViewById(R.id.img_tutorial);
			ImageLoader.getInstance().displayImage("drawable://" + General.getDrawable(R.drawable.class, "tutorial_" + (index + 1)), img_tutorial, options);
		}catch(OutOfMemoryError ex) {}
		return view;
	}
	private class TutorialPagerAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			return drawable.size();
		}
		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			View v = setImage(position);
			collection.addView(v, 0);
			return v;
		}
		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			collection.removeView((View)view);
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}
		@Override
		public void finishUpdate(ViewGroup arg0) {}
		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}
		@Override
		public Parcelable saveState() {
			return null;
		}
		@Override
		public void startUpdate(ViewGroup arg0) {}
    }
}