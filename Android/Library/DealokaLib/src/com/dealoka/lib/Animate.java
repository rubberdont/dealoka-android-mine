package com.dealoka.lib;

import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

public class Animate {
	public static void setTranslationX(final View view, final float value) {
		TranslateAnimation anim = new TranslateAnimation(value, value, 0, 0);
		anim.setFillAfter(true);
		anim.setDuration(0);
		view.startAnimation(anim);
	}
	public static void setTranslationX(final View view, final float value, final AnimationListener animation_listener) {
		TranslateAnimation anim = new TranslateAnimation(value, value, 0, 0);
		anim.setFillAfter(true);
		anim.setDuration(500);
		anim.setAnimationListener(animation_listener);
		view.startAnimation(anim);
	}
	public static void setScaleX(final View view, final float value) {
		ScaleAnimation anim = new ScaleAnimation(1, value, 1, 1);
		anim.setFillAfter(true);
		anim.setDuration(0);
		view.startAnimation(anim);
	}
	public static void setScaleY(final View view, final float value) {
		ScaleAnimation anim = new ScaleAnimation(1, 1, 1, value);
		anim.setFillAfter(true);
		anim.setDuration(0);
		view.startAnimation(anim);
	}
}