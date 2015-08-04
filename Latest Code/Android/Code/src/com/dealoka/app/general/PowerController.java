package com.dealoka.app.general;

import com.dealoka.app.DealokaApp;
import com.dealoka.app.R;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PowerController {
	public static void loadLayout(final RelativeLayout layout) {
		int id = 0;
		ImageView image = new ImageView(DealokaApp.getAppContext());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		image.setId(id + 1);
		params.addRule(RelativeLayout.BELOW, ((TextView)layout.findViewById(R.id.lbl_powered)).getId());
		layout.addView(image, params);
	}
}