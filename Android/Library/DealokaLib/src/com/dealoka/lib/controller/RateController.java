package com.dealoka.lib.controller;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class RateController {
	public static void openMarket(final Context context) {
		Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
		Intent market = new Intent(Intent.ACTION_VIEW, uri);
		try {
			context.startActivity(market);
		}catch(ActivityNotFoundException e) {}
	}
}