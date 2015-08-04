package com.dealoka.lib.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.dealoka.lib.General;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class PhoneManager {
	private static PhoneStateCallback phone_state_callback;
	public static interface PhoneStateCallback {
		public abstract void didSignalChanged(final int strength);
	}
	public static boolean isPhoneNetworkConnected(final Context context) {
		final TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		final ConnectivityManager connectivity_manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo network_info = connectivity_manager.getActiveNetworkInfo();
		if(network_info == null) {
			return false;
		}else if(telephony_manager.getDataState() == TelephonyManager.DATA_CONNECTED || network_info.isConnected()) {
			return true;
		}else {
			return false;
		}
	}
	public static String getDeviceID(final Context context) {
		final TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephony_manager.getDeviceId();
	}
	public static String getSIMSerialNumber(final Context context) {
		final TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephony_manager.getSimSerialNumber();
	}
	public static String getSIMIMSI(final Context context) {
		final TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephony_manager.getSubscriberId();
	}
	public static String getMobileNumber(final Context context) {
		final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
	public static String getOperator(final Context context) {
		TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephony_manager.getNetworkOperator();
	}
	public static String getOperatorName(final Context context) {
		TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephony_manager.getNetworkOperatorName();
	}
	public static String getSerialNumber() {
		String serial_number = General.TEXT_BLANK;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			serial_number = (String)(get.invoke(c, "ro.serialno", "unknown"));
		}catch(Exception ex) {}
		return serial_number;
	}
	public static String getAndroidID(final Context context) {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}
	public static ArrayList<Integer> getLACCID(final Context context) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		final TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if(telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			final GsmCellLocation location = (GsmCellLocation)telephony.getCellLocation();
			if(location != null) {
				final int lac = location.getLac();
				final int cid = location.getCid();
				result.add(lac);
				result.add(cid);
			}
		}else if(telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
			final CdmaCellLocation location = (CdmaCellLocation)telephony.getCellLocation();
			if(location != null) {
				final int lat = location.getBaseStationLatitude();
				final int lng = location.getBaseStationLongitude();
				result.add(lat);
				result.add(lng);
			}
		}
		return result;
	}
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "InlinedApi" })
	public static boolean isAirplaneModeOn(final Context context){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;          
        }else {
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } 
	}
	public static boolean isSimCardAbsent(final Context context) {
		TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return (telephony_manager.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
	}
	public static boolean isDualSIM(final Context context) {
		return SimNoInfo.getInstance(context).isDualSIM();
	}
	public static List<String> getDeviceIDDual(final Context context) {
		final SimNoInfo sim_no_info = SimNoInfo.getInstance(context);
		List<String> id = new ArrayList<String>();
		id.add(sim_no_info.getImeiSIM1());
		id.add(sim_no_info.getImeiSIM2());
		return id;
	}
	public static List<String> getIMSIDual(final Context context) {
		final SimNoInfo sim_no_info = SimNoInfo.getInstance(context);
		List<String> imsi = new ArrayList<String>();
		imsi.add(sim_no_info.getIMSISIM1());
		imsi.add(sim_no_info.getIMSISIM2());
		return imsi;
	}
	public static List<String> getOperatorDual(final Context context) {
		final SimNoInfo sim_no_info = SimNoInfo.getInstance(context);
		List<String> operator = new ArrayList<String>();
		operator.add(sim_no_info.getOperatorSIM1());
		operator.add(sim_no_info.getOperatorSIM2());
		return operator;
	}
	public static List<String> getOperatorNameDual(final Context context) {
		final SimNoInfo sim_no_info = SimNoInfo.getInstance(context);
		List<String> operator_name = new ArrayList<String>();
		operator_name.add(sim_no_info.getOperatorNameSIM1());
		operator_name.add(sim_no_info.getOperatorNameSIM2());
		return operator_name;
	}
	public static List<String> getMethods(final Context context) {
		List<String> method = new ArrayList<String>();
		TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		Class<?> telephony_class;
		try {
			telephony_class = Class.forName(telephony_manager.getClass().getName());
			Method[] methods = telephony_class.getMethods();
			for(int counter = 0; counter < methods.length; counter++) {
				method.add(methods[counter].getName());
			}
		}catch(ClassNotFoundException ex) {}
		return method;
	}
	public static int getCpuCores() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.availableProcessors();
	}
	public static void getPhoneState(final Context context, final PhoneStateCallback psc) {
		phone_state_callback = psc;
		TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		telephony_manager.listen(phone_state_listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	private static PhoneStateListener phone_state_listener = new PhoneStateListener() {
		public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
			if(phone_state_callback != null) {
				if(signalStrength.isGsm()) {
					phone_state_callback.didSignalChanged(signalStrength.getGsmSignalStrength());
				}else {
					phone_state_callback.didSignalChanged(signalStrength.getCdmaDbm());
				}
			}
		};
	};
	private static class SimNoInfo {
		private static SimNoInfo instance;
		private String imei_sim1 = General.TEXT_BLANK;
		private String imei_sim2 = General.TEXT_BLANK;
		private String imsi_sim1 = General.TEXT_BLANK;
		private String imsi_sim2 = General.TEXT_BLANK;
		private String operator_name_sim1 = General.TEXT_BLANK;
		private String operator_name_sim2 = General.TEXT_BLANK;
		private String operator_sim1 = General.TEXT_BLANK;
		private String operator_sim2 = General.TEXT_BLANK;
		private boolean sim1_ready = false;
		private boolean sim2_ready = false;
		public static SimNoInfo getInstance(final Context context) {
			if(instance == null) {
				instance = new SimNoInfo();
			}
			try {
				instance.imei_sim1 = getDeviceIdBySlot(context, "getDeviceId", 0);
				instance.imei_sim2 = getDeviceIdBySlot(context, "getDeviceId", 1);
				instance.imsi_sim1 = getDeviceIdBySlot(context, "getSubscriberId", 0);
				instance.imsi_sim2 = getDeviceIdBySlot(context, "getSubscriberId", 1);
				instance.operator_sim1 = getDeviceIdBySlot(context, "getNetworkOperator", 0);
				instance.operator_sim2 = getDeviceIdBySlot(context, "getNetworkOperator", 1);
				instance.operator_name_sim1 = getDeviceIdBySlot(context, "getNetworkOperatorName", 0);
				instance.operator_name_sim2 = getDeviceIdBySlot(context, "getNetworkOperatorName", 1);
			}catch(MethodNotFoundException ex) {}
			try {
				instance.sim1_ready = getSIMStateBySlot(context, "getSimState", 0);
				instance.sim2_ready = getSIMStateBySlot(context, "getSimState", 1);
			}catch(MethodNotFoundException ex) {}
			return instance;
		}
		public String getImeiSIM1() {
			return imei_sim1;
		}
		public String getImeiSIM2() {
			return imei_sim2;
		}
		public String getIMSISIM1() {
			return imsi_sim1;
		}
		public String getIMSISIM2() {
			return imsi_sim2;
		}
		public String getOperatorSIM1() {
			return operator_sim1;
		}
		public String getOperatorSIM2() {
			return operator_sim2;
		}
		public String getOperatorNameSIM1() {
			return operator_name_sim1;
		}
		public String getOperatorNameSIM2() {
			return operator_name_sim2;
		}
		public boolean isSIM1Ready() {
			return sim1_ready;
		}
		public boolean isSIM2Ready() {
			return sim2_ready;
		}
		public boolean isDualSIM() {
			return isSIM1Ready() && isSIM2Ready();
		}
		private static String getDeviceIdBySlot(
				final Context context,
				final String predictedMethodName,
				final int slotID) throws MethodNotFoundException {
			String imei = null;
			TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			try {
				Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
				Class<?>[] parameter = new Class[1];
				parameter[0] = int.class;
				Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
				Object[] obParameter = new Object[1];
				obParameter[0] = slotID;
				Object ob_phone = getSimID.invoke(telephony, obParameter);
				if(ob_phone != null) {
					imei = ob_phone.toString();
				}
			}catch(Exception ex) {
				throw new MethodNotFoundException(predictedMethodName);
			}
			return imei;
		}
		private static  boolean getSIMStateBySlot(
				final Context context,
				final String predictedMethodName,
				final int slotID) throws MethodNotFoundException {
			boolean ready = false;
			TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			try {
				Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
				Class<?>[] parameter = new Class[1];
				parameter[0] = int.class;
				Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);
				Object[] obParameter = new Object[1];
				obParameter[0] = slotID;
				Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);
				if(ob_phone != null) {
					int simState = Integer.parseInt(ob_phone.toString());
					if(simState == TelephonyManager.SIM_STATE_READY) {
						ready = true;
					}
				}
			}catch(Exception ex) {
				throw new MethodNotFoundException(predictedMethodName);
			}
			return ready;
		}
		private static class MethodNotFoundException extends Exception {
			private static final long serialVersionUID = -996812356902545308L;
			public MethodNotFoundException(String info) {
				super(info);
			}
		}
	}
}