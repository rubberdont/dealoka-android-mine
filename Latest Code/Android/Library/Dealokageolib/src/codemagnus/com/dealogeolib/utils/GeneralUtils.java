package codemagnus.com.dealogeolib.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import codemagnus.com.dealogeolib.http.WebService;
import codemagnus.com.dealogeolib.tower.Tower;
import codemagnus.com.dealogeolib.wifi.WifiObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Harold on 12/13/2014.
 */
public class GeneralUtils {

    public static String timeString(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public static JSONObject submitTowerData(Context context, List<Tower> results, Location lastKnownLocation){
        JSONObject object = new JSONObject();
        JSONObject coordinates = new JSONObject();
        try {
            coordinates.put(WebService.LATITUDE,    lastKnownLocation.getLatitude());
            coordinates.put(WebService.LONGITUDE,   lastKnownLocation.getLongitude());
            object.put(WebService.COORDINATES,  coordinates);
            object.put(WebService.TOWERS,        getTowersData(results));
            object.put(WebService.DEVICE_ID,    GeneralUtils.getDeviceID(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static JSONArray getTowersData(List<Tower> results) {
        JSONArray towerArray = new JSONArray();
        for(Tower tower : results) {
            JSONObject object = getTowerObject(tower);
            if(object != null)
                towerArray.put(getTowerObject(tower));
        }
        return towerArray;
    }

    private static  JSONObject getTowerObject(Tower tower){
        JSONObject objTower = null;
        try {
            objTower = new JSONObject();
            objTower.put(WebService.BTS,                    tower.getBts());
            objTower.put(WebService.OPERATOR,               tower.getOperator());
            objTower.put(WebService.LOCATION_AREA_CODE,       tower.getLocationAreaCode());
            objTower.put(WebService.CELL_ID,                  tower.getCellId());
            objTower.put(WebService.SIGNAL_STRENGTH,          tower.getSignalStrength());
            objTower.put(WebService.TOWER_RSSI,               tower.getRssi());
            objTower.put(WebService.PRIMARY_SCRAMBLED_CODE,   tower.getPrimaryScrambleCode());
            objTower.put(WebService.NETWORK_TYPE,           tower.getNetworkType());

            JSONObject objLatLng = new JSONObject();
            if(tower.getLatLng() != null) {
                objLatLng.put(WebService.LATITUDE, tower.getLatLng().latitude);
                objLatLng.put(WebService.LONGITUDE, tower.getLatLng().longitude);
            } else {
                objLatLng.put(WebService.LATITUDE, 0);
                objLatLng.put(WebService.LONGITUDE, 0);
            }

            objTower.put(WebService.TOWER_LATLNG,   objLatLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objTower;
    }

    public static String getOnlyDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        return sdf.format(System.currentTimeMillis());
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy, kk:mm:ss", Locale.getDefault());
        return sdf.format(System.currentTimeMillis());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss", Locale.getDefault());
        return sdf.format(System.currentTimeMillis());
    }

    public static boolean sameList(List<WifiObject> listFromTemp, List<WifiObject> newList){
        if (listFromTemp == null && newList == null){
            return true;
        }
        if((listFromTemp == null) || newList == null || listFromTemp.size() != newList.size()){
            return false;
        }
        listFromTemp = new ArrayList<>(listFromTemp);
        newList = new ArrayList<>(newList);
        if(listFromTemp.containsAll(newList))
            return true;

        if(listFromTemp.size() == newList.size())
            return true;

        return false;
    }

    public static boolean sameTowerLists(List<Tower> listFromTemp, List<Tower> newList){
        if (listFromTemp == null && newList == null){
            return true;
        }
        if((listFromTemp == null) || newList == null || listFromTemp.size() != newList.size()){
            return false;
        }
        listFromTemp = new ArrayList<>(listFromTemp);
        newList = new ArrayList<>(newList);
        if(listFromTemp.containsAll(newList))
            return true;

        if(listFromTemp.size() == newList.size())
            return true;

        return false;
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void changeFont(TextView view, String path){
        Typeface face = Typeface.createFromAsset(view.getContext().getAssets(), path);
        view.setTypeface(face);
    }

    public static String md5(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(), 0, s.length());
            String hash = new BigInteger(1, digest.digest()).toString(16);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            return false;
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Checking if service is running
     */
    public static boolean ISSERVICERUNNING(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        Iterator<RunningServiceInfo> i$ = manager.getRunningServices(Integer.MAX_VALUE).iterator();

        ActivityManager.RunningServiceInfo service;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            service = (ActivityManager.RunningServiceInfo)i$.next();
        } while(!serviceClass.getName().equals(service.service.getClassName()));

        return true;
    }

    public static Calendar stringTimeToCalendar(String strDate, TimeZone timezone) throws java.text.ParseException {
        String FORMAT_DATETIME =  "MMM dd yyyy, kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault());
        sdf.setTimeZone(timezone);

        String dateStr = GeneralUtils.getOnlyDate() + ", " + strDate;
        Date date = sdf.parse(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static boolean checkIfWithInRange(Calendar calendar, Tower tower) {
        Calendar firstLimit= calendar;
        Calendar secondLimit = Calendar.getInstance();
        secondLimit.setTime(firstLimit.getTime());
        secondLimit.add(Calendar.SECOND, -30);
        Calendar towerTime;
        boolean lessTime = false;
        try {
            towerTime = stringTimeToCalendar(tower.getTime(), TimeZone.getDefault());
            //check if time is within 30 secs.
            if (towerTime.getTime().before(firstLimit.getTime())
                    && towerTime.getTime().after(secondLimit.getTime())) {
                lessTime =  true;
            } else if (firstLimit.getTime().equals(towerTime.getTime())) {
                lessTime =  true;
            }else if (secondLimit.getTime().equals(towerTime.getTime())) {
                lessTime =  true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lessTime;
    }
    
    public static int calculateBtsSignal(int rssi){
        int MAX_RSSI = 31;
        final int MIN_RSSI = 0;
        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return 100 - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (100 - 1);
            return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }
    
    public static LatLng midPoints(LatLng point1, LatLng point2){
        double lat1,lon1,lat2;
        double dLon = Math.toRadians(point2.longitude - point1.longitude);

        lat1 = Math.toRadians(point1.latitude);
        lat2 = Math.toRadians(point2.latitude);
        lon1 = Math.toRadians(point1.longitude);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(Math.toDegrees(lat3),Math.toDegrees(lon3));
    }
    
    public static String getDeviceID(final Context context) {
		final TelephonyManager telephony_manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephony_manager.getDeviceId();
	}
}
