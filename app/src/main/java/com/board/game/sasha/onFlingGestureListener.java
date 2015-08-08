package com.board.game.sasha;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sachin.c1 on 30-Jul-15.
 */
public abstract class onFlingGestureListener implements View.OnTouchListener {

    private final GestureDetector gdt = new GestureDetector(new GestureListener());
    private  View view;
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        view = v;
        return gdt.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 60;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                toLeft(view,view.getTag());
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                toRight(view, view.getTag());
                return true;
            }
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                toTop(view,view.getTag());
                return true;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                toBottom(view,view.getTag());
                return true;
            }
            return false;
        }
    }

    public abstract void toLeft(View v, Object o);

    public abstract void toRight(View v, Object o);

    public abstract void toTop(View v, Object o);

    public abstract void toBottom(View v, Object o);

}

