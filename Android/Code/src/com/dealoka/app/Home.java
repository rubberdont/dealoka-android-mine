package com.dealoka.app;

import java.util.ArrayList;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.call.CallManager;
import com.dealoka.app.call.CallOfferRedeemWallet;
import com.dealoka.app.controller.DualController;
import com.dealoka.app.controller.PopupOffersController.PopupOffer;
import com.dealoka.app.general.BadInternetConnection;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.general.ReviewController;
import com.dealoka.app.model.BatchWallet;
import com.dealoka.app.model.OfferGeo;
import com.dealoka.app.model.call.ActionType;
import com.dealoka.app.model.call.OfferValidateCode;
import com.dealoka.app.model.call.TrackObject;
import com.dealoka.app.subs.SubsManager;
import com.dealoka.lib.General;
import com.dealoka.lib.calligraphy.CalligraphyContextWrapper;
import com.google.android.gms.analytics.GoogleAnalytics;

public class Home extends FragmentActivity {
	public static Home instance;
	public boolean start_directly = false;
	private DrawerLayout lay_drawer;
	private LinearLayout lay_menu;
	private Button btn_settings;
	private ImageView img_settings;
	private Button btn_wallet;
	private TextView lbl_wallet_point;
	private Button btn_wallet_point;
	private BadInternetConnection bic;
	private Wallet wallet_fragment;
	private Offers offers_fragment;
	private About about_fragment;
	private FragmentManager fm_main = null;
	private CallController offerValidateCodeCallController;
	private String review_offer_id = General.TEXT_BLANK;
	private boolean execute_batch_wallet = false;
	private boolean paused = false;
	private boolean opened = false;
	private boolean stopped = true;
	private boolean other_activity = false;
	public Home() {
		bic = new BadInternetConnection();
		GlobalController.exit_init();
		instance = this;
		start_directly = false;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		setInitial();
	}
	@Override
	protected void onResume() {
		super.onResume();
		DealokaApp.activityResumed();
		if(other_activity) {
			other_activity = false;
			return;
		}
		if(General.isNotNull(review_offer_id)) {
			showReview(review_offer_id);
			review_offer_id = General.TEXT_BLANK;
		}
		opened = true;
		SubsManager.Reconnect();
		CallManager.callTriggerSSP();
		executeBatchWallet();
		if(Wallet.instance != null) {
			Wallet.instance.SyncAll();
		}
		GlobalVariables.sensor_controller.load();
	}
	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		if(bic.isRunning()) {
			bic.stop();
		}
		if(other_activity) {
			return;
		}
		opened = false;
	}
	@Override
	protected void onDestroy() {
		instance = null;
		paused = false;
		opened = false;
		stopped = true;
		start_directly = true;
		other_activity = false;
		GlobalVariables.sensor_controller.unload();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		super.onDestroy();
		DealokaApp.activityDestroyed();
	}
	@Override
	protected void onPause() {
		super.onPause();
		paused = true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(bic.isRunning()) {
				bic.stop();
			}
			if(fm_main.getBackStackEntryCount() > 0) {
				fm_main.popBackStack();
			}else {
				if(GlobalController.exit(this)) {
					start_directly = true;
					GlobalVariables.time_session.Time(GlobalVariables.time);
				}
			}
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_MENU) {
			setSettings();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == Config.switch_user_activity_result) {
			if(data.getExtras().getBoolean("result")) {
				SubsManager.disconnectAllWhenItsSwitched();
				updateWalletPoint(CallOfferRedeemWallet.getWalletPoint());
				CallManager.callTriggerSSP();
			}
		}else if(resultCode == Activity.RESULT_OK && requestCode == Config.sim_card_activity_result) {
			CallManager.callTriggerSSP();
		}else {
			Fragment fragment = (Fragment)getSupportFragmentManager().findFragmentById(R.id.lay_child);
			if(fragment != null){
				fragment.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	public void setOpen(final boolean value) { opened = value; }
	public boolean isOpened() { return opened; }
	public void setPause(final boolean value) { paused = value; }
	public boolean isPaused() { return paused; }
	public void setStop(final boolean value) { stopped = value; }
	public boolean isStopped() { return stopped; }
	public void setOtherActivity(final boolean value) { other_activity = value; }
	public boolean isOtherActivity() { return other_activity; }
	public void setNewsFromNotification(final String message) {
		showNews(message);
	}
	public void setReviewFromNotification(final String offer_id) {
		showReview(offer_id);
	}
	public void resumeReviewFromNotification(final String offer_id) {
		review_offer_id = offer_id;
	}
	public void executeBatchWallet() {
		if(!GlobalController.isNetworkConnected(DealokaApp.getAppContext(), false)) {
			return;
		}
		if(GlobalVariables.query_controller.isBatchWalletExist()) {
			if(execute_batch_wallet) {
				return;
			}
			execute_batch_wallet = true;
			final ArrayList<BatchWallet> batch_wallet_data = GlobalVariables.query_controller.getBatchWallet();
			offerValidateCodeCallController = new CallController(new CallListener() {
				@Override
				public void onCallConnected() {
					General.executeAsync(new UpdateBatchWallet(batch_wallet_data));
				}
				@Override
				public void onCallFailed() {}
				@Override
				public void onCallReturned(String result) {}
			});
		}
	}
	public void pop() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(fm_main.getBackStackEntryCount() > 0) {
					fm_main.popBackStack();
				}
			}
		});
	}
	public void popBackStack() {
		if(fm_main.getBackStackEntryCount() > 1) {
			fm_main.popBackStack();
		}
	}
	public boolean isPopHeader() {
		return (fm_main.getBackStackEntryCount() > 0);
	}
	public void startLookupConnection() {
		bic.start();
	}
	public void stopLookupConnection() {
		bic.stop();
	}
	public void showNews(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				GlobalController.showNews(Home.this, message);
			}
		});
	}
	public void showReview(final String offer_id) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(offers_fragment != null) {
					ReviewController.open(offers_fragment, offer_id);
				}
			}
		});
	}
	public void checkDualSIM() {
		if(GlobalVariables.sim_session.DUALSIM()) {
			if(GlobalVariables.sim_session.INIT()) {
				runDualSelection();
			}else if(!GlobalVariables.imsi_list.contains(GlobalVariables.sim_session.IMSI())) {
				runDualSelection();
			}
		}else {
			CallManager.callTriggerSSP();
		}
	}
	public void setSettings() {
		if(lay_drawer.isDrawerOpen(lay_menu)) {
			closeDrawers();
		}else {
			openDrawers();
		}
	}
	public void closeDrawers() {
		lay_drawer.closeDrawers();
	}
	public void openDrawers() {
		lay_drawer.openDrawer(lay_menu);
	}
	public void showToast(final String value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				General.showToast(DealokaApp.getAppContext(), value, 1000);
			}
		});
	}
	public void openAbout() {
		if(about_fragment != null) {
			return;
		}
		if(offers_fragment == null) {
			return;
		}
		about_fragment = new About();
		offers_fragment.getFragmentManager().beginTransaction().add(R.id.lay_child, about_fragment, About.AboutFragment).addToBackStack(null).commit();
	}
	public void openWallet() {
		clearBackStack(false);
		doWallet();
	}
	public void confirmValidation(final String offer_id) {
		clearBackStack(true);
		if(Wallet.instance != null) {
			Wallet.instance.Update(offer_id);
		}
		updateWalletPoint(CallOfferRedeemWallet.getWalletPoint());
	}
	public void confirmExpired() {
		clearBackStack(true);
		if(Wallet.instance != null) {
			Wallet.instance.SyncAll();
		}
		updateWalletPoint(CallOfferRedeemWallet.getWalletPoint());
	}
	public void updateWalletPoint(final int value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lbl_wallet_point.setText(General.TEXT_BLANK + value);
			}
		});
	}
	public void closeAboutFragment() {
		about_fragment = null;
	}
	public void closeWalletFragment() {
		wallet_fragment = null;
	}
	public void openDownload(OfferGeo offer){
		Bundle args = new Bundle();
		args.putString("from", PopupOffer.TAG);
		args.putSerializable("offer", offer);
		Download download_fragment = new Download();
		download_fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction().add(R.id.lay_child, download_fragment, Download.DownloadFragment).addToBackStack(null).commitAllowingStateLoss();
	}
	private void runDualSelection() {
		if(DualController.instance != null) {
			return;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				GlobalController.openSimCard(Home.this, General.TEXT_BLANK);
			}
		});
	}
	private void setInitial() {
		checkDualSIM();
		fm_main = getSupportFragmentManager();
		lay_drawer = (DrawerLayout)findViewById(R.id.lay_drawer);
		lay_menu = (LinearLayout)findViewById(R.id.lay_menu);
		btn_wallet = (Button)findViewById(R.id.btn_wallet);
		btn_settings = (Button)findViewById(R.id.btn_settings);
		img_settings = (ImageView)findViewById(R.id.img_settings);
		lbl_wallet_point = (TextView)findViewById(R.id.lbl_wallet_point);
		btn_wallet_point = (Button)findViewById(R.id.btn_wallet_point);
		setEventListener();
		lay_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		lay_drawer.closeDrawers();
		new LeftMenu();
		General.setAlpha(lbl_wallet_point, 0.9f);
		CallOfferRedeemWallet.init();
		openOffers();
		if(getIntent().getExtras() != null) {
			if(General.isNotNull(getIntent().getExtras().getString(Config.message_notif))) {
				showNews(getIntent().getExtras().getString(Config.message_notif));
			}else if(General.isNotNull(getIntent().getExtras().getString(Config.review_notif))) {
				showReview(getIntent().getExtras().getString(Config.review_notif));
			}else if(getIntent().getExtras().getSerializable(Config.popup_notif) != null){
				OfferGeo offerGeo = (OfferGeo) getIntent().getExtras().getSerializable(Config.popup_notif);
				openDownload(offerGeo);
			}
		}
		updateWalletPoint(CallOfferRedeemWallet.getWalletPoint());
	}
	private void setEventListener() {
		lay_drawer.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int arg0) {
				if(arg0 == DrawerLayout.STATE_SETTLING) {
					if(!lay_drawer.isDrawerOpen(lay_menu)) {
						if(General.isValidOS()) {
							drawerOpened();
						}
					}else {
						if(General.isValidOS()) {
							drawerClosed();
						}
					}
		        }
			}
			@Override
			public void onDrawerSlide(View arg0, float arg1) {}
			@Override
			public void onDrawerOpened(View arg0) {
				lay_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			}
			@Override
			public void onDrawerClosed(View arg0) {
				lay_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			}
		});
		btn_settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setSettings();
			}
		});
		btn_wallet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doWallet();
			}
		});
		btn_wallet_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doWallet();
			}
		});
	}
	private void openOffers() {
		offers_fragment = new Offers();
		if(fm_main.findFragmentByTag(Offers.OffersFragment) == null) {
			clearBackStack(false);
			fm_main.beginTransaction()
			.replace(R.id.lay_main, offers_fragment, Offers.OffersFragment)
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.commit();
		}
	}
	private void doWallet() {
		if(offers_fragment == null) {
			return;
		}
		if(wallet_fragment != null) {
			return;
		}
		closeDrawers();
		wallet_fragment = new Wallet();
		offers_fragment.getFragmentManager().beginTransaction().add(R.id.lay_child, wallet_fragment, Wallet.WalletFragment).addToBackStack(null).commit();
	}
	private void clearBackStack(final boolean left_one) {
		if(bic.isRunning()) {
			bic.stop();
		}
		if(left_one) {
			for(int counter = 0; counter < fm_main.getBackStackEntryCount() - 1; counter++) {
				fm_main.popBackStack();
			}
		}else {
			fm_main.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void drawerOpened() {
		final ObjectAnimator object_animator = ObjectAnimator.ofFloat(img_settings, "x",
				0, -(img_settings.getWidth() / 2)).setDuration(100);
		object_animator.setRepeatCount(0);
		object_animator.setRepeatMode(ValueAnimator.REVERSE);
		object_animator.setInterpolator(new AccelerateInterpolator(2f));
		final AnimatorSet anim = new AnimatorSet();
		((AnimatorSet)anim).play(object_animator);
		anim.start();
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void drawerClosed() {
		final ObjectAnimator object_animator = ObjectAnimator.ofFloat(img_settings, "x",
				-(img_settings.getWidth() / 2), 0).setDuration(100);
		object_animator.setRepeatCount(0);
		object_animator.setRepeatMode(ValueAnimator.REVERSE);
		object_animator.setInterpolator(new AccelerateInterpolator(2f));
		final AnimatorSet anim = new AnimatorSet();
		((AnimatorSet)anim).play(object_animator);
		anim.start();
	}
	private class UpdateBatchWallet extends AsyncTask<Void, Void, Void> {
		private ArrayList<BatchWallet> batch_wallet_data;
		public UpdateBatchWallet(final ArrayList<BatchWallet> batch_wallet_data) {
			this.batch_wallet_data = batch_wallet_data;
		}
		@Override
		protected void onPostExecute(Void result) {
			execute_batch_wallet = false;
		}
		@Override
		protected void onPreExecute() {}
		@Override
		protected Void doInBackground(Void... params) {
			int size = batch_wallet_data.size();
			while(size > 0) {
				size--;
				try {
					Thread.sleep(5000);
					final OfferValidateCode offer_validate_code = new OfferValidateCode(
							batch_wallet_data.get(size).offer_id,
							batch_wallet_data.get(size).user_token,
							batch_wallet_data.get(size).md5hash,
							batch_wallet_data.get(size).unique_code,
							new TrackObject(
									batch_wallet_data.get(size).category,
									GlobalVariables.longitude,
									GlobalVariables.latitude,
									GlobalVariables.user_session.GetToken(),
									batch_wallet_data.get(size).offer_id,
									ActionType.VALIDATE,
									batch_wallet_data.get(size).merchant_id,
									GlobalVariables.user_session.GetGender(),
									new TrackObject.BTSObject(
											GlobalVariables.longitude,
											GlobalVariables.latitude,
											GlobalVariables.orientation,
											GlobalVariables.signal,
											GlobalVariables.cid,
											GlobalVariables.lac,
											GlobalVariables.mcc,
											GlobalVariables.mnc)));
					offerValidateCodeCallController.callOfferValidateCode(offer_validate_code);
					GlobalVariables.query_controller.updateBatchWallet(batch_wallet_data.get(size).offer_redeem_wallet_id);
				}catch(InterruptedException ex) {}
			}
			return null;
		}
	}
}