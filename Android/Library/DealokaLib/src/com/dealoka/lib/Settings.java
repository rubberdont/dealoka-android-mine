package com.dealoka.lib;

public class Settings {
	public static void setLogger(final boolean enabled) {
		Config.LOG_ENABLED = enabled;
	}
	public static void setCurrencyFormat(final String format) {
		Config.CURRENCY_FORMAT = format;
	}
	public static void setDateFormat(final String format) {
		Config.DATE_FORMAT = format;
	}
}