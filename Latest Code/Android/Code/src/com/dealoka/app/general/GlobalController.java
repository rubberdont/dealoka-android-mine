package com.dealoka.app.general;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.dealoka.lib.General;
import com.dealoka.lib.manager.PhoneManager;
import com.dealoka.app.DealokaApp;
import com.dealoka.app.Home;
import com.dealoka.app.R;
import com.dealoka.app.activity.Connect;
import com.dealoka.app.activity.Tutorial;
import com.dealoka.app.controller.AlertController;
import com.dealoka.app.controller.DualController;
import com.dealoka.app.controller.QuestionController;
import com.dealoka.app.controller.SendMessageController;
import com.dealoka.app.controller.SDKSpecialCodeController;
import com.dealoka.app.controller.SpecialCodeController;
import com.dealoka.app.controller.TCController;
import com.dealoka.app.model.OfferGeo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class GlobalController {
	private static boolean exit_pressed = false;
	private static Timer finish_timer = null;
	public static void pop() {
		if(Home.instance != null) {
			Home.instance.pop();
		}
	}
	public static boolean isLocationRetrieved() {
		if(GlobalVariables.latitude != 0.0f && GlobalVariables.longitude != 0.0f) {
			return true;
		}
		if(GlobalVariables.feature_session.isLocation()) {
			GlobalVariables.latitude = GlobalVariables.feature_session.Latitude();
			GlobalVariables.longitude = GlobalVariables.feature_session.Longitude();
			return true;
		}
		GlobalController.loadLocationFromAssets();
		return true;
	}
	public static void openSettingsProvider(final Activity activity) {
		activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Config.provider_settings_activity_result);
	}
	public static String getVersion() {
		String version = General.TEXT_BLANK;
		try {
			version = DealokaApp.getAppContext().getPackageManager().getPackageInfo(DealokaApp.getAppContext().getPackageName(), 0).versionName;
		}catch(NameNotFoundException ex) {}
		return version;
	}
	public static void setFeatureLocation(final float latitude, final float longitude) {
		GlobalVariables.feature_session.Latitude(latitude);
		GlobalVariables.feature_session.Longitude(longitude);
	}
	@SuppressLint("UseValueOf")
	public static void loadLocationFromAssets() {
		try {
			final String content = General.readDataFromAssets(DealokaApp.getAppContext(), "location.json");
			JSONObject json = new JSONObject(content);
			GlobalVariables.latitude = new Double(json.getDouble("latitude")).floatValue();
			GlobalVariables.longitude = new Double(json.getDouble("longitude")).floatValue();
		}catch(JSONException ex) {}
	}
	public static boolean isNetworkConnected(final Context context, final boolean show_toast) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni == null) {
			if(show_toast) {
				General.showToast(DealokaApp.getAppContext(), DealokaApp.getAppContext().getString(R.string.text_err_connection_lost), 1000);
			}
			return false;
		}else {
			return true;
		}
	}
	public static String setDecimalFormat(final double value) {
		return new DecimalFormat("#.##").format(value);
	}
	public static String getHHMMSSFromTimeStamp(final long timestamp) {
		final int SECOND = 1000;
		final int MINUTE = 60 * SECOND;
		final int HOUR = 60 * MINUTE;
		final int DAY = 24 * HOUR;
		long ts = timestamp;
		StringBuffer text = new StringBuffer(General.TEXT_BLANK);
		if(ts > DAY) {
			text.append(ts / DAY).append(" " + DealokaApp.getAppContext().getString(R.string.text_label_day) + " ");
			ts %= DAY;
		}
		if(ts > HOUR) {
			text.append(String.format("%02d", ts / HOUR)).append(" : ");
			ts %= HOUR;
		}else {
			text.append("00 : ");
		}
		if(ts > MINUTE) {
			text.append(String.format("%02d", ts / MINUTE)).append(" : ");
			ts %= MINUTE;
		}else {
			text.append("00 : ");
		}
		if(ts > SECOND) {
			text.append(String.format("%02d", ts / SECOND));
			ts %= SECOND;
		}else {
			text.append("00");
		}
		return text.toString();
	}
	public static void showAlert(final Activity activity, final String message) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, AlertController.class);
		intent.putExtra("message", message);
		activity.startActivity(intent);
	}
	public static void showAlert(final Activity activity, final String message, final int request_code) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, AlertController.class);
		intent.putExtra("message", message);
		intent.putExtra("request_code", request_code);
		activity.startActivityForResult(intent, request_code);
	}
	public static void showAlert(final String message) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.setOtherActivity(true);
		final Intent intent = new Intent(Home.instance, AlertController.class);
		intent.putExtra("message", message);
		Home.instance.startActivity(intent);
	}
	public static void showAlert(final String message, final int request_code) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.setOtherActivity(true);
		final Intent intent = new Intent(Home.instance, AlertController.class);
		intent.putExtra("message", message);
		intent.putExtra("request_code", request_code);
		Home.instance.startActivityForResult(intent, request_code);
	}
	public static void showToast(final int resource_id) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				General.showToast(DealokaApp.getAppContext(), DealokaApp.getAppContext().getString(resource_id), Config.toast_delay);
			}
		});
	}
	public static void showToast(final String message) {
		if(Home.instance == null) {
			return;
		}
		Home.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				General.showToast(DealokaApp.getAppContext(), message, Config.toast_delay);
			}
		});
	}
	public static void showNews(final Context context, final String message) {
		NewsController.show(context, General.TEXT_BLANK, message);
	}
	public static void openWeb(final Activity activity, final String url) {
		String uri = url;
		if(!url.startsWith("http://") && !url.startsWith("https://")) {
			uri = "http://" + url;
		}
		try {
			final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			activity.startActivity(intent);
		}catch(ActivityNotFoundException ex) {}
	}
	public static void openTC(final Activity activity) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, TCController.class);
		activity.startActivityForResult(intent, Config.tc_activity_result);
	}
	public static void openSendMessage(final Activity activity, final String offer_id) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, SendMessageController.class);
		intent.putExtra("offer_id", offer_id);
		activity.startActivityForResult(intent, Config.send_message_activity_result);
	}
	public static void openSimCard(final Activity activity, final String caption) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, DualController.class);
		intent.putExtra("caption", caption);
		activity.startActivityForResult(intent, Config.sim_card_activity_result);
	}
	public static void openSDKSpecialCode(final Activity activity) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, SDKSpecialCodeController.class);
		activity.startActivityForResult(intent, Config.sdk_special_code_activity_result);
	}
	public static void openSpecialCode(final Activity activity) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, SpecialCodeController.class);
		activity.startActivityForResult(intent, Config.special_code_activity_result);
	}
	public static void openQuestion(final Activity activity, final String message) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, QuestionController.class);
		intent.putExtra("message", message);
		activity.startActivityForResult(intent, Config.question_activity_result);
	}
	public static void openQuestion(final Activity activity, final String message, final String tac) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(activity, QuestionController.class);
		intent.putExtra("message", message);
		intent.putExtra("value", tac);
		intent.putExtra("default", 1);
		activity.startActivityForResult(intent, Config.question_activity_result);
	}
	public static void openTutorial(final Activity activity) {
		if(Home.instance != null) {
			Home.instance.setOtherActivity(true);
		}
		final Intent intent = new Intent(DealokaApp.getAppContext(), Tutorial.class);
		activity.startActivity(intent);
	}
	public static void sendNewsNotification(final String title, final String message) {
		Bitmap bitmap = null;
		try {
			BufferedInputStream buf = new BufferedInputStream(DealokaApp.getAppContext().getAssets().open("ic_notification_big.png"));
			bitmap = BitmapFactory.decodeStream(buf);
			buf.close();
			if(General.isValidOS()) {
				bitmap = scaleNotif(bitmap);
			}
		}catch(IOException ex) {}
		GlobalVariables.notification_manager = (NotificationManager)DealokaApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(DealokaApp.getAppContext(), Connect.class);
		intent.putExtra(Config.message_notif, message);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent content_intent = PendingIntent.getActivity(
				DealokaApp.getAppContext(),
				99,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(DealokaApp.getAppContext())
		.setSmallIcon(R.drawable.ic_notification)
		.setTicker(title)
		.setContentTitle(DealokaApp.getAppContext().getString(R.string.app_name))
		.setContentText(message)
		.setLargeIcon(bitmap)
		.setSound(Uri.parse("android.resource://" + DealokaApp.getAppContext().getPackageName() + "/" + R.raw.notification));
		NotificationCompat.BigTextStyle big_text_style = new NotificationCompat.BigTextStyle();
		big_text_style.setBigContentTitle(DealokaApp.getAppContext().getString(R.string.app_name));
		big_text_style.bigText(message);
		notification_builder.setStyle(big_text_style);
		notification_builder.setContentIntent(content_intent);
		GlobalVariables.notification_manager.notify(99, notification_builder.build());
	}
	public static void sendReviewNotification(final String offer_id, final String content) {
		Config.review_notification++;
		Bitmap bitmap = null;
		try {
			BufferedInputStream buf = new BufferedInputStream(DealokaApp.getAppContext().getAssets().open("ic_notification_big.png"));
			bitmap = BitmapFactory.decodeStream(buf);
			buf.close();
			if(General.isValidOS()) {
				bitmap = scaleNotif(bitmap);
			}
		}catch(IOException ex) {}
		GlobalVariables.notification_manager = (NotificationManager)DealokaApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(DealokaApp.getAppContext(), Connect.class);
		intent.putExtra(Config.review_notif, offer_id);
		intent.putExtra("notif", Config.review_notification);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent content_intent = PendingIntent.getActivity(
				DealokaApp.getAppContext(),
				Config.review_notification,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(DealokaApp.getAppContext())
		.setSmallIcon(R.drawable.ic_notification)
		.setTicker(DealokaApp.getAppContext().getString(R.string.text_message_review))
		.setContentTitle(DealokaApp.getAppContext().getString(R.string.app_name))
		.setContentText(content)
		.setLargeIcon(bitmap);
		NotificationCompat.BigTextStyle big_text_style = new NotificationCompat.BigTextStyle();
		big_text_style.setBigContentTitle(DealokaApp.getAppContext().getString(R.string.app_name));
		big_text_style.bigText(content);
		notification_builder.setStyle(big_text_style);
		notification_builder.setContentIntent(content_intent);
		GlobalVariables.notification_manager.notify(Config.review_notification, notification_builder.build());
	}
	public static void sendOffersNotification(Context context, Intent intent, OfferGeo offer) {
		Bitmap bitmap = null;
		String title = "SPECIAL promo from " + offer.c_merchant_rec.merchant_name;
		String message = offer.c_offer_rec.name;
		try {
			BufferedInputStream buf = new BufferedInputStream(context.getAssets().open("ic_notification_big.png"));
			bitmap = BitmapFactory.decodeStream(buf);
			buf.close();
			if(General.isValidOS()) {
				bitmap = scaleNotif(bitmap);
			}
		}catch(IOException ex) {}
		GlobalVariables.notification_manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent content_intent = PendingIntent.getActivity(
				context,
				84,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_notification)
		.setTicker(title)
		.setContentTitle(context.getString(R.string.app_name))
		.setContentText(message)
		.setLargeIcon(bitmap)
		.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification));
		NotificationCompat.BigTextStyle big_text_style = new NotificationCompat.BigTextStyle();
		big_text_style.setBigContentTitle(context.getString(R.string.app_name));
		big_text_style.bigText(message);
		notification_builder.setStyle(big_text_style);
		notification_builder.setContentIntent(content_intent);
		GlobalVariables.notification_manager.notify(84, notification_builder.build());
	}
	public static void closeNotification(final int notif_id) {
		if(GlobalVariables.notification_manager == null) {
			GlobalVariables.notification_manager = (NotificationManager)DealokaApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
		}
		GlobalVariables.notification_manager.cancel(notif_id);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static Bitmap scaleNotif(final Bitmap bitmap) {
		final int height = (int)DealokaApp.getAppContext().getResources().getDimension(android.R.dimen.notification_large_icon_height);
		final int width = (int)DealokaApp.getAppContext().getResources().getDimension(android.R.dimen.notification_large_icon_width);
		return Bitmap.createScaledBitmap(bitmap, width, height, false);
	}
	public static void exit_init() {
		exit_pressed = false;
	}
	public static boolean exit(final Activity activity) {
		if(!exit_pressed) {
			exit_pressed = true;
			finish_timer = new Timer();
			finish_timer.schedule(new FinishTimer(), 2000, 2000);
			General.showToast(DealokaApp.getAppContext(), DealokaApp.getAppContext().getString(R.string.text_message_exit), 1000);
			return false;
		}else {
			activity.finish();
			return true;
		}
	}
	public static ArrayList<ImageView> setDrawPoint(RelativeLayout relative_layout, final Context context, final int max) {
		relative_layout.removeAllViews();
		ArrayList<ImageView> img_view_list = new ArrayList<ImageView>();
		int start = 0;
		RelativeLayout lay_point = new RelativeLayout(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		lay_point.setLayoutParams(params);
		final int size = (int)(context.getResources().getDimension(R.dimen.TopPoint) / context.getResources().getDisplayMetrics().density);
		for(int counter = 1; counter <= max; counter++) {
			ImageView img_view = new ImageView(context);
			img_view.setImageResource(R.drawable.point);
			RelativeLayout.LayoutParams point_params = new RelativeLayout.LayoutParams(size / 2, size / 2);
			point_params.setMargins(start, 0, 0, 0);
			lay_point.addView(img_view, point_params);
			img_view_list.add(img_view);
			start += size;
		}
		relative_layout.addView(lay_point);
		return img_view_list;
	}
	public static Bitmap encodeAsBitmap(
    		final String contents,
    		final BarcodeFormat format,
    		final int img_width,
    		final int img_height) throws WriterException {
		String contents_to_encode = contents;
		if(contents_to_encode == null) {
			return null;
		}
		Map<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contents_to_encode);
		if(encoding != null) {
			hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result;
		try {
			result = writer.encode(contents_to_encode, format, img_width, img_height, hints);
		}catch(IllegalArgumentException ex) {
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for(int y = 0; y < height; y++) {
			int offset = y * width;
			for(int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? Config.BLACK : Config.WHITE;
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	public static DisplayImageOptions getOption(final boolean cache_memory, final boolean cache_disk) {
		return new DisplayImageOptions.Builder()
			.cacheInMemory(cache_memory)
			.cacheOnDisk(cache_disk)
			.build();
	}
	public static DisplayImageOptions getOptionWithNoImage() {
		return new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.bg_no_image)
			.showImageForEmptyUri(R.drawable.bg_no_image)
			.showImageOnFail(R.drawable.bg_no_image)
			.cacheInMemory(false)
			.cacheOnDisk(true)
			.build();
	}
	public static void getSIMDevice(final Context context) {
		if(PhoneManager.isDualSIM(context)) {
			GlobalVariables.sim_session.DUALSIM(true);
			GlobalVariables.device_id_list = PhoneManager.getDeviceIDDual(context);
			GlobalVariables.imsi_list = PhoneManager.getIMSIDual(context);
			GlobalVariables.operator_list = PhoneManager.getOperatorDual(context);
			GlobalVariables.operator_name_list = PhoneManager.getOperatorNameDual(context);
			GlobalVariables.device_id = General.TEXT_BLANK;
			GlobalVariables.imsi = General.TEXT_BLANK;
			GlobalVariables.operator = General.TEXT_BLANK;
			GlobalVariables.operator_name = General.TEXT_BLANK;
			if(!General.isNotNull(GlobalVariables.sim_session.DEVICEID())) {
				GlobalVariables.sim_session.DEVICEID(GlobalVariables.device_id_list.get(0));
				GlobalVariables.sim_session.IMSI(GlobalVariables.imsi_list.get(0));
			}
		}else {
			GlobalVariables.sim_session.DUALSIM(false);
			GlobalVariables.device_id_list = new ArrayList<String>();
			GlobalVariables.imsi_list = new ArrayList<String>();
			GlobalVariables.operator_list = new ArrayList<String>();
			GlobalVariables.operator_name_list = new ArrayList<String>();
			GlobalVariables.device_id = PhoneManager.getDeviceID(context);
			GlobalVariables.imsi = PhoneManager.getSIMIMSI(context);
			GlobalVariables.operator = PhoneManager.getOperator(context);
			GlobalVariables.operator_name = PhoneManager.getOperatorName(context);
		}
		String operator = GlobalVariables.operator;
		if(GlobalVariables.operator_list.size() > 0) {
			operator = GlobalVariables.operator_list.get(0);
		}
		if(operator.length() < 3) {
			return;
		}
		String mcc = operator.substring(0, 3);
		String mnc = operator.substring(3);
		if(General.isNumber(mcc) && General.isNumber(mnc)) {
			GlobalVariables.mcc = Integer.parseInt(mcc);
			GlobalVariables.mnc = Integer.parseInt(mnc);
		}
	}
	public static String getDeviceID() {
		if(GlobalVariables.sim_session.DUALSIM()) {
			String device_id = GlobalVariables.sim_session.DEVICEID();
			if(!General.isNotNull(device_id)) {
				device_id = PhoneManager.getDeviceID(DealokaApp.getAppContext());
			}
			return device_id;
		}else {
			return GlobalVariables.device_id;
		}
	}
	public static void backupDatabase() {
		final String destionation_path = General.getStorageDirectory(DealokaApp.getAppContext()) + File.separatorChar + "Download" + File.separatorChar + Config.sql_file + ".bak";
		try {
			final InputStream input = new FileInputStream(Config.sql_path + Config.sql_file);
			final OutputStream output = new FileOutputStream(destionation_path);
			byte[] buffer = new byte[1024];
			int length;
			while((length = input.read(buffer))>0) {
				output.write(buffer, 0, length);
			}
			output.flush();
			output.close();
			input.close();
			GlobalController.showToast(destionation_path);
		}catch(IOException ex) {
			GlobalController.showToast(ex.getMessage());
		}
	}
	private static String guessAppropriateEncoding(CharSequence contents) {
		for(int i = 0; i < contents.length(); i++) {
			if(contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}
	private static class FinishTimer extends TimerTask {
		@Override
		public void run() {
			if(exit_pressed) {
				exit_pressed = !exit_pressed;
				finish_timer.cancel();
				finish_timer = null;
			}
		}
	}
}