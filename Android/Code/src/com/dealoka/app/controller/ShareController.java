package com.dealoka.app.controller;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;

import android.app.Activity;
import android.content.Intent;

public class ShareController {
	public static void share(final Activity activity, final String title, final String message) {
		Intent share_intent = new Intent();
		share_intent.setAction(Intent.ACTION_SEND);
		share_intent.putExtra(Intent.EXTRA_SUBJECT, title);
		share_intent.putExtra(Intent.EXTRA_TEXT, message);
		share_intent.setType("text/plain");
		activity.startActivity(Intent.createChooser(share_intent, DealokaApp.getAppContext().getResources().getText(R.string.text_label_share)));		
	}
}