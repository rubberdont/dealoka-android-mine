package codemagnus.com.dealogeolib.wifi;

import android.net.wifi.ScanResult;

/**
 * Created by codemagnus on 3/4/2015.
 */
public class WifiData {
    private String ssis;
    private String bssis;

    public WifiData() {
    }
    public String getSsis() {
        return ssis;
    }

    public void setSsis(String ssis) {
        this.ssis = ssis;
    }

    public String getBssis() {
        return bssis;
    }

    public void setBssis(String bssis) {
        this.bssis = bssis;
    }

    public WifiData(ScanResult result) {
        setSsis(result.SSID);
        setBssis(result.BSSID);
    }
}
