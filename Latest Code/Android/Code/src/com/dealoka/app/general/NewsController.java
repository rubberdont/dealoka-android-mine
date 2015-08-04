package com.dealoka.app.general;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class NewsController {
	private static AlertDialog alert_dialog;
	public static void show(final Context context, final String title, final String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder
		.setMessage(message)
		.setCancelable(false)
		.setNegativeButton(DealokaApp.getAppContext().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				unshow();
			}
		});
		alert_dialog = builder.create();
		alert_dialog.show();
	}
	private static void unshow() {}
}