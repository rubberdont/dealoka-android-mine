package com.dealoka.app.activity;

import java.util.ArrayList;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.Home;
import com.dealoka.app.R;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.subs.SubsManager;
import com.dealoka.lib.control.CustomViewPager;
import com.dealoka.lib.General;
import com.dealoka.lib.GeneralCalendar;
import com.google.android.gms.analytics.GoogleAnalytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Land extends FragmentActivity {
	public static Land instance;
	private CustomViewPager vwp_land;
	private View view_land;
	private View view_register;
	private View view_login;
	private LandPagerAdapter land_adapter;
	private LandMain land_main;
	private Register register;
	private Login login;
	private ArrayList<View> view_list;
	public Land() {
		GlobalController.exit_init();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.land);
		instance = this;
		setInitial();
	}
	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onDestroy() {
		instance = null;
		General.closeToast();
		super.onDestroy();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(vwp_land.getCurrentItem() == 1) {
				GlobalController.exit(this);
			}else {
				vwp_land.setCurrentItem(1);
			}
			return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(GlobalVariables.facebook_controller != null) {
			GlobalVariables.facebook_controller.onActivityResult(requestCode, resultCode, data);
		}
		if(land_main.google_plus_controller != null) {
			land_main.google_plus_controller.onActivityResult(requestCode, resultCode);
		}
	}
	public void setContinue(
			final String token,
			final String email,
			final String first_name,
			final String last_name,
			final String gender,
			final String birthday) {
		General.closeKeyboard(this);
		GlobalVariables.user_session.Init(email, first_name, last_name, gender, birthday);
		GlobalVariables.user_session.Token(token);
		GlobalVariables.user_session.openSession();
		GlobalVariables.time = GeneralCalendar.getUTCTimestamp();
		GlobalVariables.time_session.Time(GlobalVariables.time);
		SubsManager.Home();
		Land.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(DealokaApp.getAppContext(), Home.class);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
	}
	public void setPage(final int index) {
		vwp_land.setCurrentItem(index);
	}
	@SuppressLint("InflateParams")
	private void setInitial() {
		LayoutInflater layout_inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view_land = layout_inflater.inflate(R.layout.land_main, null, false);
		land_main = new LandMain(view_land);
		view_register = layout_inflater.inflate(R.layout.register, null, false);
		register = new Register(view_register);
		view_login = layout_inflater.inflate(R.layout.login, null, false);
		login = new Login(view_login);
		view_list = new ArrayList<View>();
		view_list.add(view_register);
		view_list.add(view_land);
		view_list.add(view_login);
		land_adapter = new LandPagerAdapter();
		vwp_land = (CustomViewPager)findViewById(R.id.vwp_land);
		vwp_land.setAdapter(land_adapter);
		vwp_land.setScrollDurationFactor(3.0);
		vwp_land.setCurrentItem(1);
		setEventListener();
	}
	private void setEventListener() {
		vwp_land.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if(arg0 == 0) {
					register.setAlpha();
				}else if(arg0 == 2) {
					login.setAlpha();
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	private class LandPagerAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			return view_list.size();
		}
		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			collection.addView(view_list.get(position), 0);
			return view_list.get(position);
		}
		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			collection.removeView(view_list.get(position));
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