package com.dealoka.lib.social;

import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import com.dealoka.lib.General;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterController {
	private static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	private Twitter twitter;
	private AccessToken token;
	private RequestToken request_token;
	private TwitterCallback twitter_callback;
	private String url_callback = "oauth://mjstone_lib";
//	<intent-filter>
//		<action android:name="android.intent.action.VIEW" />
//		<category android:name="android.intent.category.DEFAULT" />
//		<category android:name="android.intent.category.BROWSABLE" />
//		<data android:scheme="oauth" android:host="mjstone_lib"/>
//    </intent-filter>
	public interface TwitterCallback {
		public abstract void didTwitterConnected();
		public abstract void didTwitterFailed();
		public abstract void didTwitterUpdateSucceed();
		public abstract void didTwitterUpdateFailed();
	}
	public TwitterController(
			final String consumer_key,
			final String consumer_secret,
			final String url_callback,
			final TwitterCallback twitter_callback) {
		this.url_callback = url_callback;
		this.twitter_callback = twitter_callback;
		ConfigurationBuilder configuration_builder = new ConfigurationBuilder();
		configuration_builder.setOAuthConsumerKey(consumer_key);
		configuration_builder.setOAuthConsumerSecret(consumer_secret);
		Configuration configuration = configuration_builder.build();
		twitter = new TwitterFactory(configuration).getInstance();
	}
	public void onCreate(final Intent intent) {
		final Uri uri = intent.getData();
		if(uri != null && uri.toString().startsWith(url_callback)) {
			final String verifier = uri.getQueryParameter(IEXTRA_OAUTH_VERIFIER);
			setAccessToken(verifier);
		}
	}
	public AccessToken getAccessToken() {
		return token;
	}
	public void setAccessToken(final String access_token, final String token_secret) {
		token = new AccessToken(access_token, token_secret);
	}
	public void setAccessToken(final String verifier) {
		if(General.isValidOS()) {
			token = executeValidAccessTokenGetter(verifier);
		}else {
			token = executeNonValidAccessTokenGetter(verifier);
		}
	}
	public void signin(final Activity activity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					request_token = twitter.getOAuthRequestToken(url_callback);
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request_token.getAuthenticationURL()));
					activity.startActivity(intent);
				}catch(TwitterException ex) {
					twitter_callback.didTwitterFailed();
				}
			}
		}).start();
	}
	public void updateStatus(final String status) {
		if(General.isValidOS()) {
			executeValidUpdateStatus(status);
		}else {
			executeNonValidUpdateStatus(status);
		}
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private AccessToken executeValidAccessTokenGetter(final String verifier) {
		try {
			return new AccessTokenGetter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, verifier).get();
		}catch(InterruptedException ex) {
		}catch(ExecutionException ex) {}
		return null;
	}
	private AccessToken executeNonValidAccessTokenGetter(final String verifier) {
		try {
			return new AccessTokenGetter().execute(verifier).get();
		}catch(InterruptedException ex) {
		}catch(ExecutionException ex) {}
		return null;
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private twitter4j.Status executeValidUpdateStatus(final String status) {
		try {
			return new UpdateStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, status).get();
		}catch(InterruptedException ex) {
		}catch(ExecutionException ex) {}
		return null;
	}
	private twitter4j.Status executeNonValidUpdateStatus(final String status) {
		try {
			return new UpdateStatus().execute(status).get();
		}catch(InterruptedException ex) {
		}catch(ExecutionException ex) {}
		return null;
	}
	private class AccessTokenGetter extends AsyncTask<String, Integer, AccessToken> {
		@Override
		protected AccessToken doInBackground(String... arg0) {
			try {
				return twitter.getOAuthAccessToken(request_token, arg0[0]);
			}catch(TwitterException ex) {
				return null;
			}
		}
		@Override
		protected void onPostExecute(AccessToken result) {
			super.onPostExecute(result);
			if(result != null) {
				twitter_callback.didTwitterConnected();
			}else {
				twitter_callback.didTwitterFailed();
			}
		}
	}
	private class UpdateStatus extends AsyncTask<String, Integer, twitter4j.Status> {
		@Override
		protected twitter4j.Status doInBackground(String... params) {
			try {
				return twitter.updateStatus(params[0]);
			}catch(TwitterException ex) {
				return null;
			}
		}
		@Override
		protected void onPostExecute(twitter4j.Status result) {
			super.onPostExecute(result);
			if (result != null) {
				twitter_callback.didTwitterUpdateSucceed();
			}else {
				twitter_callback.didTwitterUpdateFailed();
			}
		}
	}
}