package com.dealoka.app.general;

public class Config {
	public final static String TAG = "Dealoka";
	public final static String phone_type = "android";
	public final static boolean log_enabled = true;
	public final static int notification_id = 1;
	public final static String socket_server = "wss://mobile.dealoka.com:443/websocket";
	//public final static String socket_server = "ws://54.254.203.180:3000/websocket";
	public final static String sql_path = "data/data/com.dealoka.app/database/";
	public final static String sql_file = "db.sqlite";
	public final static String date_format = "MM/dd/yyyy";
	public final static String truncate_wallet_format = "6ac0d21b0b593654bc20c298bc074179";
	public final static String delete_wallet_format = "d260635e5ac806c35fdaca3f1ffa01d3";
	public final static int list_limit = 10;
	public final static int location_timer = 10;
	public final static long socket_timer = 3;
	public final static long idle_timer = 5 * 1000;
	public final static int toast_delay = 3000;
	public final static int question_activity_result = 90;
	public final static int switch_user_activity_result = 91;
	public final static int forgot_activity_result = 92;
	public final static int provider_settings_activity_result = 93;
	public final static int restart_activity_result = 94;
	public final static int sim_card_activity_result = 95;
	public final static int tc_activity_result = 96;
	public final static int google_plus_activity_result = 97;
	public final static int send_message_activity_result = 98;
	public final static int sdk_special_code_activity_result = 99;
	public final static int special_code_activity_result = 100;
	public final static int WHITE = 0xFFFFFFFF;
	public final static int BLACK = 0xFF000000;
	public final static long text_length_valid = 6;
	public final static float alpha = 0.8f;
	public final static String message_notif = "message";
	public final static String review_notif = "review";
	public final static String popup_notif = "popup";
	public static int review_notification = 100;
	
	public static final String DEALOKA_GEO_LIB_URL = "http://54.169.132.22:3000";
}