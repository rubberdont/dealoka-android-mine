package com.dealoka.app.url;

import java.util.HashMap;

import org.apache.http.HttpStatus;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.lib.General;
import com.dealoka.lib.url.URLResponse;
import com.dealoka.lib.url.URLResponse.URLEnum;

public class URLManager {
	public static String urlTask(final String url) {
		String result = General.TEXT_BLANK;
		HashMap<URLEnum, Object> response = URLResponse.get(DealokaApp.getAppContext().getString(R.string.app_name), url);
		if(Integer.valueOf(response.get(URLEnum.StatusCode).toString()) == HttpStatus.SC_OK) {
			result = response.get(URLEnum.Response).toString();
		}
		return result;
	}
}