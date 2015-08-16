package com.board.game.sasha.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by sachin on 8/15/2015.
 */
public class SlidingViewGroupHolder extends LinearLayout {
    private int childCount;
    public SlidingViewGroupHolder(Context context) {
        super(context);
    }

    public SlidingViewGroupHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childCount = this.getChildCount();
    }
}
