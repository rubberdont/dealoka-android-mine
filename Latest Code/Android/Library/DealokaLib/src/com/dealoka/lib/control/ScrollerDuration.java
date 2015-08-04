package com.dealoka.lib.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ScrollerDuration extends Scroller {
	private double mScrollFactor = 1;
	public ScrollerDuration(Context context) {
		super(context);
	}
	public ScrollerDuration(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}
	@SuppressLint("NewApi")
    public ScrollerDuration(Context context, Interpolator interpolator, boolean flywheel) {
		super(context, interpolator, flywheel);
	}
	public void setScrollDurationFactor(double scrollFactor) {
		mScrollFactor = scrollFactor;
	}
	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, (int) (duration * mScrollFactor));
	}
}