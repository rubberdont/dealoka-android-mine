package com.dealoka.app.general;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.lib.General;

import android.os.AsyncTask;

public class BadInternetConnection {
	private final int to_timer = 20;
	private int counter = 0;
	private boolean loop = false;
	public void start() {
		if(loop) {
			return;
		}
		General.executeAsync(new CounterTimer());
	}
	public void stop() {
		loop = false;
	}
	public boolean isRunning() {
		return loop;
	}
	private class CounterTimer extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			if(counter >= to_timer) {
				General.showToast(DealokaApp.getAppContext(), DealokaApp.getAppContext().getString(R.string.text_err_connection_bad), 5000);
			}
		}
		@Override
		protected void onPreExecute() {
			counter = 0;
			loop = true;
		}
		@Override
		protected Void doInBackground(Void... params) {
			while(loop) {
				try {
					Thread.sleep(1000);
					counter++;
					if(counter >= to_timer) {
						loop = false;
					}
				}catch(InterruptedException ex) {}
			}
			return null;
		}
	}
}