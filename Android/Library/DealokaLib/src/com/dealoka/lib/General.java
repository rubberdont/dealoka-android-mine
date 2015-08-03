package com.dealoka.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.dealoka.lib.badge.Badge;
import com.dealoka.lib.control.Alert;
import com.dealoka.lib.control.Loading;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class General {
	public static final String TEXT_BLANK = "";
	private static ProgressDialog loading;
	private static Toast toast;
	private static WakeLock wake_lock;
	public static void restart(final Context context, int delay) {
		if(delay == 0) {
			delay = 1;
		}
		Intent restart_intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		restart_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0, restart_intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
		General.kill();
	}
	public static void kill() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	public static boolean isValidOS() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return true;
		}else {
			return false;
		}
	}
	public static boolean isWidgetExisted(final Context context, @SuppressWarnings("rawtypes") final Class object) {
		final int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
				new ComponentName(context, object));
		return (appWidgetIds.length > 0);
	}
	public static void executeAsync(final AsyncTask<Void, Void, Void> async_task) {
		if(General.isValidOS()) {
			executeValidAsync(async_task);
		}else {
			executeNonValidAsync(async_task);
		}
	}
	public static void closeKeyboard(final Activity activity) {
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		View v = activity.getCurrentFocus();
		if(v == null) return;
		imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	public static boolean isEmailValid(final String text) {
		final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
				"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
				"\\@" +
				"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
				"(" +
				"\\." +
				"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
				")+");
		return EMAIL_ADDRESS_PATTERN.matcher(text).matches();
	}
	public static void setPasswordEditText(EditText edit_text) {
		edit_text.setTransformationMethod(new PasswordTransformationMethod());
	}
	public static boolean isNotNull(final String value) {
		if(value == null) {
			return false;
		}
		if(value.trim().equals(TEXT_BLANK)) {
			return false;
		}else {
			return true;
		}
	}
	public static boolean isLengthValid(final String value, final long length) {
		if(!isNotNull(value)) {
			return false;
		}
		if(value.length() >= length) {
			return true;
		}else {
			return false;
		}
	}
	public static boolean isNumber(final String value) {
		final String regex = "^[0-9]*$";
		if(value.matches(regex)) {
			return true;
		}else {
			return false;
		}
	}
	public static boolean isExternalStorageAvailable() {
		final String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void copyTextToClipboard(final Context context, final String title, final String value) {
		@SuppressWarnings("static-access")
		ClipboardManager clipboard = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText(title, value);
		clipboard.setPrimaryClip(clip);
	}
	public static String getStorageDirectory(final Context context) {
		if(General.isExternalStorageAvailable()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}else {
			return context.getCacheDir().getAbsolutePath();
		}
	}
	@SuppressLint("DefaultLocale")
	public static String setFloatFormat(final float value, final int precision) {
		return String.format("%." + precision + "f", value);
	}
	@SuppressLint("DefaultLocale")
	public static String setFloatFormat(final double value, final int precision) {
		return String.format("%." + precision + "f", value);
	}
	public static void setAlpha(final View view, final float value) {
		AlphaAnimation alpha = new AlphaAnimation(value, value);
		alpha.setFillAfter(true);
		view.startAnimation(alpha);
	}
	public static void setBadgeCount(final Context context, final int count, final String class_name) {
		if(Badge.isBadgingSupported(context)) {
			Badge badge = Badge.getBadge(context, class_name);
			if(badge != null) {
				badge.mBadgeCount = count;
				badge.update(context);
			}
		}else {
			Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
			intent.putExtra("badge_count", count);
			intent.putExtra("badge_count_package_name", context.getPackageName());
			intent.putExtra("badge_count_class_name", class_name);
			context.sendBroadcast(intent);
		}
	}
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static void getSignature(final Context context) {
		try {
			final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			for(Signature signature : info.signatures) {
				final MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Logger.d("SHA String: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		}catch(NameNotFoundException ex) {
		}catch(NoSuchAlgorithmException ex) {}
	}
	public static int[] getIntegerArray(final Context context, final int array_resource_id) {
		final TypedArray ar = context.getResources().obtainTypedArray(array_resource_id);
		final int len = ar.length();
		final int[] res_ids = new int[len];
		for(int i = 0; i < len; i++)
			res_ids[i] = ar.getResourceId(i, 0);
		ar.recycle();
		return res_ids;
	}
	public static void registerC2dm(final Context context, final String gcm_id) {
		Intent registration_intent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registration_intent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		registration_intent.putExtra("sender", gcm_id);
		context.startService(registration_intent);
	}
	public static void unregisterC2dm(final Context context) {
		Intent unregistration_intent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		unregistration_intent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		context.startService(unregistration_intent);
	}
	public static int getScreenWidth(final Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	public static void vibrate(final Context context, final int time) {
		Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(time);
	}
	@SuppressWarnings("rawtypes")
	public static int getDrawable(final Class class_obj, final String value) {
		try {
			return class_obj.getField(value).getInt(null);
		}catch (IllegalArgumentException e) {
		}catch (SecurityException e) {
		}catch (IllegalAccessException e) {
		}catch (NoSuchFieldException e) {}
		return 0;
	}
	public static String readDataFromAssets(final Context context, final String filename) {
		String result = TEXT_BLANK;
		try {
			InputStream is = context.getAssets().open(filename);
			final int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			result = new String(buffer, "UTF-8");
		}catch(IOException ex) {}
		return result;
	}
	public static String formatCurrency(final int value, final String currency_format) {
		DecimalFormatSymbols other_symbols = new DecimalFormatSymbols();
		other_symbols.setDecimalSeparator(',');
		other_symbols.setGroupingSeparator('.');
		DecimalFormat df = new DecimalFormat((currency_format.equals(TEXT_BLANK) ? Config.CURRENCY_FORMAT : currency_format), other_symbols);
		return df.format(value);
	}
	public static double getDistance1(
			final double latitude1,
			final double longitude1,
			final double latitude2,
			final double longitude2) {
		double R = 6371;
		double dLat = Math.toRadians(latitude2 - latitude1);
		double dLon = Math.toRadians(longitude2 - longitude1);
		double lat1 = Math.toRadians(latitude1);
		double lat2 = Math.toRadians(latitude2);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		return d;
	}
	public static double getDistance2(
			final double latitude1,
			final double longitude1,
			final double latitude2,
			final double longitude2) {
		double lat1 = latitude1 * Config.PI / 180.0;
		double lon1 = longitude1 * Config.PI / 180.0;
		double lat2 = latitude2 * Config.PI / 180.0;
		double lon2 = longitude2 * Config.PI / 180.0;
		double r = 6378100;
		double rho1 = r * Math.cos(lat1);
		double z1 = r * Math.sin(lat1);
		double x1 = rho1 * Math.cos(lon1);
		double y1 = rho1 * Math.sin(lon1);
		double rho2 = r * Math.cos(lat2);
		double z2 = r * Math.sin(lat2);
		double x2 = rho2 * Math.cos(lon2);
		double y2 = rho2 * Math.sin(lon2);
		double dot = (x1 * x2 + y1 * y2 + z1 * z2);
		double cos_theta = dot / (r * r);
		double theta = Math.acos(cos_theta);
		return r * theta;
	}
	public static double getSpeed(
			final double latitude1,
			final double longitude1,
			final double latitude2,
			final double longitude2,
			final long timestamp1,
			final long timestamp2) {
		double distance = getDistance2(latitude1, longitude1, latitude2, longitude2);
		return getSpeed(distance, timestamp1, timestamp2);
	}
	public static double getSpeed(final double distance, final long timestamp1, final long timestamp2) {
		double time_s = (timestamp2 - timestamp1) / 1000.0;
		double speed_mps = distance / time_s;
		double speed_kph = (speed_mps * 3600.0) / 1000.0;
		return speed_kph;
	}
	public static int getIdentifier(final Context context, final String value, final String package_name) {
		return context.getResources().getIdentifier(value, "id", package_name);
	}
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static String getStringFromBytes(final byte[] bytes) {
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static byte[] getBytesFromString(final String value) {
		if(!isNotNull(value)) {
			return null;
		}
		return Base64.decode(value, Base64.DEFAULT);
	}
	public static byte[] compress(final byte[] data) throws IOException {  
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		ByteArrayOutputStream output_stream = new ByteArrayOutputStream(data.length);   
		deflater.finish();  
		byte[] buffer = new byte[1024];
		while(!deflater.finished()) {
			int count = deflater.deflate(buffer);
			output_stream.write(buffer, 0, count);
		}
		output_stream.close();
		byte[] output = output_stream.toByteArray();
		deflater.end();
		return output;
	}
	public static byte[] decompress(final byte[] data) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream output_stream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while(!inflater.finished()) {
			int count = inflater.inflate(buffer);
			output_stream.write(buffer, 0, count);
		}
		output_stream.close();  
		byte[] output = output_stream.toByteArray();
		inflater.end();
		return output;
	}
	@SuppressLint("ShowToast")
	public static void showToast(final Context context, final String value, final int delay) {
		if(toast != null) {
			if(toast.getView().isShown()) {
				return;
			}
		}
		toast = Toast.makeText(context, value, delay);
		toast.show();
	}
	public static void closeToast() {
		if(toast != null) {
			toast.cancel();
		}
	}
	public static void openActivityLoading(final Activity activity, final String title, final String message, final boolean cancelable) {
		Intent intent = new Intent(activity, Loading.class);
		intent.putExtra("title", title);
		intent.putExtra("message", message);
		intent.putExtra("cancelable", cancelable);
		activity.startActivityForResult(intent, Config.LOADING_ACTIVITY_RESULT);
	}
	public static void closeActivityLoading() {
		if(Loading.instance != null) {
			Loading.instance.close();
		}
	}
	public static void showLoading(final Activity activity) {
		if(activity == null) {
			return;
		}
		if(loading != null) {
			return;
		}
		loading = new ProgressDialog(activity);
		loading.setCancelable(false);
		loading.setMessage(activity.getString(R.string.text_msg_loading));
		loading.show();
	}
	public static void closeLoading() {
		if(loading == null) {
			return;
		}
		loading.cancel();
		loading.dismiss();
		loading = null;
	}
	public static boolean isLoading() {
		return (loading != null);
	}
	public static void openActivityAlert(final Activity activity, final String title, final String message) {
		closeActivityAlert();
		Intent intent = new Intent(activity, Alert.class);
		intent.putExtra("title", title);
		intent.putExtra("message", message);
		activity.startActivityForResult(intent, Config.ALERT_ACTIVITY_RESULT);
	}
	public static void closeActivityAlert() {
		if(Alert.instance != null) {
			Alert.instance.close(true);
		}
	}
	public static void setFont(final Context context, final TextView text_view, final String font_res_name) {
		Typeface face = Typeface.createFromAsset(context.getAssets(), font_res_name);
		text_view.setTypeface(face);
	}
	@SuppressWarnings("deprecation")
	public static void setWakeLock(final Context context, final String TAG) {
		final PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			wake_lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
		}else {
			wake_lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		}
		wake_lock.acquire();
	}
	@SuppressWarnings("deprecation")
	public static void setBrightWakeLock(final Context context, final String TAG) {
		final PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		wake_lock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
	}
	public static void destroyWakeLock() {
		if(wake_lock != null) {
			if(wake_lock.isHeld()) {
				wake_lock.release();
			}
		}
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static void executeValidAsync(final AsyncTask<Void, Void, Void> async_task) {
		async_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	private static void executeNonValidAsync(final AsyncTask<Void, Void, Void> async_task) {
		async_task.execute();
	}
}