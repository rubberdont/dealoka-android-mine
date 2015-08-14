package codemagnus.com.dealogeolib.service;

import static codemagnus.com.dealogeolib.utils.LogUtils.LOGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.IBinder;
import codemagnus.com.dealogeolib.http.WebserviceRequest;
import codemagnus.com.dealogeolib.interfaces.DetectorListener;
import codemagnus.com.dealogeolib.tower.Tower;
import codemagnus.com.dealogeolib.tower.TowerManager;
import codemagnus.com.dealogeolib.utils.GeneralUtils;
import codemagnus.com.dealogeolib.wifi.WifiDataUtils;
import codemagnus.com.dealogeolib.wifi.WifiHelper;
import codemagnus.com.dealogeolib.wifi.WifiObject;

/**
 * Created by codemagnus on 5/5/15.
 */
public class DealokaService extends Service{
    private Context context;

    //Tag for shared preference
    public static final String BASEURL = "baseURL";
    public static final String ENABLE_REQUEST = "enableRequest";

    public static final String OFFERS_FROM_TOWER = "com.codemagnus.dealokageolib.NEW_OFFERS";
    public static final String NEW_OFFERS = "new_offers";
    private String baseUrl;

    //Declaring current List
    private List<Tower> currentTowers = new ArrayList<>();
    private List<WifiObject> mCurrentWifis = new ArrayList<>();

    //Declaring utility objects
    private TowerManager mTowerManager;
    private WifiHelper mWifiHelper;
    private WifiDataUtils mWifiUtility;

    private DetectorListener detectorListener;

    private SharedPreferences sharedPreferences;

    private static final long ONE_MINUTE = 60 * 1000;
    private Timer towerReqTimer;
    private Timer wifiReqTimer;

    private boolean canGetOffers;
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context         = DealokaService.this;
        mTowerManager   = new TowerManager(this);
        mWifiHelper     = new WifiHelper(this);

