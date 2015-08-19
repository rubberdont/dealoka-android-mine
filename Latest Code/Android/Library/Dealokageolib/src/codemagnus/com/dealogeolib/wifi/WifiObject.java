package codemagnus.com.dealogeolib.wifi;

import android.net.wifi.ScanResult;

import codemagnus.com.dealogeolib.utils.GeneralUtils;

/**
 * Created by codemagnus on 4/21/2015.
 */
public class WifiObject {
    private String nodeChanges = "-";
    private String status = "-";
    private ScanResult scanResult;
    private String postStatus;
    private String time;
    private int signalLevel;

    public WifiObject(){}

    public WifiObject(ScanResult result){
        setScanResult(result);
        setNodeChanges("");
        setStatus("");
        setTime(GeneralUtils.getCurrentTime());
        setSignalLevel(result.level);
    }
    public String getNodeChanges() {
        return nodeChanges;
    }
    public void setNodeChanges(String nodeChanges) {
        this.nodeChanges = nodeChanges;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public ScanResult getScanResult() {
        return scanResult;
    }
    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

	public int getSignalLevel() {
		return signalLevel;
	}

	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}
    
    
}
