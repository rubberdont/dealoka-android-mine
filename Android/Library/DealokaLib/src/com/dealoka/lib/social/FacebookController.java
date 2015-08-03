package com.dealoka.lib.social;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class FacebookController {
	public static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");
	private static final String DEFAULT_BIRTHDAY = "01/01/1990";
	private static final String DEFAULT_GENDER = "Male";
	public static class FB_INFO {
		public String token;
		public long expiry;
		public String fb_id;
		public String email;
		public String first_name;
		public String last_name;
		public String birthday;
		public String gender;
	}
	public interface FacebookSigninCallback {
		public abstract void didFBSignIn(final FB_INFO fb_info);
		public abstract void didFBSignInError();
		public abstract void didFBSignInCancel();
	}
	public interface FacebookShareCallback {
		public abstract void didFBShareSucceed();
		public abstract void didFBShareError();
		public abstract void didFBShareCancel();
	}
	public static List<String> PERMISSIONS = Arrays.asList("email");
	private FacebookSigninCallback facebook_signin_callback;
	private CallbackManager callback_manager;
	private ShareDialog share_dialog;
	public FacebookController(final Context context) {
		FacebookSdk.sdkInitialize(context);
		callback_manager = CallbackManager.Factory.create();
	}
	public void onActivityResult(final int request_code, final int result_code, final Intent data) {
		callback_manager.onActivityResult(request_code, result_code, data);
	}
	public void signin(final Fragment fragment, final List<String> permissions, final FacebookSigninCallback facebook_signin_callback) {
		this.facebook_signin_callback = facebook_signin_callback;
		if(permissions != null) {
			PERMISSIONS = permissions;
		}
		LoginManager.getInstance().registerCallback(callback_manager, new com.facebook.FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult result) {
				fetchUserInfo();
			}
			@Override
			public void onCancel() {}
			@Override
			public void onError(FacebookException error) {
				facebook_signin_callback.didFBSignInError();
			}
	    });
		LoginManager.getInstance()
			.setDefaultAudience(DefaultAudience.EVERYONE)
			.logInWithReadPermissions(fragment, PERMISSIONS);
	}
	public void signin(final Activity activity, final List<String> permissions, final FacebookSigninCallback facebook_signin_callback) {
		this.facebook_signin_callback = facebook_signin_callback;
		if(permissions != null) {
			PERMISSIONS = permissions;
		}
		LoginManager.getInstance().registerCallback(callback_manager, new com.facebook.FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult result) {
				fetchUserInfo();
			}
			@Override
			public void onCancel() {}
			@Override
			public void onError(FacebookException error) {
				facebook_signin_callback.didFBSignInError();
			}
	    });
		LoginManager.getInstance()
			.setDefaultAudience(DefaultAudience.EVERYONE)
			.logInWithReadPermissions(activity, PERMISSIONS);
	}
	public void signinAsPublish(final Fragment fragment, final List<String> permissions, final FacebookSigninCallback facebook_signin_callback) {
		this.facebook_signin_callback = facebook_signin_callback;
		if(permissions != null) {
			PERMISSIONS = permissions;
		}
		LoginManager.getInstance().registerCallback(callback_manager, new com.facebook.FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult result) {
				fetchUserInfo();
			}
			@Override
			public void onCancel() {
				facebook_signin_callback.didFBSignInCancel();
			}
			@Override
			public void onError(FacebookException error) {
				facebook_signin_callback.didFBSignInError();
			}
	    });
		LoginManager.getInstance()
			.setDefaultAudience(DefaultAudience.EVERYONE)
			.logInWithPublishPermissions(fragment, PERMISSIONS);
	}
	public void signinAsPublish(final Activity activity, final List<String> permissions, final FacebookSigninCallback facebook_signin_callback) {
		this.facebook_signin_callback = facebook_signin_callback;
		if(permissions != null) {
			PERMISSIONS = permissions;
		}
		LoginManager.getInstance().registerCallback(callback_manager, new com.facebook.FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult result) {
				fetchUserInfo();
			}
			@Override
			public void onCancel() {
				facebook_signin_callback.didFBSignInCancel();
			}
			@Override
			public void onError(FacebookException error) {
				facebook_signin_callback.didFBSignInError();
			}
	    });
		LoginManager.getInstance()
			.setDefaultAudience(DefaultAudience.EVERYONE)
			.logInWithPublishPermissions(activity, PERMISSIONS);
	}
	public void signout() {
		LoginManager.getInstance().logOut();
	}
	public void share(
			final Fragment fragment,
			final String title,
			final String description,
			final String link,
			final FacebookShareCallback facebook_share_callback) {
		share_dialog = new ShareDialog(fragment);
		share_dialog.registerCallback(callback_manager, new com.facebook.FacebookCallback<Sharer. Result>() {
			@Override
			public void onCancel() {
				facebook_share_callback.didFBShareCancel();
			}
			@Override
			public void onError(FacebookException error) {
				facebook_share_callback.didFBShareError();
			}
			@Override
			public void onSuccess(com.facebook.share.Sharer.Result result) {
				facebook_share_callback.didFBShareSucceed();
			}
		});
		if(ShareDialog.canShow(ShareLinkContent.class)) {
			ShareLinkContent link_content = new ShareLinkContent.Builder()
				.setContentTitle(title)
				.setContentDescription(description)
				.setContentUrl(Uri.parse(link))
				.build();
			share_dialog.show(link_content);
		}
	}
	public void fetchUserInfo() {
		final AccessToken access_token = AccessToken.getCurrentAccessToken();
		if(access_token != null) {
			GraphRequest request = GraphRequest.newMeRequest(access_token, new GraphRequest.GraphJSONObjectCallback() {
				@Override
				public void onCompleted(JSONObject me, GraphResponse response) {
					if(me == null) {
						return;
					}
					FB_INFO fb_info = new FB_INFO();
					fb_info.token = AccessToken.getCurrentAccessToken().getToken();
					fb_info.expiry = AccessToken.getCurrentAccessToken().getExpires().getTime();
					fb_info.fb_id = me.optString("id");
					fb_info.first_name = me.optString("first_name");
					fb_info.last_name = me.optString("last_name");
					if(me.has("birthday")) {
						fb_info.birthday = me.optString("birthday");
					}else {
						fb_info.birthday = FacebookController.DEFAULT_BIRTHDAY;
					}
					fb_info.email = me.optString("email");
					if(me.has("gender")) {
						fb_info.gender = me.optString("gender");
					}else {
						fb_info.gender = FacebookController.DEFAULT_GENDER;
					}
					facebook_signin_callback.didFBSignIn(fb_info);
				}
			});
			GraphRequest.executeBatchAsync(request);
        }
    }
}