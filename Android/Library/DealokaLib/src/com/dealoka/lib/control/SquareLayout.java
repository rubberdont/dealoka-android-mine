package com.dealoka.lib.control;

import com.dealoka.lib.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareLayout extends RelativeLayout {
	private final int BasedOn_BOTH = 0;
	private final int BasedOn_WIDTH = 1;
	private final int BasedOn_HEIGHT = 2;
	private int based_on = BasedOn_BOTH;
	public SquareLayout(Context context) {
		super(context);
	}
	public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setInitial(context, attrs);
	}
	public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setInitial(context, attrs);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int size = Math.min(width, height);
		if(based_on == BasedOn_WIDTH) {
			size = width;
		}else if(based_on == BasedOn_HEIGHT) {
			size = height;
		}
		super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
	}
	private void setInitial(final Context context, final AttributeSet attrs) {
		TypedArray a = null;
		try {
			a = context.obtainStyledAttributes(attrs, R.styleable.com_dealoka_lib_square_layout);
			based_on = a.getInt(R.styleable.com_dealoka_lib_square_layout_based_on, BasedOn_BOTH);
		}finally {
			if(a != null) {
				a.recycle();
			}
		}
	}
}