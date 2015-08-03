package com.dealoka.lib.controller;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSController {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public static ArrayList<Object> parseFromReceiver(final Intent intent) {
		ArrayList<Object> value = null;
		if(intent.getAction() != null && intent.getAction().equals(ACTION)) {
			Bundle bundle = intent.getExtras();
			if(bundle != null) {
				Object[] pdus = (Object[])bundle.get("pdus");
				value = new ArrayList<Object>();
				for(Object each_pdus : pdus) {
					SmsMessage current_message = SmsMessage.createFromPdu((byte[])each_pdus);
					final String number = current_message.getDisplayOriginatingAddress();
					final String message = current_message.getDisplayMessageBody();
					value.add(new SMSController().new SMS(number, message));
				}
			}
		}
		return value;
	}
	public static void sendSMS(final String number, final String message) {
		SmsManager sms_manager = SmsManager.getDefault();
		try {
			sms_manager.sendTextMessage(number, null, message, null, null);
		}catch(Exception ex) {
		}
	}
	public class SMS {
		public String number;
		public String message;
		public SMS(
				final String nb,
				final String msg) {
			number = nb;
			message = msg;
		}
	}
}