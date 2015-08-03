package com.dealoka.app.controller;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class RateController {
	public interface RateCallback {
		public abstract void didRate();
		public abstract void didNoRate();
	}
	public static void show(final Context context, final RateCallback rate_callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(DealokaApp.getAppContext().getString(R.string.text_message_title_rate));
		builder.setMessage(DealokaApp.getAppContext().getString(R.string.text_message_rate));
		builder.setCancelable(false);
		builder.setPositiveButton(DealokaApp.getAppContext().getString(R.string.button_rate_now), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openMarket(context);
				rate_callback.didRate();
			}
		});
		builder.setNegativeButton(DealokaApp.getAppContext().getString(R.string.button_rate_later), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				rate_callback.didNoRate();
			}
		});
		builder.create().show();
	}
	public static void openMarket(final Context context) {
		Uri uri = Uri.parse("market://details?id=" + DealokaApp.getAppContext().getPackageName());
		Intent market = new Intent(Intent.ACTION_VIEW, uri);
		try {
			context.startActivity(market);
		}catch(ActivityNotFoundException e) {}
	}
}