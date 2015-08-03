package com.dealoka.app.general;

import java.io.File;
import java.util.List;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.Main;
import com.dealoka.app.R;
import com.dealoka.app.controller.TrackerSensorController;
import com.dealoka.app.controller.TrackerSignalController;
import com.dealoka.app.general.session.FeatureSession;
import com.dealoka.app.general.session.SIMSession;
import com.dealoka.app.general.session.TimeSession;
import com.dealoka.app.general.session.UserSession;
import com.dealoka.lib.calligraphy.CalligraphyConfig;
import com.dealoka.lib.controller.SqlController;
import com.dealoka.lib.social.FacebookController;
import com.dealoka.lib.General;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GlobalVariables {
	public static SQLiteDatabase sql_connection = null;
	public static QueryController query_controller = null;
	public static NotificationManager notification_manager = null;
	public static UserSession user_session = null;
	public static FeatureSession feature_session = null;
	public static TimeSession time_session = null;
	public static SIMSession sim_session = null;
	public static TrackerSensorController sensor_controller;
	public static TrackerSignalController signal_controller;
	public static FacebookController facebook_controller;
	public static float latitude = 0.0f;
	public static float longitude = 0.0f;
	public static float orientation = 0.0f;
	public static int signal = 0;
	public static int lac = 0;
	public static int cid = 0;
	public static int mcc = 0;
	public static int mnc = 0;
	public static long time = 0;
	public static int notif_id = 0;
	public static String session = General.TEXT_BLANK;
	public static String imsi = General.TEXT_BLANK;
	public static String device_id = General.TEXT_BLANK;
	public static String operator_name = General.TEXT_BLANK;
	public static String operator = General.TEXT_BLANK;
	public static List<String> imsi_list;
	public static List<String> device_id_list;
	public static List<String> operator_name_list;
	public static List<String> operator_list;
	public static String[] menu_list;
	public static void load(final Context context) {
		com.dealoka.lib.Config.LOG_ENABLED = Config.log_enabled;
		com.dealoka.lib.Config.LOCATION_TIMER = Config.location_timer;
		final SqlController sql_controller = new SqlController(context, Config.sql_path, Config.sql_file);
		sql_connection = sql_controller.openConnection();
		query_controller = new QueryController();
		user_session = new UserSession(context);
		feature_session = new FeatureSession(context);
		time_session = new TimeSession(context);
		sim_session = new SIMSession(context);
		GlobalController.getSIMDevice(context);
		sensor_controller = new TrackerSensorController(context);
		signal_controller = new TrackerSignalController(context);
		facebook_controller = new FacebookController(context);
		menu_list = context.getResources().getStringArray(R.array.menu);
		setImageLoader();
	}
	public static void unload() {
		user_session.closeSession();
		query_controller.truncateWallet();
		General.setBadgeCount(DealokaApp.getAppContext(), 0, Main.class.getName());
	}
	private static void setImageLoader() {
		final File cache_dir = StorageUtils.getCacheDirectory(DealokaApp.getAppContext());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(DealokaApp.getAppContext())
			.diskCacheExtraOptions(480, 800, null)
			.denyCacheImageMultipleSizesInMemory()
			.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
			.memoryCacheSize(2 * 1024 * 1024)
			.diskCacheSize(50 * 1024 * 1024)
			.diskCache(new UnlimitedDiscCache(cache_dir))
			.diskCacheFileCount(100)
			.build();
		ImageLoader.getInstance().init(config);
	}
	public static void setFontInit() {
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
			.setDefaultFontPath("fonts/MyriadPro-Light.ttf")
			.setFontAttrId(R.attr.font_path)
			.build()
		);
	}
}