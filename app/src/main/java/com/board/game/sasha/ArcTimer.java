package com.board.game.sasha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.board.game.sasha.com.board.game.sasha.logutils.LogUtils;

/**
 * Created by sachin on 8/8/2015.
 */
public  class ArcTimer extends View {
    private float sweepAngle=0;
    private ArcTimer view;
    private RectF rectF,rectF1;
    private Animation anim;
    private   float delta=120;
    private  float startAngle=-90;
    private Paint p = new Paint();
    private Paint p1 = new Paint();
    private Paint p2 = new Paint();
    private float sweepdelta = 0;

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
    private void init() {
        anim = new CustomAnimation();
        this.setAnimation(anim);
        anim.setDuration(1000);
        //DashPathEffect dashPath = new DashPathEffect(new float[]{10,10}, (float)1.0);
        p.setColor(Color.TRANSPARENT);
        p1.setAntiAlias(true);
        p1.setColor(getResources().getColor(R.color.orange));
        p1.setStyle(Paint.Style.STROKE);
        //p1.setPathEffect(dashPath);
        p1.setStrokeWidth(10);

        p2.setAntiAlias(true);
        p2.setColor(getResources().getColor(R.color.green));
        p2.setStyle(Paint.Style.STROKE);
        //p1.setPathEffect(dashPath);
        p2.setStrokeWidth(10);

        sweepdelta = sweepAngle;
        anim.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(getWidth()/4,getHeight()/4, 3*getWidth()/4,3*getHeight()/4);
        rectF1 = new RectF(getWidth()/4-15,getHeight()/4-15, 3*getWidth()/4+15,3*getHeight()/4+15);
    }

    public void updateSweepAngle(){
        LogUtils.LOGD("timerdemo","updateSweepAngle() called");
        sweepdelta = sweepAngle;
        this.startAnimation(anim);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawOval(rectF, p);
        canvas.drawArc (rectF, startAngle,sweepAngle, false, p1);
        canvas.drawArc (rectF1, startAngle,sweepAngle, false, p2);
    }

    private class CustomAnimation extends Animation{

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            sweepAngle = interpolatedTime * delta + sweepdelta;
            LogUtils.LOGD("timerdemo","customanim arctimer.."+sweepAngle);
            view.invalidate();
        }
    }

}


