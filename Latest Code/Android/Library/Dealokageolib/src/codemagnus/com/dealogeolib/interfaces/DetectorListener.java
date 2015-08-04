package codemagnus.com.dealogeolib.interfaces;

import android.location.Location;

import java.util.List;

import codemagnus.com.dealogeolib.tower.Tower;
import codemagnus.com.dealogeolib.wifi.WifiObject;

/**
 * Created by codemagnus on 5/11/15.
 */
public interface DetectorListener {
    void onNewTowerDetected(Tower aTower);
    void onLocationChange(Location location);
    void onNewWifiDetected(List<WifiObject> aWifiData);
    void onError(String message);
    void onWifiRequestResult(WifiObject aWifi);
}
