package com.dealoka.app.general.session;

import com.dealoka.lib.General;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
	private final Context context;
	private String token = General.TEXT_BLANK;
	private String email = General.TEXT_BLANK;
	private String first_name = General.TEXT_BLANK;
	private String last_name = General.TEXT_BLANK;
	private String gender = General.TEXT_BLANK;
	private String birthday = General.TEXT_BLANK;
	public UserSession(final Context context) {
		this.context = context;
	}
	public void openSession() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_init_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(SessionConst.session_init_name + "TOKEN", this.token);
		editor.putString(SessionConst.session_init_name + "EMAIL", this.email);
		editor.putString(SessionConst.session_init_name + "FNAME", this.first_name);
		editor.putString(SessionConst.session_init_name + "LNAME", this.last_name);
		editor.putString(SessionConst.session_init_name + "GENDER", this.gender);
		editor.putString(SessionConst.session_init_name + "BIRTHDAY", this.birthday);
		editor.commit();
	}
	public void closeSession() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_init_name, Context.MODE_PRIVATE);  
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(SessionConst.session_init_name + "TOKEN");
		editor.remove(SessionConst.session_init_name + "EMAIL");
		editor.remove(SessionConst.session_init_name + "FNAME");
		editor.remove(SessionConst.session_init_name + "LNAME");
		editor.remove(SessionConst.session_init_name + "GENDER");
		editor.remove(SessionConst.session_init_name + "BIRTHDAY");
		editor.commit();
	}
	public boolean isSession() {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_init_name, Context.MODE_PRIVATE);
		if(General.isNotNull(preferences.getString(SessionConst.session_init_name + "TOKEN", General.TEXT_BLANK))) {
			return true;
		}else {
			return false;
		}
	}
	public final String GetFromSession(final String id) {
		SharedPreferences preferences = this.context.getSharedPreferences(SessionConst.session_init_name, Context.MODE_PRIVATE);  
		if(General.isNotNull(preferences.getString(id, General.TEXT_BLANK) )) {
			return preferences.getString(id, General.TEXT_BLANK);
		}else {
			return General.TEXT_BLANK;
		}
	}
	public void Token(final String token) {
		this.token = token;
	}
	public final String GetToken() {
		this.token = GetFromSession(SessionConst.session_init_name + "TOKEN");
		return this.token;
	}
	public void Init(
			final String email,
			final String first_name,
			final String last_name,
			final String gender,
			final String birthday) {
		this.email = email;
		this.first_name = first_name;
		this.last_name = last_name;
		this.gender = gender;
		this.birthday = birthday;
	}
	public final String GetEmail() {
		this.email = GetFromSession(SessionConst.session_init_name + "EMAIL");
		return this.email;
	}
	public final String GetFName() {
		this.first_name = GetFromSession(SessionConst.session_init_name + "FNAME");
		return this.first_name;
	}
	public final String GetLName() {
		this.last_name = GetFromSession(SessionConst.session_init_name + "LNAME");
		return this.last_name;
	}
	public final String GetGender() {
		this.gender = GetFromSession(SessionConst.session_init_name + "GENDER");
		return this.gender;
	}
	public final String GetBirthday() {
		this.birthday = GetFromSession(SessionConst.session_init_name + "BIRTHDAY");
		return this.birthday;
	}
}