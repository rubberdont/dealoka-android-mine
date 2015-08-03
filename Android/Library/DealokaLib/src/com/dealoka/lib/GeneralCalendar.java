package com.dealoka.lib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;

public class GeneralCalendar {
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setMinMaxDatePicker(final DatePickerDialog dlg_date, final Calendar cal_min, final Calendar cal_max) {
		if(General.isValidOS()) {
			dlg_date.getDatePicker().setMinDate(cal_min.getTimeInMillis());
			dlg_date.getDatePicker().setMaxDate(cal_max.getTimeInMillis());
		}
	}
	public static long getUTCTimestamp() {
		return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
	}
	@SuppressLint("SimpleDateFormat")
	public static String getDate(final long timestamp, final String format) {
		Date date = new Date(timestamp);
		SimpleDateFormat date_format = new SimpleDateFormat(format);
		return date_format.format(date);
	}
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate(final String date_format, final int max_hour) {
		DateFormat dateFormat = new SimpleDateFormat((date_format.equals(General.TEXT_BLANK) ? Config.DATE_FORMAT : date_format));
    	Date date = new Date();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	if(max_hour > 0) {
    		if(cal.get(Calendar.HOUR) > max_hour) {
    			cal.add(Calendar.DATE, 1);
    		}
    	}
    	date = cal.getTime();
    	return dateFormat.format(date);
	}
	@SuppressLint("SimpleDateFormat")
	public static long getCurrentUTCTimestamp(final long timestamp) {
		Date date = new Date(timestamp);
		return date.getTime();
	}
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDiffDate(final int diff_day, final String date_format, final int max_hour) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(max_hour > 0) {
			if(cal.get(Calendar.HOUR) > max_hour) {
				cal.add(Calendar.DATE, 1);
			}
		}
		cal.add(Calendar.DATE, diff_day);
		date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat((date_format.equals(General.TEXT_BLANK) ? Config.DATE_FORMAT : date_format));
		return dateFormat.format(date);
	}
	public static Calendar getCalendarFromTimestamp(final long timestamp) {
		Date date = new Date(timestamp);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	public static Calendar getCalendarFromInt(final int year, final int month, final int date) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date);
		return cal;
	}
	@SuppressLint("SimpleDateFormat")
	public static String getStringFromCalendar(final Calendar cal, final String date_format) {
		Date date = new Date();
		date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat((date_format.equals(General.TEXT_BLANK) ? Config.DATE_FORMAT : date_format));
		return dateFormat.format(date);
	}
	public static long getDiffDate(final Calendar cal_from, final Calendar cal_to) {
		Date dateFrom = new Date();
		Date dateTo = new Date();
		dateFrom = cal_from.getTime();
		dateTo = cal_to.getTime();
		return (dateFrom.getTime() - dateTo.getTime()) / (1000 * 60 * 60 * 24);
	}
	public static String getDayName(final Context context, final Calendar calendar) {
		List<String> day_name = Arrays.asList(context.getResources().getStringArray(R.array.day_name));
		return day_name.get(calendar.get(Calendar.DAY_OF_WEEK));
	}
	public static String getDayFullName(final Context context, final Calendar calendar) {
		List<String> day_fullname = Arrays.asList(context.getResources().getStringArray(R.array.day_fullname));
		return day_fullname.get(calendar.get(Calendar.DAY_OF_WEEK));
	}
	public static String getMonthName(final Context context, final Calendar calendar) {
		List<String> month_name = Arrays.asList(context.getResources().getStringArray(R.array.month_name));
		return month_name.get(calendar.get(Calendar.MONTH) + 1);
	}
	public static String getMonthFullName(final Context context, final Calendar calendar) {
		List<String> month_fullname = Arrays.asList(context.getResources().getStringArray(R.array.month_fullname));
		return month_fullname.get(calendar.get(Calendar.MONTH) + 1);
	}
}