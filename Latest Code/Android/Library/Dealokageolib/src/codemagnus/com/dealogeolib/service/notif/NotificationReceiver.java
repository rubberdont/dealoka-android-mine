package codemagnus.com.dealogeolib.service.notif;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import codemagnus.com.dealogeolib.interfaces.NotificationMessageListener;

import static codemagnus.com.dealogeolib.utils.LogUtils.LOGD;
import static codemagnus.com.dealogeolib.utils.LogUtils.LOGE;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        JSONObject pushObj = new JSONObject();
        String jsonData;
        LOGE("NotificationReceiver", "action : " + intent.getAction());
        try {
            for (String key : extras.keySet()) {
                pushObj.put(key, extras.getString(key));
            }
            jsonData = pushObj.getString("Data");
            LOGD("onReceive", "jsonData : " + jsonData);
            onPushReceived(jsonData);
        } catch (JSONException e) {
            jsonData = "";
        }
    }
    private static NotificationMessageListener listener;

    public static void setOnNewPushListener(NotificationMessageListener newListener) {
        listener = newListener;
    }

    private void onPushReceived(String data) {
        if (listener != null) {
            listener.onReceive(data);
        }
    }
}
