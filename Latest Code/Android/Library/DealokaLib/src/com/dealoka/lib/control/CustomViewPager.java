package com.dealoka.lib.control;

import java.lang.reflect.Field;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

public class CustomViewPager extends ViewPager {
	private ScrollerDuration mScroller = null;
	public CustomViewPager(Context context) {
		super(context);
		postInitViewPager();
	}
	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		postInitViewPager();
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
	private void postInitViewPager() {
		try {
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field interpolator = viewpager.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);
			mScroller = new ScrollerDuration(getContext(), (Interpolator)interpolator.get(null));
			scroller.set(this, mScroller);
		}catch(Exception e) {}
	}
	public void setScrollDurationFactor(double scrollFactor) {
		mScroller.setScrollDurationFactor(scrollFactor);
	}
}