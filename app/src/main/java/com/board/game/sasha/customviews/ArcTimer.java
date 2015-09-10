package com.board.game.sasha.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.board.game.sasha.R;
import com.board.game.sasha.gui.MainActivity;
import com.board.game.sasha.logutils.LogUtils;

/**
 * Created by sachin on 8/8/2015.
 */
public  class ArcTimer extends View {
    private float sweepAngle=0;
    private ArcTimer view;
    private RectF rectF,rectF1;
    private CustomAnimation anim;
    private   float delta=120;
    private  float startAngle=-90;
    private Paint p ;
    private Paint p1 ;
    private Paint p2;
    private Paint p3;
    private CountDownTimer timer;
    private int count = 4;
    private float sweepdelta = 0;
    private boolean begin = false;
    private boolean isCountDownFinished = false;
    // CONSTRUCTOR
    public ArcTimer(Context context) {
        super(context);
        view = this;
        init();
    }

    public ArcTimer(Context context,AttributeSet attrs) {
        super(context,attrs);
        view = this;
        init();
    }
    public void beginCountDown(){
        timer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtils.LOGD("timerdemo1", "millisUntilFinished" + millisUntilFinished);
                updateSweepAngle();
            }

            @Override
            public void onFinish() {
                LogUtils.LOGD("timert", "onfinish");
                        ((MainActivity) getContext()).startappTimer();
                setVisibility(View.GONE);
                setCountDownFinished(true);
            }
        };

        begin = true;
        timer.start();

    }

    public void setCountDownFinished(boolean finished){
        isCountDownFinished = finished;
    }

    public boolean getCountDownFinished(){
        return isCountDownFinished;
    }


    private void init() {
        p = new Paint();
        p1 = new Paint();
        p2 = new Paint();
        p3 = new Paint();

        anim = new CustomAnimation();
        this.setAnimation(anim);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(1000);

        p.setColor(Color.TRANSPARENT);
        p1.setAntiAlias(true);
        p1.setColor(getResources().getColor(R.color.darkgreen));
        p1.setStyle(Paint.Style.STROKE);
        p1.setStrokeWidth(15);
        p2.setAntiAlias(true);
        p2.setColor(getResources().getColor(R.color.lightgreen));
        p2.setStyle(Paint.Style.STROKE);
        p2.setStrokeWidth(5);
        p3.setAntiAlias(true);
        p3.setColor(getResources().getColor(R.color.white));
        p3.setStyle(Paint.Style.STROKE);
        p3.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 100, getContext().getResources().getDisplayMetrics()));

        sweepdelta = sweepAngle;
        //anim.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(0,getHeight()/2-getWidth()/2,getWidth(),getHeight()/2+getWidth()/2);
       // rectF1 = new RectF(10,getWidth()/8-15,getWidth()/8-15, 7*getWidth()/8+15,7*getWidth()/8+15);
    }

    public void updateSweepAngle(){
        LogUtils.LOGD("timerdemo","updateSweepAngle() called");
        sweepdelta = sweepAngle;
        this.startAnimation(anim);
         count--;
        //view.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("sweep", "ondraw");
        if(begin) {
            Rect bounds = new Rect();
            p3.getTextBounds(String.valueOf(count), 0, String.valueOf(count).length(), bounds);
            int x = ((int) rectF.centerX()) - (bounds.width() / 2);
            int y = ((int) rectF.centerY()) + (bounds.height() / 2);
            canvas.drawOval(rectF, p);
            //canvas.drawOval(rectF1, p);
            canvas.drawArc(rectF, startAngle, sweepAngle, false, p1);
            //canvas.drawArc (rectF1,startAngle,-sweepAngle, false, p2);
            canvas.drawText(String.valueOf(count), x, y, p3);
        }
    }

    public void resetMe() {
        LogUtils.LOGD("sachin","reset called");
        delta=120;
        startAngle=-90;
        count = 4;
        sweepdelta = 0;
        begin = false;
        isCountDownFinished = false;
        if (anim!=null)
           anim.cancel();
        if (timer!=null)
           timer.cancel();
    }

    public void clearAnim(){
        LogUtils.LOGD("sachin", "clear anim called");
        //if (anim!=null)
             anim.cancel();

        //if (timer!=null)
            timer.cancel();
    }

    private class CustomAnimation extends Animation{

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            sweepAngle = interpolatedTime * delta + sweepdelta;
            LogUtils.LOGD("timerdemo", "customanim arctimer.."+sweepAngle);
            view.invalidate();
        }
    }

}