        sharedPreferences = context.getSharedPreferences(BASEURL, MODE_PRIVATE);
        baseUrl         = sharedPreferences.getString(BASEURL, "");
        canGetOffers	= sharedPreferences.getBoolean(ENABLE_REQUEST, true);
        setUpTowerListener();
        setUpWifiListener();
    }

    private void setUpTowerListener(){
            mTowerManager.setOnPrimaryTowerChangedListener(new TowerManager.PrimaryTowersChangeCallback() {
                @Override
                public void didTowersChanged(ArrayList<Tower> towersPrimary, ArrayList<Tower> neighbor, Exception e) {

                    if (towersPrimary != null && towersPrimary.size() > 0) {
                        //merging two list
                        if (neighbor != null)
                            towersPrimary.addAll(towersPrimary.size(), neighbor);

                        if (currentTowers.size() > 0) {
                            if (!GeneralUtils.sameTowerLists(currentTowers, towersPrimary)) {

                                if (towersPrimary.get(0) != currentTowers.get(0)) {

                                    LOGE("DealokaService", "primaryTowerListener : " + towersPrimary.get(0));
                                    LOGE("DealokaService", "primaryTowerListener : " + currentTowers.get(0));
                                } else {

                                    LOGE("DealokaService", "primaryTowerListener : still same");

                                }
                                //Replace recent list of towers
                                currentTowers = towersPrimary;
                            }
                        } else {
                            currentTowers = towersPrimary;

                            LOGE("DealokaService", "primaryTowerListener first tower: " + towersPrimary.get(0));
                            LOGE("DealokaService", "primaryTowerListener tower size: " + towersPrimary.size());
                        }

                    }
                }
            });

        mTowerManager.setOnTowersChangedListener(new TowerManager.TowersChangeCallback() {
            @Override
            public void didTowersChanged(ArrayList<Tower> towers, Exception e) {
                LOGE("DealokaService", "towerListener : " + towers.toString());
               
                if (currentTowers.size() > 0) {

                    if (currentTowers.get(0).getCellId() != towers.get(0).getCellId()) {
                        //Sending towers to server
                        getOffers(towers.get(0));
                        
                        towerDetected(towers.get(0));
                    }
                    currentTowers = towers;
                }
            }

        });
    }

    private void setUpWifiListener() {
        setUpWifiUtility();
        mWifiHelper.setScanResultsListener(new WifiHelper.ScanResultsListener() {
            @Override
            public void onChange(List<ScanResult> scanResults) {
                for (ScanResult result : scanResults) {
                    LOGE("DealokaService", "WifiResult : " + result.BSSID + " : " + result.SSID);
                }

                List<WifiObject> list = mWifiUtility.getWifiObjects(scanResults, "");
                getWifiOffers(list);
                mCurrentWifis = list;
                wifiDetected(list);
//                List<WifiObject> tempList = new ArrayList<>();
//                tempList.addAll(list);
//                if(!tempList.isEmpty())
//                    wifiDetected(tempList);
//
//                if (mCurrentWifis.size() == 0) {
//                    LOGE("WIFI RESULT", "size == 0");
//                    if (list != null) {
//                        mCurrentWifis.addAll(list);
//
//                        mCurrentWifiObject = mCurrentWifis.get(0);
//
//                        //start location service if available
//                        useLocationService();
//
//                        mCurrentWifiObject = mCurrentWifis.get(0);
//                        getWifiOffers(mCurrentWifis);
//                    }
//
//                } else {
//                    for (WifiObject obj : mCurrentWifis) {
//                        LOGE("DealokaService", "Current WifiResult : " + obj.getScanResult().BSSID + " : " + obj.getScanResult().SSID);
//                    }
//                    if (!GeneralUtils.sameList(mCurrentWifis, list)) {
//                        LOGE("WIFI RESULT", "Not Same");
//                        List<WifiObject> newAdded = mWifiUtility.getNewAddedWifi(mCurrentWifis, list);
//                        if (!newAdded.isEmpty()) {
//                            getWifiOffers(newAdded);
//                        } else {
//                            LOGE("newAdded", newAdded.size() + "");
//                        }
//
//                        //start location service if available
//                        useLocationService();
//
//                        mCurrentWifis.clear();
//                        mCurrentWifis.addAll(tempList);
//                        mCurrentWifiObject = mCurrentWifis.get(0);
//                    }
//
//                }
            }
        });
    }

    private void setUpWifiUtility(){
        mWifiUtility = new WifiDataUtils(context, mWifiHelper);
    }

    public void setDetectorListener(DetectorListener newLister){
        this.detectorListener = newLister;
    }

    private void towerDetected(Tower aTower){
        if (detectorListener != null)
            detectorListener.onNewTowerDetected(aTower);
    }

    private void wifiDetected(List<WifiObject> aWifis){
        if (detectorListener != null)
            detectorListener.onNewWifiDetected(aWifis);
    }

    private void getOffers(final Tower tower){
    	if(!canGetOffers){
        	return;
        }
        if(towerReqTimer != null){
            towerReqTimer.cancel();
        }

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(mTowerManager.getMobileCountryCode())
                .append("-")
                .append(mTowerManager.getMobileNetworkCode())
                .append("-")
                .append(tower.getLocationAreaCode())
                .append("-")
                .append(tower.getCellId());

        tower.setBts(sBuilder.toString());

        mTowerManager.getOffersByTower(baseUrl, tower, new WebserviceRequest.Callback() {
            @Override
            public void onResult(int responseCode, String responseMessage, Exception exception) {
                if (responseCode == 200 && exception == null) {
                    LOGE("GET OFFERS", "response : " + responseMessage);
                    JSONArray offers = getOffersFromArray(responseMessage);
                    if (offers.length() > 0) {
                        sendBroadcastToApp(offers.toString());
                    }
                } else {
                	if(responseMessage != null){
                		return;
                	}
                	 towerReqTimer = new Timer();
                     TimerTask task = new TimerTask() {
                         @Override
                         public void run() {
                             getOffers(tower);
                         }
                     };
                     towerReqTimer.schedule(task, ONE_MINUTE);
                }
            }
        });
    }

    private void getWifiOffers(final List<WifiObject> wifiObjects){
    	if(!canGetOffers){
        	return;
        }
        if(wifiReqTimer != null){
            wifiReqTimer.cancel();
        }
        String BSSIDArray = mWifiUtility.getWifiBSSID(wifiObjects).toString();
        mWifiHelper.getOffersByWifi(baseUrl, BSSIDArray, new WebserviceRequest.Callback() {
            @Override
            public void onResult(int responseCode, String responseMessage, Exception exception) {
                if (responseCode == 200 && exception == null) {
                    LOGE("POST WIFI", responseMessage);
                    JSONArray offersArray = getOffersFromArray(responseMessage);
                    if (offersArray.length() > 0) {
                        sendBroadcastToApp(offersArray.toString());
                    }
                } else {
                    if (responseMessage != null) {
                       return;  
                    }
                    
                    wifiReqTimer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            getWifiOffers(wifiObjects);
                        }
                    };
                    wifiReqTimer.schedule(task, ONE_MINUTE);
                }
            }
        });
    }

    private JSONArray getOffersFromArray(String data) {
        JSONArray array;
        JSONArray offersArray = new JSONArray();
        try {
            array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
				JSONObject wifiObj = array.getJSONObject(i);
				if(wifiObj.has("offers")){
					JSONArray offerAry = wifiObj.getJSONArray("offers");
					if(offerAry.length() > 0){
						for (int j = 0; j < offerAry.length(); j++) {
							offersArray.put(offerAry.getJSONObject(j));
						}
					}
				}
			}
        } catch (JSONException e) { e.printStackTrace(); }

        return offersArray;
    }
    private void sendBroadcastToApp(String offersArray){
        Intent offerIntent = new Intent(OFFERS_FROM_TOWER);
        offerIntent.setAction(OFFERS_FROM_TOWER);
        offerIntent.putExtra(NEW_OFFERS, offersArray);
        sendBroadcast(offerIntent);
        LOGE("sendBroadcastToApp", "I think it's sending...");
    }

    public List<WifiObject> getmCurrentWifis() {
        return mCurrentWifis;
    }


    //Binder to access method from this service
    public class ServiceBinder extends Binder{
        public DealokaService getService(){
            return DealokaService.this;
        }
    }

}
