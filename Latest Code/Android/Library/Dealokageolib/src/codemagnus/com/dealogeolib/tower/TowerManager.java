package codemagnus.com.dealogeolib.tower;

import static codemagnus.com.dealogeolib.utils.LogUtils.LOGD;
import static codemagnus.com.dealogeolib.utils.LogUtils.LOGI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import codemagnus.com.dealogeolib.http.WebserviceRequest;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by eharoldreyes on 11/21/14.
 */
public class TowerManager {
	
    public static final String tag = "TowerManager";
    private static TowerManager instance;
    private final TelephonyManager telephonyManager;
    private TowersChangeCallback towersChangeCallback;
    private PrimaryTowersChangeCallback primaryTowersChangeCallback;
    private SignalStrength signalStrength;

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            TowerManager.this.signalStrength = signalStrength;

            if(towersChangeCallback != null) {
                try {
                    towersChangeCallback.didTowersChanged(getTowers(), null);
                } catch (JSONException e) {
                    towersChangeCallback.didTowersChanged(null, e);
                }
            }

            if(primaryTowersChangeCallback != null) {
                try {
                    List<Tower> availableTowers = getPrimaryTowers();
                    ArrayList<Tower> towersPrimary = new ArrayList<>();
                    ArrayList<Tower> towersNeighbor = new ArrayList<>();

                    for(Tower tower: availableTowers) {
                        if(tower.isNeighbor()) {
                            towersNeighbor.add(tower);
                        } else {
                            towersPrimary.add(tower);
                        }
                    }

                    primaryTowersChangeCallback.didTowersChanged(towersPrimary, towersNeighbor, null);
//                    primaryTowersChangeCallback.didTowersChanged(getTowers(), null);
                } catch (JSONException e) {
                    primaryTowersChangeCallback.didTowersChanged(null, null, e);
                }
            }
        }
    };
    
    public static TowerManager getInstance(Context context){
    	 if(instance == null){
             synchronized (TowerManager.class){
                 if(instance == null)
                     instance = new TowerManager(context);
             }
         }
    	return instance;
    }

    public TowerManager(Context context){
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public void setOnTowersChangedListener(TowersChangeCallback towersChangeCallback) {
        this.towersChangeCallback = towersChangeCallback;
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public void setOnPrimaryTowerChangedListener(PrimaryTowersChangeCallback towersChangeCallback){
        this.primaryTowersChangeCallback = towersChangeCallback;
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public void stopTowerUpdate(){
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    public ArrayList<Tower> getTowers() throws JSONException {
        ArrayList<Tower> towers                             = new ArrayList<>();

        List<NeighboringCellInfo> neighboringCellInfos      = getNeighboringCellInfo();
        List<CellInfo> cellInfos                            = getAllCellInfo();

        LOGI(tag, "getTowers phoneType: " + getPhoneType());
        LOGI(tag, "getTowers using: NeighboringCellInfos size: " + neighboringCellInfos.size());
        LOGI(tag, "getTowers using: CellInfos size: " + (cellInfos == null ? "null" : cellInfos.size()));

        LOGI(tag, "getTowers using: PrimaryTower");
        Tower primaryTower = getPrimaryTower();
        if(primaryTower != null) towers.add(primaryTower);
        if(cellInfos != null && cellInfos.size() > 0) {
            towers.addAll(Converter.cellInfosToTowers(this, cellInfos));
        }
        if (neighboringCellInfos.size() > 0) {
            towers.addAll(Converter.neighboringCellInfosToTowers(neighboringCellInfos));
        }
        LOGI(tag, "getTowers using: PrimaryTower size in here: " + towers.size());
        return towers;
    }

    public ArrayList<Tower> getPrimaryTowers() throws JSONException {
        ArrayList<Tower> towers = new ArrayList<>();
        LOGI(tag, "getTowers using: PrimaryTower");
        Tower primaryTower = getPrimaryTower();
        if (primaryTower != null) towers.add(primaryTower);

        List<NeighboringCellInfo> neighboringCellInfos      = getNeighboringCellInfo();
        if (neighboringCellInfos != null && neighboringCellInfos.size() > 0) {
            LOGI(tag, "getTowers using: NeighboringCellInfos size: " + neighboringCellInfos.size());
            for(Tower info: Converter.neighboringCellInfosToTowers(neighboringCellInfos))
            if(!towers.toString().contains("" + info.getCellId())) {
                towers.add(info);
            }
        }
        return towers;
    }

    public Tower getPrimaryTower() throws JSONException{
        CellLocation cellLocation = telephonyManager.getCellLocation();
        LOGD(tag, "Cell location instance:");
        if(cellLocation instanceof GsmCellLocation){
            LOGD(tag, "Cell location instance: GsmCellLocation");
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
            return new Tower(Converter.gsmCellLocationToJson(this, gsmCellLocation));
        }else if(cellLocation instanceof CellLocation){
            LOGD(tag, "Cell location instance: CellLocation");
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
            return new Tower(Converter.gsmCellLocationToJson(instance, gsmCellLocation));
        }  else {
            LOGD(tag, "Cell location instance: null");
            return null;
        }
    }
    public List<NeighboringCellInfo> getNeighboringCellInfo(){
        return telephonyManager.getNeighboringCellInfo();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public List<CellInfo> getAllCellInfo(){
        return telephonyManager.getAllCellInfo();
    }
    public String getSIMSerialNumber() {
        try{
            return telephonyManager.getSimSerialNumber();
        }catch (Exception e) {
            return "Not available";
        }
    }
    public String getSIMIMSI()  {
        return telephonyManager.getSubscriberId();
    }
    public String getMobileNumber() {
        try{
            return telephonyManager.getLine1Number();
        } catch (Exception e){
            return "Not available";
        }
    }

    public String getOperatorName() {
        try{
            return telephonyManager.getNetworkOperator().substring(0, 3);
        } catch (Exception e){
            return "Not available";
        }
    }

    public String getMobileCountryCode(){
        try{
            return telephonyManager.getNetworkOperator().substring(0, 3);
        } catch (Exception e){
            try {
                return telephonyManager.getSimOperator().substring(0, 3);
            } catch (Exception e1) {
                return "Not available";
            }
        }
    }
    public String getMobileNetworkCode(){
        try{
            return telephonyManager.getNetworkOperator().substring(3);
        } catch (Exception e){
            try {
                return telephonyManager.getSimOperator().substring(3);
            } catch (Exception e1) {
                return "Not available";
            }
        }
    }
    public String getPhoneType(){
        switch (telephonyManager.getPhoneType()){
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "cdma";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "gsm";
            default:
                return "unknown";
        }
    }
    public GsmCellLocation getCellLocation(){
        try {
            return (GsmCellLocation) telephonyManager.getCellLocation();
        } catch (Exception e) {
            return null;
        }
    }
    public SignalStrength getSignalStrength() {
        try {
            return signalStrength;
        } catch (Exception e) {
            return null;
        }
    }
    
    public interface LocationRequestCallback{
        void onSuccess(LatLng latLng, List<Tower> towers);
        void onFailed(Exception e);
    }

    public interface TowersChangeCallback {
        void didTowersChanged(ArrayList<Tower> towers, Exception e);
    }

    public interface PrimaryTowersChangeCallback {
        void didTowersChanged(ArrayList<Tower> towersPrimary, ArrayList<Tower> neighbor, Exception e);
    }

    public static  class TowerComparator implements Comparator<Tower> {
        @Override
        public int compare(Tower o1, Tower o2) {
            return ((Integer) o2.getSignalStrength()).compareTo(o1.getSignalStrength());
        }
    }
    public WebserviceRequest.HttpURLCONNECTION getOffersByTower(String url, Tower tower,WebserviceRequest.Callback callback){
        WebserviceRequest.HttpURLCONNECTION getOffers = new WebserviceRequest.HttpURLCONNECTION();
        getOffers.setUrl(url + "/station/offers?bts=" + tower.getBts());
        getOffers.setRequestMethod("GET");
        getOffers.setCallback(callback);
        getOffers.execute();
        return getOffers;
    }
}
