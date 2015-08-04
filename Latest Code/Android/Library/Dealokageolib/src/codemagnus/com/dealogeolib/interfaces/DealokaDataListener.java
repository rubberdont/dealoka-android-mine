package codemagnus.com.dealogeolib.interfaces;

import android.location.Location;

import java.util.List;

import codemagnus.com.dealogeolib.tower.Tower;
import codemagnus.com.dealogeolib.wifi.WifiObject;

/**
 * Created by codemagnus on 5/22/15.
 */
public interface DealokaDataListener {
    void onLocationUpdate(Location location);
    void onWifiDetect(List<WifiObject> wifis);
    void onTowerDetect(Tower tower);
    void onWifiRequestResult(WifiObject wifiObject);
    void onError(String message);
}
