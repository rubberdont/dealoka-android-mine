package codemagnus.com.dealogeolib.config;


import android.content.Context;

import codemagnus.com.dealogeolib.interfaces.DetectorListener;

/**
 * Created by codemagnus on 4/6/15.
 */
public final class GeoConfiguration {
    public final Context context;
    public static boolean writeDebugLogs = false;

    public final String baseConnectionUrl;
    public final String socketConnectionUrl;

    public final boolean appendCoordinates;
    public final boolean submitData;
    public final boolean disableSocketIO;

    public final DetectorListener mDetectorListener;

    private GeoConfiguration(Builder builder) {
        context             = builder.context;
        baseConnectionUrl   = builder.baseConnectionUrl;
        socketConnectionUrl = builder.socketConnectionUrl;
        appendCoordinates   = builder.appendCoordinates;
        writeDebugLogs      = builder.writeDebugLogs;
        submitData          = builder.canSubmitData;
        disableSocketIO     = builder.disableSocket;
        mDetectorListener   = builder.mDetectorListener;
    }

    public static class Builder {
        private Context context;
        private String baseConnectionUrl;
        private String socketConnectionUrl;
        private boolean writeDebugLogs      = false;
        private boolean appendCoordinates   = false;
        private boolean canSubmitData       = true;
        private boolean disableSocket       = false;

        private DetectorListener mDetectorListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setEndpoint(String anUrl){
            this.baseConnectionUrl = anUrl;
            return this;
        }
        public Builder setSocketConnectionUrl(String anSocketUrl){
            this.socketConnectionUrl = anSocketUrl;
            return this;
        }
        public Builder appendUserCoordinates(){
            this.appendCoordinates = true;
            return this;
        }
        public Builder writeDebugLogs(){
            this.writeDebugLogs = true;
            return this;
        }
        public Builder disableDataSubmission(){
            this.canSubmitData = false;
            return this;
        }
        public Builder disableSocketEmit(){
            this.disableSocket = true;
            return this;
        }
        public Builder setDetectorListener(DetectorListener newListener){
            this.mDetectorListener = newListener;
            return this;
        }
        public GeoConfiguration build(){
            return new GeoConfiguration(this);
        }
    }
}
