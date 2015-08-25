package codemagnus.com.dealogeolib.wifi;

import static codemagnus.com.dealogeolib.utils.LogUtils.LOGD;
import static codemagnus.com.dealogeolib.utils.LogUtils.LOGE;
import static codemagnus.com.dealogeolib.utils.LogUtils.LOGW;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import codemagnus.com.dealogeolib.http.WebService;
import codemagnus.com.dealogeolib.utils.GeneralUtils;

/**
 * Created by codemagnus on 4/21/2015.
 */
public class WifiDataUtils {

    public static final String tag = "WifiDataUtils";

    public WifiHelper getWifiHelper() {
        return wifiHelper;
    }

    private WifiHelper wifiHelper;
    private Context context;

    public WifiDataUtils(Context context, WifiHelper wifiHelper){
        this.context = context;
        this.wifiHelper = wifiHelper;
    }

    public JSONObject submitWifiData(List<WifiObject> results){
        JSONObject object = new JSONObject();
        try {
            object.put(WebService.DEVICE_ID,    GeneralUtils.getDeviceID(context));
            object.put(WebService.WIFIS, getWifisData(results));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public JSONArray getWifisData(List<WifiObject> results) {
        JSONArray arrayWifi = new JSONArray();
        for(WifiObject scanned:results) {
            JSONObject object = getWifiObject(scanned);
            if(object != null)
                arrayWifi.put(getWifiObject(scanned));
        }
        return arrayWifi;
    }

    public JSONObject getWifiObject(WifiObject scanned){
        WifiConfiguration config = getWifiConfig(scanned);
        JSONObject objScanned = null;
        if(config != null) {
            objScanned = new JSONObject();
            try {
                objScanned.put(WebService.BSSID,            scanned.getScanResult().BSSID);
                objScanned.put(WebService.RSSI,             scanned.getScanResult().level);
                objScanned.put(WebService.SSID,             scanned.getScanResult().SSID);
                objScanned.put(WebService.FREQUENCY,        scanned.getScanResult().frequency);
                objScanned.put(WebService.CAPABILITIES,     scanned.getScanResult().capabilities);
                objScanned.put(WebService.HIDDEN_SSID,      config.hiddenSSID);
                objScanned.put(WebService.NETWORK_ID,       config.networkId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objScanned;
    }

    public WifiConfiguration getWifiConfig(WifiObject result){
        List<WifiConfiguration> infos = wifiHelper.getWifiManager().getConfiguredNetworks();
        if(infos != null) {
            LOGW("WifiPreference", "No of Networks" + infos.size());
            for(WifiConfiguration config: infos) {
                LOGD(tag, "Result SSID: " + result.getScanResult().SSID + " Config SSID: " + config.SSID);
                return config;
            }
        }
        return null;
    }

    public List<WifiObject> getWifiObjects(List<ScanResult> scanResults, String lastLongMillies) {
        List<WifiObject> wifis = new ArrayList<>();
        for(ScanResult result: scanResults) {
            WifiObject wifi = new WifiObject(result);
            wifi.setStatus("-");
            wifi.setNodeChanges(lastLongMillies);
            wifis.add(wifi);
        }
        return wifis;
    }

    public JSONArray getWifiBSSID(List<WifiObject> list){
        JSONArray listBSSID = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            WifiObject wifi = list.get(i);
            JSONObject wifiObj = new JSONObject();
            try {
				wifiObj.put("_id", wifi.getScanResult().BSSID);
				wifiObj.put("signal", WifiManager.calculateSignalLevel(wifi.getSignalLevel(), 100));
	            listBSSID.put(wifiObj);
			} catch (JSONException e) {
				;
			}
        }
        return listBSSID;
    }

    public List<WifiObject> getNewAddedWifi(List<WifiObject> mCurrentWifis,
                                            List<WifiObject> newList){
        for (int i = 0; i < mCurrentWifis.size(); i++) {
            WifiObject mWifi = mCurrentWifis.get(i);
            for (int j = 0; j < newList.size(); j++) {
                WifiObject nWifi = newList.get(j);
                if(mWifi.getScanResult().BSSID.equals(nWifi.getScanResult().BSSID)){
                    newList.remove(nWifi);
                }
            }
        }
        for (WifiObject nWifi : newList){
            LOGE("getNewAddedWifi", nWifi.getScanResult().SSID);
        }
        return newList;
    }


}
