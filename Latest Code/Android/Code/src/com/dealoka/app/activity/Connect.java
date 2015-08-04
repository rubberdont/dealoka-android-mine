package com.dealoka.app.activity;

import com.dealoka.lib.General;
import com.dealoka.app.Home;
import com.dealoka.app.Main;
import com.dealoka.app.Review;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Connect extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setInitial();
		finish();
	}
	private void setInitial() {
		if(getIntent().getExtras() == null) {
			showApp();
			return;
		}
		if(getIntent().getExtras() != null) {
			if(General.isNotNull(getIntent().getExtras().getString(Config.message_notif))) {
				GlobalController.closeNotification(99);
				if(Home.instance == null) {
					Intent intent = new Intent(this, Main.class);
					intent.putExtra(Config.message_notif, getIntent().getExtras().getString(Config.message_notif));
					startActivity(intent);
				}else {
					if(Home.instance.start_directly) {
						Intent intent = new Intent(this, Main.class);
						intent.putExtra(Config.message_notif, getIntent().getExtras().getString(Config.message_notif));
						startActivity(intent);
					}else {
						Home.instance.setNewsFromNotification(getIntent().getExtras().getString(Config.message_notif));
					}
				}
			}else if(General.isNotNull(getIntent().getExtras().getString(Config.review_notif))) {
				if(Review.instance != null) {
					showApp();
					return;
				}
				GlobalController.closeNotification(getIntent().getExtras().getInt("notif"));
				if(Home.instance == null) {
					Intent intent = new Intent(this, Main.class);
					intent.putExtra(Config.review_notif, getIntent().getExtras().getString(Config.review_notif));
					startActivity(intent);
				}else {
					if(Home.instance.start_directly) {
						Intent intent = new Intent(this, Main.class);
						intent.putExtra(Config.review_notif, getIntent().getExtras().getString(Config.review_notif));
						startActivity(intent);
					}else {
						if(Home.instance.isOpened()) {
							Home.instance.setReviewFromNotification(getIntent().getExtras().getString(Config.review_notif));
						}else {
							Home.instance.resumeReviewFromNotification(getIntent().getExtras().getString(Config.review_notif));
						}
					}
				}
			}
		}else {
			showApp();
		}
	}
	private void showApp() {
		GlobalController.closeNotification(99);
		if(Home.instance == null) {
			Intent intent = new Intent(this, Main.class);
			startActivity(intent);
		}else {
			if(Home.instance.start_directly) {
				Intent intent = new Intent(this, Main.class);
				startActivity(intent);
			}
		}
	}
}