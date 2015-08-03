package com.dealoka.app.url;

import android.os.AsyncTask;

import com.dealoka.lib.General;

public class URLController {
	public static interface URLCallback {
		public abstract void didURLResponse(final String response);
		public abstract void didURLFailed();
	}
	public static class URLTask extends AsyncTask<Void, Void, Void> {
		private URLCallback url_callback;
		private String url = General.TEXT_BLANK;
		private String response = General.TEXT_BLANK;
		private boolean error = false;
		private boolean run = false;
		public URLTask(final String url, final URLCallback url_callback) {
			this.url = url;
			this.url_callback = url_callback;
		}
		public boolean isRun() {
			return run;
		}
		@Override
		protected void onPreExecute() {
			response = General.TEXT_BLANK;
			error = false;
		}
		@Override
		protected Void doInBackground(Void... params) {
			run = true;
			response = URLManager.urlTask(url);
			if(General.isNotNull(response)) {
				error = false;
			}else {
				error = true;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if(error) {
				url_callback.didURLFailed();
			}else {
				url_callback.didURLResponse(response);
			}
			run = false;
		}
	}
}