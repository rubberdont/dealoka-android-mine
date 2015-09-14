package codemagnus.com.dealogeolib;

import static codemagnus.com.dealogeolib.utils.GeneralUtils.ISSERVICERUNNING;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import codemagnus.com.dealogeolib.config.GeoConfiguration;
import codemagnus.com.dealogeolib.interfaces.DealokaDataListener;
import codemagnus.com.dealogeolib.interfaces.DetectorListener;
import codemagnus.com.dealogeolib.interfaces.NotificationMessageListener;
import codemagnus.com.dealogeolib.service.DealokaService;
import codemagnus.com.dealogeolib.service.notif.NotificationReceiver;
import codemagnus.com.dealogeolib.tower.Tower;
import codemagnus.com.dealogeolib.wifi.WifiObject;

/**
 * Created by codemagnus on 5/13/15.
 */
public class DealokaGeoLib {

    public static final String TAG                  = DealokaGeoLib.class.getSimpleName();
    public static final String CONFIGURATION_ISNULL = TAG + " configuration cannot be initialized with null";
    public static final String ENDPOINT_EXCEPTION   = "Unable to connect. Url connection is null";
    public static final String SERVICE_EXCEPTION    = "The dealokageolib service is null.";
    public static final String DEVICE_ID_NULL    	= "deviceId is null";

    private static DealokaGeoLib instance;

    private Context context;
    private GeoConfiguration configuration;
    private volatile DealokaService dService;

    private DealokaDataListener dealokaDataListener;

    private Handler mHandler = new Handler();

    private static String RANDOMSESSION;

    private String sendingEndPointUrl;
    private Location lastKnownLocation;

    private SharedPreferences sharedPreferences;

    private DetectorListener detectorListener = new DetectorListener() {
        @Override
        public void onNewTowerDetected(Tower aTower) {
            dealokaDataListener.onTowerDetect(aTower);
        }

        @Override
        public void onLocationChange(Location location) {
            dealokaDataListener.onLocationUpdate(location);
            lastKnownLocation = location;
        }

        @Override
        public void onNewWifiDetected(List<WifiObject> aWifiData) {
            dealokaDataListener.onWifiDetect(aWifiData);
        }

        @Override
        public void onError(String message) {
            dealokaDataListener.onError(message);
        }

        @Override
        public void onWifiRequestResult(WifiObject aWifi) {
            dealokaDataListener.onWifiRequestResult(aWifi);
        }
    };
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dService = ((DealokaService.ServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Runnable bindRunnable = new Runnable() {
        @Override
        public void run() {
            bindService(context);
        }
    };
    private Runnable detectorRunnable = new Runnable() {
        @Override
        public void run() {
            if(dService != null) {
                dService.setDetectorListener(detectorListener);
            }
        }
    };
    public static DealokaGeoLib getInstance(){
        if(instance == null){
            synchronized (DealokaGeoLib.class){
                if(instance == null)
                    instance = new DealokaGeoLib();
            }
        }

        return instance;
    }
    public DealokaGeoLib() {
    }
    public synchronized void init(GeoConfiguration configuration){
        if(configuration == null)
            throw new ExceptionInInitializerError(CONFIGURATION_ISNULL);

        if(this.configuration == null)
            this.configuration = configuration;

        this.context            = configuration.context;	
        this.sendingEndPointUrl = configuration.baseConnectionUrl;
        if(configuration.deviceId == null){
        	throw new NullPointerException(DEVICE_ID_NULL);
        }else if(configuration.deviceId == ""){
        	throw new NullPointerException(DEVICE_ID_NULL);
        }

        this.sharedPreferences  = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        this.sharedPreferences.edit().putString(DealokaService.BASEURL, sendingEndPointUrl).apply();
        if(this.sharedPreferences.getString(DealokaService.DEVICE_ID, "") == "")
        	this.sharedPreferences.edit().putString(DealokaService.DEVICE_ID, this.configuration.deviceId).apply();

        if(!ISSERVICERUNNING(context, DealokaService.class))
            startService();

        mHandler.post(bindRunnable);
    }
    private void startService(){
        Intent serviceIntent = new Intent(context, DealokaService.class);
        context.startService(serviceIntent);
    }
    private void bindService(Context context){
        Intent serviceConnIntent = new Intent(context, DealokaService.class);
        context.bindService(serviceConnIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }
    public void startDetection(DealokaDataListener newListener){
        this.dealokaDataListener   = newListener;
        mHandler.postDelayed(detectorRunnable, 1500);
    }
    public void setEnableSDKcore(Context context, boolean isEnable) {
    	this.sharedPreferences  = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    	this.sharedPreferences.edit().putBoolean(DealokaService.ENABLE_REQUEST, isEnable).apply();
	}
    public void onStop(){
        context.unbindService(serviceConn);
    }
    public void setNotificationListener(NotificationMessageListener newListener){
        NotificationReceiver.setOnNewPushListener(newListener);
    }
    public List<WifiObject> getLastWifis(){
        if(dService != null){
           return dService.getmCurrentWifis();
        }
        return null;
    }
    public static String getRANDOMSESSION(){
        if(RANDOMSESSION.equals("")) {
            RANDOMSESSION =  new SessionIdentifierGenerator().nextSessionId();
        }
        return RANDOMSESSION;
    }
    public static final class SessionIdentifierGenerator {
        @SuppressLint("TrulyRandom")
		private SecureRandom random = new SecureRandom();
        public String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }
    public Location getLastKnownLocation(){
        return this.lastKnownLocation;
    }
}
