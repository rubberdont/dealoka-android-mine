/*
 * 
 * Copyright (C) 2014 CodeMagnus. All Rights Reserved.
 * Created by Edgar Harold C. Reyes
 * eharoldreyes@gmail.com
 * 
 */
package codemagnus.com.dealogeolib.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.List;

import codemagnus.com.dealogeolib.http.model.ValuePair;


public class WebserviceRequest {
			
	public interface Callback {
		void onResult(int responseCode, String responseMessage, Exception exception);
	}

    public interface NullCallback {
        void onResult(int responseCode);
    }
	
	public static class HttpURLCONNECTION extends AsyncTask<Void, Void, Response>{		
		
		private Callback callback;
		private ProgressDialog pd;
		private String url;
		private List<ValuePair> headers;
		private String requestMethod;
		private String parameters;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(pd != null) pd.show();
		}
		
		@Override
		protected Response doInBackground(Void... params) {			
			Response response;
			try {
				response = WebService.HttpUrlConnection(this.url, this.requestMethod, this.headers, this.parameters);
			} catch (Exception e) {
				response = new Response();
				response.setException(e);
			}
			return response;	
		}
		
		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			this.callback.onResult(result.getResponseCode(), result.getResponseMesssage(), result.getException()); 
			if (pd != null && pd.isShowing()) pd.dismiss();			
		}
		
		public void cancel(){
			this.cancel(true);
		}
				
		public Callback getCallback() {
			return callback;
		}

		public void setCallback(Callback callback) {
			this.callback = callback;
		}

		public ProgressDialog getPd() {
			return pd;
		}

		public void setPd(ProgressDialog pd) {
			this.pd = pd;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<ValuePair> getHeaders() {
			return headers;
		}

		public void setHeaders(List<ValuePair> headers) {
			this.headers = headers;
		}
		
		public String getParameters() {
			return parameters;
		}

		public void setParameters(String parameters) {
			this.parameters = parameters;
		}

		public String getRequestMethod() {
			return requestMethod;
		}

		public void setRequestMethod(String requestMethod) {
			this.requestMethod = requestMethod;
		}
	}
	
}
