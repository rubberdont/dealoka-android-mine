package com.dealoka.lib.control;

import com.dealoka.lib.General;
import com.dealoka.lib.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class AmountEditText extends EditText {
	private int amount;
	private String sign;
	public AmountEditText(Context context) {
		super(context);
	}
	public AmountEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setInitial(context, attrs);
	}
	public AmountEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setInitial(context, attrs);
	}
	private void setInitial(final Context context, final AttributeSet attrs) {
		TypedArray a = null;
		try {
			a = context.obtainStyledAttributes(attrs, R.styleable.com_dealoka_lib_amount_edit_text);
			sign = a.getString(R.styleable.com_dealoka_lib_amount_edit_text_sign);
		}finally {
			if(a != null) {
				a.recycle();
			}
		}
		if(!General.isNotNull(sign)) {
			sign = context.getString(R.string.text_amount_sign_dollar);
		}
		this.setKeyListener(new KeyListener() {
			@Override
			public int getInputType() {
				return InputType.TYPE_CLASS_PHONE;
			}
			@Override
			public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DEL) {
					amount /= 10;
					updateAmount();
					return true;
				}
				if(keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 && amount <= 999999) {
					amount = amount * 10 + keyCode - KeyEvent.KEYCODE_0;
					updateAmount();
					return true;
				}
				return true;
			}
			@Override
			public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
				return false;
			}
			@Override
			public boolean onKeyOther(View view, Editable text, KeyEvent event) {
				return false;
			}
			@Override
			public void clearMetaKeyState(View view, Editable content, int states) {
			}
		});
		this.setCursorVisible(false);
	}
	private void updateAmount() {
		if(amount == 0) {
			this.setText(General.TEXT_BLANK);
		}else {
			this.setText(sign + amount);
		}
	}
}