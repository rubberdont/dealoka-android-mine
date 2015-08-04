package com.dealoka.app.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;
import com.dealoka.app.activity.Category.CategoryListener;
import com.dealoka.app.activity.Gender.GenderListener;
import com.dealoka.app.call.CallConst;
import com.dealoka.app.call.CallController;
import com.dealoka.app.call.CallController.CallListener;
import com.dealoka.app.general.Config;
import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.lib.General;
import com.dealoka.lib.GeneralCalendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Register implements CategoryListener, GenderListener {
	public static Register instance;
	public static final String RegisterView = "REGISTER_VIEW";
	private LinearLayout lay_main;
	private DatePickerDialog dtp_birthdate;
	private EditText txt_username;
	private EditText txt_email;
	private EditText txt_password;
	private EditText txt_conf_password;
	private Button btn_gender;
	private Button btn_birthdate;
	private Button btn_category;
	private CheckBox chk_tc;
	private Button btn_register;
	private Button btn_tc;
	private ArrayList<String> category = new ArrayList<String>();
	private Calendar cal_min = Calendar.getInstance();
	private Calendar cal_max = Calendar.getInstance();
	private String gender = General.TEXT_BLANK;
	private String birthdate = General.TEXT_BLANK;
	private String username;
	private String email;
	private String password;
	private String conf_password;
	public Register(final View view) {
		setInitial(view);
		instance = this;
	}
	@Override
	public void didCategorySelected(List<String> key, List<String> value) {
		category = (ArrayList<String>)key;
		String text = General.TEXT_BLANK;
		if(value.size() > 0) {
			for(String each : value) {
				text += each + ", ";
			}
			text = text.substring(0, text.length() - 2);
		}
		if(General.isNotNull(text)) {
			btn_category.setText(text);
		}else {
			btn_category.setText(DealokaApp.getAppContext().getString(R.string.button_category));
		}
	}
	@Override
	public void didCategoryCancelled() {}
	@Override
	public void didGenderSelected(String key, String value) {
		gender = key;
		btn_gender.setText(value);
	}
	@Override
	public void didGenderCancelled() {}
	public void setAlpha() {
		General.setAlpha(lay_main, Config.alpha);
	}
	public void destroy() {
		instance = null;
	}
	private void setInitial(final View view) {
		lay_main = (LinearLayout)view.findViewById(R.id.lay_main);
		txt_username = (EditText)view.findViewById(R.id.txt_username);
		txt_email = (EditText)view.findViewById(R.id.txt_email);
		txt_password = (EditText)view.findViewById(R.id.txt_password);
		txt_conf_password = (EditText)view.findViewById(R.id.txt_conf_password);
		btn_register = (Button)view.findViewById(R.id.btn_register);
		btn_gender = (Button)view.findViewById(R.id.btn_gender);
		btn_birthdate = (Button)view.findViewById(R.id.btn_birthdate);
		btn_category = (Button)view.findViewById(R.id.btn_category);
		chk_tc = (CheckBox)view.findViewById(R.id.chk_tc);
		btn_tc = (Button)view.findViewById(R.id.btn_tc);
		General.setPasswordEditText(txt_password);
		General.setPasswordEditText(txt_conf_password);
		setEventListener();
		setCalendarView();
	}
	private void setEventListener() {
		btn_gender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openGender();
			}
		});
		btn_birthdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openBirthdate();
			}
		});
		btn_category.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openCategory();
			}
		});
		btn_tc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doTC();
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
	}
	private void setCalendarView() {
		final OnDateSetListener odsl = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				final int min_year = cal_min.get(Calendar.YEAR);
				final int min_month = cal_min.get(Calendar.MONTH);
				final int min_day = cal_min.get(Calendar.DAY_OF_MONTH);
				final int max_year = cal_max.get(Calendar.YEAR);
				final int max_month = cal_max.get(Calendar.MONTH);
				final int max_day = cal_max.get(Calendar.DAY_OF_MONTH);
				Calendar set = Calendar.getInstance();
				set.set(year, monthOfYear, dayOfMonth);
				if(set.after(cal_max)) {
					view.updateDate(max_year, max_month, max_day);
				}else if(set.before(cal_min)) {
					view.updateDate(min_year, min_month, min_day);
				}
				btn_birthdate.setText((String)(set.get(Calendar.DATE) + " " + GeneralCalendar.getMonthFullName(DealokaApp.getAppContext(), set) + " " + set.get(Calendar.YEAR)));
				birthdate = GeneralCalendar.getStringFromCalendar(set, Config.date_format);
			}
		};
		Calendar in = Calendar.getInstance();
		in.add(Calendar.YEAR, -27);
		dtp_birthdate = new DatePickerDialog(
				Land.instance,
				odsl,
				in.get(Calendar.YEAR),
				in.get(Calendar.MONTH),
				in.get(Calendar.DAY_OF_MONTH));
		cal_min.add(Calendar.YEAR, -80);
		GeneralCalendar.setMinMaxDatePicker(dtp_birthdate, cal_min, cal_max);
	}
	private void register() {
		if(!GlobalController.isNetworkConnected(DealokaApp.getAppContext(), true)) {
			return;
		}
		if(!chk_tc.isChecked()) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_tc_unchecked));
			return;
		}
		General.closeKeyboard(Land.instance);
		username = txt_username.getText().toString();
		email = txt_email.getText().toString();
		password = txt_password.getText().toString();
		conf_password = txt_conf_password.getText().toString();
		if(!General.isLengthValid(username, Config.text_length_valid)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_username_blank));
			return;
		}else if(!General.isEmailValid(email)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_email_blank));
			return;
		}else if(!General.isLengthValid(password, Config.text_length_valid)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_password_blank));
			return;
		}else if(!General.isLengthValid(conf_password, Config.text_length_valid) ||
				!password.equals(conf_password)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_conf_password_not_match));
			return;
		}else if(!General.isNotNull(gender)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_gender_blank));
			return;
		}else if(!General.isNotNull(birthdate)) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_age_blank));
			return;
		}else if(category == null || category.size() <= 0) {
			GlobalController.showAlert(Land.instance, DealokaApp.getAppContext().getString(R.string.text_err_category_blank));
			return;
		}
		General.executeAsync(new PrefetchData());
	}
	private void openCategory() {
		Intent next_intent = new Intent(DealokaApp.getAppContext(), Category.class);
		next_intent.putExtra("from", RegisterView);
		next_intent.putStringArrayListExtra("key", category);
		Land.instance.startActivity(next_intent);
	}
	private void openGender() {
		Intent next_intent = new Intent(DealokaApp.getAppContext(), Gender.class);
		next_intent.putExtra("from", RegisterView);
		next_intent.putExtra("key", gender);
		Land.instance.startActivity(next_intent);
	}
	private void openBirthdate() {
		dtp_birthdate.show();
	}
	private void doTC() {
		GlobalController.openTC(Land.instance);
	}
	private void setContinue(
			final String token,
			final String email,
			final String first_name,
			final String last_name,
			final String gender,
			final String birthday) {
		Land.instance.setContinue(token, email, first_name, last_name, gender, birthday);
	}
	private class PrefetchData extends AsyncTask<Void, Void, Void> {
		private CallController call_controller;
		@Override
		protected void onPostExecute(Void result) {
			call_controller = new CallController(new CallListener() {
				@Override
				public void onCallReturned(String result) {
					General.closeLoading();
					try {
						JSONObject json = new JSONObject(result);
						if(json.getString("message_func").equals(CallConst.api_user_registration)) {
							if(json.getString("message_action").equals("USER_REGISTRATION_SUCCESSFUL")) {
								JSONObject data = new JSONObject(json.getString("message_data"));
								setContinue(
										json.getString("user_token"),
										data.getString("user_email"),
										data.getString("fname"),
										data.getString("lname"),
										data.getString("gender"),
										data.getString("birthday"));
							}else if(json.getString("message_action").equals("USER_REGISTRATION_FAILED_ACCOUNT_ALREADY_EXISTS")) {
								GlobalController.showAlert(Land.instance, json.getString("message_desc"));
							}else if(json.getString("message_action").equals("USER_REGISTRATION_FAILED")) {
								GlobalController.showAlert(Land.instance, json.getString("message_desc"));
							}
						}
					}catch(JSONException ex) {}
				}
				@Override
				public void onCallFailed() {
					General.closeLoading();
				}
				@Override
				public void onCallConnected() {
					call_controller.callUserRegistration(username, email, password, gender, birthdate, category, GlobalVariables.latitude, GlobalVariables.longitude);
				}
			});
		}
		@Override
		protected void onPreExecute() {
			General.showLoading(Land.instance);
		}
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}
}