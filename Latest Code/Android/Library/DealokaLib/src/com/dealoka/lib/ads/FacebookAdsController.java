package com.dealoka.lib.ads;

import android.content.Context;
import android.widget.FrameLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;

public class FacebookAdsController {
	private AdView ad_view;
	private InterstitialAd interstitial_ad;
	private NativeAd native_ad;
	private FacebookAdsCallback facebook_ads_callback;
	public interface FacebookAdsCallback {
		public abstract void didNativeAdLoaded(final NativeAd native_ad);
	}
	public FacebookAdsController(
			final Context context,
			final FrameLayout lay_ads,
			final String placement_id,
			final AdSize ad_size) {
		ad_view = new AdView(context, placement_id, ad_size);
		ad_view.setAdListener(ad_listener);
		lay_ads.addView(ad_view);
		ad_view.loadAd();
	}
	public FacebookAdsController(
			final Context context,
			final String placement_id,
			final boolean interstitial) {
		if(interstitial) {
			interstitial_ad = new InterstitialAd(context, placement_id);
			interstitial_ad.setAdListener(interstitial_ad_listener);
		}else {
			native_ad = new NativeAd(context, placement_id);
			native_ad.setAdListener(ad_listener);
		}
	}
	public void showInterstitialAd() {
		if(interstitial_ad != null) {
			if(interstitial_ad.isAdLoaded()) {
				interstitial_ad.show();
			}
		}
	}
	public void loadNativeAd(final FacebookAdsCallback facebook_ads_callback) {
		this.facebook_ads_callback = facebook_ads_callback;
		if(native_ad != null) {
			native_ad.loadAd();
		}
	}
	public void unloadNativeAd() {
		facebook_ads_callback = null;
	}
	public void closeIntersitialAd() {
		if(interstitial_ad != null) {
			interstitial_ad.destroy();
		}
		interstitial_ad = null;
	}
	private AdListener ad_listener = new AdListener() {
		@Override
		public void onError(Ad arg0, AdError arg1) {}
		@Override
		public void onAdLoaded(Ad arg0) {
			if(native_ad != null) {
				if(facebook_ads_callback != null) {
					facebook_ads_callback.didNativeAdLoaded(native_ad);
				}
			}
		}
		@Override
		public void onAdClicked(Ad arg0) {}
	};
	private InterstitialAdListener interstitial_ad_listener = new InterstitialAdListener() {
		@Override
		public void onError(Ad arg0, AdError arg1) {}
		@Override
		public void onAdLoaded(Ad arg0) {}
		@Override
		public void onAdClicked(Ad arg0) {}
		@Override
		public void onInterstitialDisplayed(Ad arg0) {}
		@Override
		public void onInterstitialDismissed(Ad arg0) {}
	};
}