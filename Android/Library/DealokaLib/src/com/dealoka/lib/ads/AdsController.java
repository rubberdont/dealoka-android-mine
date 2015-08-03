package com.dealoka.lib.ads;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.dealoka.lib.General;
import com.dealoka.lib.manager.PhoneManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdsController {
	private Activity activity;
	private InterstitialAd interstitial_ad;
	private AdRequest ad_request;
	private AdView view;
	private boolean show = false;
	private boolean test = true;
	private boolean invisible = true;
	public AdsController(
			final Activity activity,
			final String unit_id,
			final boolean test) {
		this.activity = activity;
		this.test = test;
		interstitial_ad = new InterstitialAd(activity);
		interstitial_ad.setAdUnitId(unit_id);
		request();
	}
	public AdsController(
			final Activity activity,
			final AdView view,
			final boolean test) {
		this.activity = activity;
		this.view = view;
		this.test = test;
		init();
	}
	public void setVisibility(final boolean invisible) {
		this.invisible = invisible;
	}
	private void request() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(test) {
					ad_request = new AdRequest.Builder().addTestDevice(PhoneManager.getDeviceID(activity)).build();
				}else {
					ad_request = new AdRequest.Builder().build();
				}
				interstitial_ad.loadAd(ad_request);
			}
		});
	}
	private void init() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				view.setAdListener(new AdsListener());
				if(test) {
					ad_request = new AdRequest.Builder().addTestDevice(PhoneManager.getDeviceID(activity)).build();
				}else {
					ad_request = new AdRequest.Builder().build();
				}
				view.loadAd(ad_request);
			}
		});
	}
	public void show() {
		if(view == null) {
			return;
		}
		show = true;
		General.executeAsync(new AdsLayout());
	}
	public void showInterstitialAd() {
		if(interstitial_ad == null) {
			return;
		}
		if(interstitial_ad.isLoaded()) {
			interstitial_ad.show();
		}
	}
	public void hide() {
		if(view == null) {
			return;
		}
		show = false;
		General.executeAsync(new AdsLayout());
	}
	public void onPause() {
		if(view == null) {
			return;
		}
		view.pause();
	}
	public void onResume() {
		if(view == null) {
			return;
		}
		view.resume();
	}
	public void onDestroy() {
		if(view == null) {
			return;
		}
		view.destroy();
	}
	public class AdsListener extends AdListener {
		public AdsListener() {}
		@Override
		public void onAdLoaded() {}
		@Override
		public void onAdFailedToLoad(int errorCode) {
			switch(errorCode) {
				case AdRequest.ERROR_CODE_INTERNAL_ERROR:
					break;
				case AdRequest.ERROR_CODE_INVALID_REQUEST:
					break;
				case AdRequest.ERROR_CODE_NETWORK_ERROR:
					break;
				case AdRequest.ERROR_CODE_NO_FILL:
					break;
			}
		}
		@Override
		public void onAdOpened() {}
		@Override
		public void onAdClosed() {}
		@Override
		public void onAdLeftApplication() {}
	}
	private class AdsLayout extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if(show) {
				view.setVisibility(View.VISIBLE);
				view.loadAd(ad_request);
			}else {
				if(invisible) {
					view.setVisibility(View.INVISIBLE);
				}else {
					view.setVisibility(View.GONE);
				}
				view.destroy();
			}
		}
	}
}