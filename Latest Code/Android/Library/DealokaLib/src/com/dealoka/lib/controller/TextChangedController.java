package com.dealoka.lib.controller;

import java.util.Timer;
import java.util.TimerTask;

import com.dealoka.lib.General;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TextChangedController {
	public interface TextChangedListener {
		public void didTextChanged(final String value);
		public void didTextBlank();
		public void didTextActioned(final String value);
	}
	private EditText text;
	private TextChangedListener text_changed_listener;
	private boolean pressed = false;
	private Timer timer = null;
	public TextChangedController(final Activity activity, final EditText text, final TextChangedListener text_changed_listener) {
		this.text = text;
		this.text_changed_listener = text_changed_listener;
		text.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				start();
			}
		});
		text.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
					stop();
					General.closeKeyboard(activity);
					text_changed_listener.didTextActioned(getText());
					return true;
				}
				return false;
			}
		});
	}
	public void start() {
		if(!pressed) {
			pressed = true;
			timer = new Timer();
			timer.schedule(new ChangedTimer(), 2000, 2000);
		}
	}
	public void stop() {
		pressed = false;
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	private void textChanged() {
		final String value = getText();
		if(General.isNotNull(value)) {
			text_changed_listener.didTextChanged(value);
		}else {
			text_changed_listener.didTextBlank();
		}
	}
	private String getText() {
		String value = text.getText().toString();
		if(!General.isNotNull(value)) {
			value = General.TEXT_BLANK;
		}
		return value;
	}
	private class ChangedTimer extends TimerTask {
		@Override
		public void run() {
			if(pressed) {
				stop();
				textChanged();
			}
		}
	}
}