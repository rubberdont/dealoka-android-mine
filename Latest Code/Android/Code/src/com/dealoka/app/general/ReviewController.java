package com.dealoka.app.general;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.dealoka.app.Home;
import com.dealoka.app.R;
import com.dealoka.app.Review;

public class ReviewController {
	private static final String ReviewFragment = "REVIEW_FRAGMENT";
	public static void open(final Fragment fragment, final String offer_id) {
		Bundle args = new Bundle();
		args.putString("offer_id", offer_id);
		final Review review = new Review();
		review.setArguments(args);
		final FragmentTransaction fragment_transaction = fragment.getFragmentManager().beginTransaction();
		if(Home.instance != null) {
			if(Home.instance.isPopHeader()) {
				fragment_transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			}
		}
		fragment_transaction.add(R.id.lay_child, review, ReviewFragment).addToBackStack(null).commit();
	}
}