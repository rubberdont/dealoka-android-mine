package codemagnus.com.dealogeolib.service;

import static codemagnus.com.dealogeolib.utils.LogUtils.LOGD;
import static codemagnus.com.dealogeolib.utils.LogUtils.LOGE;
import static codemagnus.com.dealogeolib.utils.LogUtils.LOGI;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.TextUtils;
import codemagnus.com.dealogeolib.service.notif.SimpleNotification;
import codemagnus.com.dealogeolib.service.notif.SimpleQueue;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * Created by codemagnus on 1/21/15.
 */
public class AwsService {
    public static final String tag = "AwsService";
    public static final int POLICY_PROPAGATION_TIME_IN_SECONDS = 12;
    public static BasicAWSCredentials credentials = null;
    public static String TOKEN;
    private static GoogleCloudMessaging gcm;
    public static String deviceUID;
    public static String topicARN;
    public static String queueURL;
    public static String endpointRes;
    public static String mTopic = "YOUR TOPIC";
    public static String applicationArn = "YOUR ARN";

    private static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    private static final String prefs_TOKEN = "token";
    private static final String prefs_DEVICEUID = "deviceUid";
    private static final String prefs_ENDP_ARN = "endPointArn";
    private static final String prefs_TOPIC_ARN = "topicArn";
    private static final String prefs_QUEUEURL = "queueURL";
    private static Context context;
    private static CreatePushNotification task;
    public static SharedPreferences prefs;

    public interface EndPointCallback{
        void onSave(String endPoint);
    }

    private static EndPointCallback listener;

    public AwsService(Context context) {
        AwsService.context = context;
        prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
    }

    public void setAWSCredentials() {
        certificateAWS();

        getCredentials();
        deviceUID = getNotifsCredentials(context, prefs_DEVICEUID);

        if (deviceUID.length() == 0)
            deviceUID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        getGCMCredentials();
    }

    //Set up pushnotification
    private static void certificateAWS() {
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    private void getCredentials() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    String accessKeyId = "AKIAJWJ3WCTFYXEU24SA";
                    String secretKey = "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";

                    credentials = new BasicAWSCredentials(accessKeyId, secretKey);
                } catch (Exception e) {
                    LOGE("Loading AWS Credentials", "Error" + e.getMessage());
                }
            }
        };
        t.start();
    }

    public static class CreatePushNotification extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            AmazonSNSClient sns = new AmazonSNSClient(credentials);
            sns.setRegion(Region.getRegion(Regions.US_EAST_1));

            LOGD(tag, "CreatePlatformEndpointAsyncTask TOKEN: " + TOKEN);

            CreatePlatformEndpointRequest req = new CreatePlatformEndpointRequest();

            req.setToken(TOKEN);
            req.setPlatformApplicationArn(applicationArn);
            req.setCustomUserData(deviceUID);
            String newEpRes;
            if (endpointRes == null || endpointRes.equals("")) {
                try {
                    newEpRes = sns.createPlatformEndpoint(req).getEndpointArn();
                    endpointRes = newEpRes;
                } catch (Exception e) {
                    LOGD(tag, "AmazonClient error: " + e.getMessage());
                }
            }
            return endpointRes;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && !result.equals("")) {
                LOGD(tag, "endpointRes: " + result);
                LOGD(tag, "Token: " + TOKEN);
                LOGD(tag, "AppARN: " + applicationArn);
                LOGD(tag, "deviceUID: " + deviceUID);
                createQueue();
//                createTopic();
                subscribeTopic();

                storeNotifsCredentials(context, TOKEN, deviceUID, endpointRes, topicARN, queueURL);
                getGCMCredentials();
            }else{
                onSavedEndPoint("");
            }
        }
    }



    public static void storeNotifsCredentials(Context context, String token, String deviceUid,
                                              String endPointArn, String topicArn, String queueURL) {

        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(prefs_TOKEN, token);
        editor.putString(prefs_DEVICEUID, deviceUid);
        editor.putString(prefs_ENDP_ARN, endPointArn);
        editor.putString(prefs_TOPIC_ARN, topicArn);
        editor.putString(prefs_QUEUEURL, queueURL);

        editor.putInt(APP_VERSION, appVersion);
        editor.apply();

    }

    public static void createTopic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CreateTopicResult result = SimpleNotification.createTopic(deviceUID);
                topicARN = result.getTopicArn();
                LOGD(tag, "TopicARN: " + topicARN);
            }
        }).start();
    }

    public static void createQueue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CreateQueueResult result = SimpleQueue.createQueue(deviceUID);
                    queueURL = result.getQueueUrl();
                    LOGD(tag, "Queue URL: " + queueURL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void subscribeTopic() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    SimpleQueue.allowNotifications(queueURL, mTopic);
                } catch (Throwable e) {
                }
                try {
                    Thread.sleep(POLICY_PROPAGATION_TIME_IN_SECONDS * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    SimpleQueue.getQueueArn(queueURL);
                    SimpleNotification.subscribe(mTopic, "application", endpointRes);

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private static void getGCMCredentials() {
        TOKEN = getNotifsCredentials(context, prefs_TOKEN);
        if (TOKEN.length() == 0)
            TOKEN = registerGCM();

        queueURL = getNotifsCredentials(context, prefs_QUEUEURL);
        topicARN = getNotifsCredentials(context, prefs_TOPIC_ARN);
        endpointRes = getNotifsCredentials(context, prefs_ENDP_ARN);
        if (endpointRes.length() == 0)
            callCreatePlatForm();
    }

    public static String registerGCM() {
        gcm = GoogleCloudMessaging.getInstance(context);
        TOKEN = getRegistrationId(context);
        if (TextUtils.isEmpty(TOKEN)) {
            registerInBackground();
        }
        return TOKEN;
    }

    public static void callCreatePlatForm() {
        task = new CreatePushNotification();
        task.execute();
    }

    private static String getRegistrationId(Context context) {
        prefs = context.getSharedPreferences("YOU CLASS NAME", Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            LOGI(tag, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            LOGI(tag, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LOGD("RegisterActivity", "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private static void storeRegistrationId(Context context, String TOKEN) {
        prefs = context.getSharedPreferences("YOU CLASS NAME", Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        LOGI(tag, "Saving TOKEN on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, TOKEN);
        editor.putInt(APP_VERSION, appVersion);
        editor.apply();
    }

    public static void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    TOKEN = gcm.register("YOUR GOOGLE API PROJECT NUMBER");
                    LOGD("RegisterGCM", "registerInBackground - TOKEN: " + TOKEN);
                    storeRegistrationId(context, TOKEN);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    LOGD("RegisterGCM", "Error: " + msg);
                }
                LOGD("RegisterGCM", "AsyncTask completed: " + msg);
                return msg;
            }

            protected void onPostExecute(String result) {
                new CreatePushNotification().execute();
            }
        }.execute(null, null, null);
    }

    private static String getNotifsCredentials(Context context, String key) {
        prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
        LOGD(tag, "Prefs: key: " + key + " value: " + prefs.getString(key, ""));
        return prefs.getString(key, "");
    }

    public static boolean hasEndPoint() {
        prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
        if (!prefs.getString(prefs_ENDP_ARN, "").equals("")) {
            return true;
        }
        return false;
    }

    private static void onSavedEndPoint(String endPoint){
        if(listener != null){
            listener.onSave(endPoint);
        }
    }

}
