package codemagnus.com.dealogeolib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PopupImageView extends ImageView{
	
	public PopupImageView(Context context) {
        super(context);
    }

    public PopupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PopupImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
	
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	// TODO Auto-generated method stub
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	int width = getMeasuredWidth();
    	int height = getMeasuredWidth() / 2;
    	setMeasuredDimension(width, height);
    }

}
