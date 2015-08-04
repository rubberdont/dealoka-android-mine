package com.dealoka.app;

import com.dealoka.app.general.GlobalController;
import com.dealoka.app.general.GlobalVariables;
import com.dealoka.app.url.URLConfig;
import com.dealoka.lib.General;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class Review extends Fragment {
	public static Review instance;
	private ImageButton btn_header;
	private WebView web_view;
	private String offer_id = General.TEXT_BLANK;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		try {
			final View view = inflater.inflate(R.layout.review, container, false);
			offer_id = getArguments().getString("offer_id");
			setInitial(view);
			return view;
		}catch(InflateException ex) {}
		return null;
	}
	@Override
	public void onDestroyView() {
		instance = null;
		super.onDestroyView();
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void setInitial(final View view) {
		btn_header = (ImageButton)view.findViewById(R.id.btn_header);
		web_view = (WebView)view.findViewById(R.id.web_view);
		btn_header.setSelected(Home.instance.isPopHeader());
		web_view.setWebViewClient(new WebClient());
		web_view.getSettings().setLoadsImagesAutomatically(true);
		web_view.getSettings().setJavaScriptEnabled(true);
		web_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		web_view.loadUrl(String.format(URLConfig.url_review, GlobalVariables.user_session.GetToken(), offer_id));
		btn_header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GlobalController.pop();
			}
		});
	}
	private class WebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}