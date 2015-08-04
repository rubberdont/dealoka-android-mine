package codemagnus.com.dealogeolib.wifi;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import codemagnus.com.dealogeolib.http.WebserviceRequest;

/**
 * Created by eharoldreyes on 1/30/15.
 */
public class WifiHelper {
    public static String tag = "WifiHelper";
    private final WifiManager wifiManager;
    private final Context context;
    private ScanResultsListener scanResultsListener;

    private WifiInfo wifiInfo;


    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
        	if(scanResultsListener != null) {
                scanResultsListener.onChange(getScanResults());
            }
        }
    };

    public interface ScanResultsListener{
        void onChange(List<ScanResult> scanResults);
    }

    public WifiHelper(Context context){
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.wifiManager.setWifiEnabled(true);
        this.wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, tag);
        this.wifiInfo = wifiManager.getConnectionInfo();
        startUpdates();
    }

    public WifiManager getWifiManager(){
        return this.wifiManager;
    }

    public List<ScanResult> getScanResults(){
        return wifiManager.getScanResults();
    }

    public void setScanResultsListener(ScanResultsListener listener){
        this.scanResultsListener = listener;
        this.wifiManager.startScan();
    }

    public void startUpdates(){
    	IntentFilter wifiFilter = new IntentFilter();
    	wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    	wifiFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
    	wifiFilter.addAction(WifiManager.EXTRA_NEW_STATE);
    	wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.context.registerReceiver(mWifiScanReceiver, wifiFilter);
    }

    public void onStop(){
        this.context.unregisterReceiver(mWifiScanReceiver);
    }

    public WifiInfo getWifiInfo() {
        return wifiInfo;
    }

    public WebserviceRequest.HttpURLCONNECTION getOffersByWifi(String url, String bssids, WebserviceRequest.Callback callback){
        WebserviceRequest.HttpURLCONNECTION getWifiOffers = new WebserviceRequest.HttpURLCONNECTION();

        getWifiOffers.setUrl(url + "/wifi/offers?bssid=" + bssids);
        getWifiOffers.setRequestMethod("GET");
        getWifiOffers.setCallback(callback);
        getWifiOffers.execute();
        return getWifiOffers;
    }
}
