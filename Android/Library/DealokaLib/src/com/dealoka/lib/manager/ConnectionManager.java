package com.dealoka.lib.manager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import com.dealoka.lib.General;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ConnectionManager {
	public static enum InterfaceName {
		Wireless ("wlan0"),
		Eth0 ("eth0");
		private final String id;
		InterfaceName(String id) { this.id = id; }
	    public String getValue() { return id; }
	};
	public static enum WifiEnum {
		SSID ("ssid"),
		Level ("level"),
		MAC ("mac");
		private final String id;
		WifiEnum(String id) { this.id = id; }
	    public String getValue() { return id; }
	};
	private static ConnectivityManager connectivity = null;
	public static void init(final Context context) {
		connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	public static boolean isConnected() {
		if(connectivity == null) {
			return false;
		}
		NetworkInfo ni = connectivity.getActiveNetworkInfo();
		if(ni == null) {
			return false;
		}else {
			return ni.isConnected();
		}
	}
	public static boolean isAvailable() {
		if(connectivity == null) {
			return false;
		}
		NetworkInfo ni = connectivity.getActiveNetworkInfo();
		if(ni == null) {
			return false;
		}else {
			return ni.isAvailable();
		}
	}
	public static boolean isConnectedOrConnecting() {
		if(connectivity == null) {
			return false;
		}
		NetworkInfo ni = connectivity.getActiveNetworkInfo();
		if(ni == null) {
			return false;
		}else {
			return ni.isConnectedOrConnecting();
		}
	}
	public static boolean isFailover() {
		if(connectivity == null) {
			return false;
		}
		NetworkInfo ni = connectivity.getActiveNetworkInfo();
		if(ni == null) {
			return false;
		}else {
			return ni.isFailover();
		}
	}
	public static boolean isRoaming() {
		if(connectivity == null) {
			return false;
		}
		NetworkInfo ni = connectivity.getActiveNetworkInfo();
		if(ni == null) {
			return false;
		}else {
			return ni.isRoaming();
		}
	}
	public static boolean isWifiAvailable() {
		if(connectivity == null) {
			return false;
		}
		android.net.NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);		 
		return wifi.isAvailable();
	}
	public static boolean isWifiConnected() {
		if(connectivity == null) {
			return false;
		}
		android.net.NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);		
		return wifi.isConnected();
	}
	public static boolean isMobileAvailable() {
		if(connectivity == null) {
			return false;
		}
		try{
			android.net.NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);		 
			return mobile.isAvailable();
		}catch(Exception ex) {
		    return false;	
		}	
	}
	public static boolean isMobileConnected() {
		if(connectivity == null) {
			return false;
		}
		try{
			android.net.NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);		
			return mobile.isConnected();
		}catch(Exception ex) {
		    return false;	
		}
	}
	public static String getSSID(final Context context) {
		final WifiManager wifi_manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if(wifi_manager.getConnectionInfo() != null) {
			String ssid = wifi_manager.getConnectionInfo().getSSID();
			if(General.isNotNull(ssid)) {
				ssid = ssid.replace("\"", "");
			}
			return ssid;
		}
		return null;
	}
	public static String getMACAddress(final Context context) {
		WifiManager wifi_manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifi_info = wifi_manager.getConnectionInfo();
		return wifi_info.getMacAddress(); 
	}
	@SuppressLint("NewApi")
	public static String getMACAddress(final InterfaceName interface_name) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for(NetworkInterface intf : interfaces) {
				if(interface_name != null) {
					if(!intf.getName().equalsIgnoreCase(interface_name.getValue())) continue;
				}
				final byte[] mac = intf.getHardwareAddress();
				if(mac == null) return General.TEXT_BLANK;
				StringBuilder buf = new StringBuilder();
				for(int idx = 0; idx < mac.length; idx++) {
					buf.append(String.format("%02X:", mac[idx]));
				}
				if(buf.length() > 0) {
					buf.deleteCharAt(buf.length()-1);
				}
				return buf.toString();
			}
		}catch(Exception ex) {}
		return General.TEXT_BLANK;
    }
	@SuppressLint("DefaultLocale")
	public static String getIPAddress(final boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for(NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for(InetAddress addr : addrs) {
					if(!addr.isLoopbackAddress()) {
						final String address = addr.getHostAddress().toUpperCase();
						final boolean isIPv4 = InetAddressUtils.isIPv4Address(address); 
						if(useIPv4) {
							if(isIPv4) {
								return address;
							}
						}else {
							if(!isIPv4) {
								int delim = address.indexOf('%');
								return delim<0 ? address : address.substring(0, delim);
							}
						}
					}
				}
			}
		}catch(Exception ex) {}
		return General.TEXT_BLANK;
	}
	public static ArrayList<HashMap<WifiEnum, Object>> getWifiScan(final Context context) {
		ArrayList<HashMap<WifiEnum, Object>> result = new ArrayList<HashMap<WifiEnum, Object>>();
		WifiManager wifi_manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		final List<ScanResult> scan_results = wifi_manager.getScanResults();
		Iterator<ScanResult> iterator = scan_results.iterator();
		while(iterator.hasNext()) {
			ScanResult scan_result = iterator.next();
			HashMap<WifiEnum, Object> item = new HashMap<WifiEnum, Object>();
			item.put(WifiEnum.SSID, scan_result.SSID);
			item.put(WifiEnum.Level, scan_result.level);
			item.put(WifiEnum.MAC, scan_result.BSSID);
			result.add(item);
		}
		return result;
	}
	public static void startWifiScan(final Context context) {
		final WifiManager wifi_manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		wifi_manager.startScan();
	}
}