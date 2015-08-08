package com.board.game.sasha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sachin on 8/4/2015.
 */
public class CustomBackground extends View {
    private Paint fgPaintSel;
    public CustomBackground(Context context, AttributeSet attrs){
        super(context,attrs);
    initResources();
    }

    private void initResources() {
        fgPaintSel = new Paint();
        fgPaintSel.setARGB(255, 0, 0,0);
        fgPaintSel.setStyle(Paint.Style.STROKE);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[] {40,60}, 0));
    }

   private Path path;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        int cols =  width /10;
        int rows = height / 20;
        float x=0,y=0;
        for(int i=0;i<cols;i++) {
            x = x+ cols;
            canvas.drawLine(x, 0, x, getHeight(),fgPaintSel);
        }
        for(int i=0;i<rows;i++) {
            y = y+ rows;

            canvas.drawLine(0, y, getWidth(),y,fgPaintSel);
        }
    }
}
