package com.test.board;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by sachin on 8/2/2015.
 */
public class Board extends TableLayout {
    private int BOARD_SIZE;
    private int board_width;
    private int board_height;
    private int cell_spacing;
    private int board_padding;
    private int no_rows;
    private int no_cols;
    private Context context;
    private TableRow[] rowArr;
    private Button temp;
    private ArrayList<String> labelList;
    private int[][] pos_array ;
    private int TRANSLATE_OFFSET = 100;
    public Board(Context ctxt, AttributeSet attr){
        super(ctxt, attr);
        context = ctxt;
        this.setWillNotDraw(false);
        this.setClipChildren(false);
    }

    public void initBoard(int size) {
        BOARD_SIZE = size;
        no_rows = size;
        no_cols = size;
        initLabels();
        initPosArray();
        rowArr = new TableRow[no_rows];
        for(int i=0;i<no_rows;i++) {
            rowArr[i] = new TableRow(context);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowArr[i].setLayoutParams(rowParams);
        }
        Collections.shuffle(labelList);
    }

    private void initLabels(){
        labelList = new ArrayList<String>();
        for(int i=1;i<no_rows*no_cols;i++){
            labelList.add(String.valueOf(i));
        }
    }

    private void initPosArray(){
      pos_array = new int[no_rows][];
      for(int i=0;i<no_rows;i++)
          pos_array[i] = new int[no_cols];
      Random rand = new Random();
      int index =  rand.nextInt(no_cols*no_rows);
      for(int i=0;i<no_rows;i++)   //i*SIZE+j
          for(int j=0;j<no_cols;j++) {
              int pos = i*no_rows+j;
              if(pos==index)
                  pos_array[i][j]=0;
              else
                  pos_array[i][j]=1;
          }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initialize();
    }

    private void initialize() {
        int button_dimen = board_width / no_cols;
        TRANSLATE_OFFSET = button_dimen;
        int count =0;
        for (int i = 0; i < no_rows; i++) {
            this.addView(rowArr[i]);
            for (int j = 0; j < no_cols; j++) {
                TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(button_dimen, button_dimen);
                Button button = new Button(context);
                button.setTextAppearance(context,R.style.ButtonText);
                button.setLayoutParams(buttonParams);
                button.setTag(new Coord(i, j));
                button.setBackgroundResource(R.drawable.btn_black_glossy);
                button.setOnTouchListener(new onFlingGestureListenerImpl());
                if(pos_array[i][j]==1)
                    button.setText(labelList.get(count++));
                rowArr[i].addView(button);
                if(pos_array[i][j]==0)
                    rowArr[i].getChildAt(j).setVisibility(View.INVISIBLE);
            }
        }

        }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        board_width = this.getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
    }



    private class Coord{
        int x;
        int y;
        public Coord(int x,int y){
            this.x = x;
            this.y = y;
        }
        public Coord getObj(){
            return this;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }

        public void setXY(int x, int y){
            this.x = x;
            this.y = y;
        }

    }




    public class onFlingGestureListenerImpl extends onFlingGestureListener{
        @Override
        public void toBottom(View v, Object o) {
            int x = ((Coord)o).getX();
            int y = ((Coord)o).getY();

            if(validate_DownMove(x,y))
                animDown((Button)v,x,y);
            else{
                //if(vib.hasVibrator())
                  //  vib.vibrate(100);
                Toast.makeText(context, "InValid Move", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void toLeft(View v, Object o) {
            int x = ((Coord)o).getX();
            int y = ((Coord)o).getY();

            if(validate_LeftMove(x,y))
                animLeft((Button)v,x,y);
            else{
                //if(vib.hasVibrator())
                  //  vib.vibrate(100);
                Toast.makeText(context,"InValid Move",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void toRight(View v, Object o) {
            //Your code here
            int x = ((Coord)o).getX();
            int y = ((Coord)o).getY();

            if(validate_RightMove(x,y))
                animRight((Button)v,x,y);
            else{
              //  if(vib.hasVibrator())
                //    vib.vibrate(100);
                Toast.makeText(context,"InValid Move",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void toTop(View v, Object o) {
            //Your code here
            int x = ((Coord)o).getX();
            int y = ((Coord)o).getY();
            if(validate_UpMove(x,y))
                animTop((Button)v,x,y);
            else{
              //  if(vib.hasVibrator())
                //    vib.vibrate(1000);
                Toast.makeText(context,"InValid Move",Toast.LENGTH_SHORT).show();

            }
        }
    }


    private boolean validate_RightMove(int x,int y){
        if(x < BOARD_SIZE && y < BOARD_SIZE && y+1<BOARD_SIZE){
            if(pos_array[x][y+1]==0)
                return true;
        }
        return  false;
    }

    private boolean validate_LeftMove(int x,int y){
        if(x < BOARD_SIZE && y < BOARD_SIZE && y-1>=0){
            if(pos_array[x][y-1]==0)
                return true;
        }
        return false;
    }

    private boolean validate_UpMove(int x,int y){
        if(x < BOARD_SIZE && y < BOARD_SIZE && x-1>=0){
            if(pos_array[x-1][y]==0)
                return true;
        }
        return false;
    }

    private boolean validate_DownMove(int x,int y){
        if(x < BOARD_SIZE && y < BOARD_SIZE && x+1<BOARD_SIZE){
            if(pos_array[x+1][y]==0)
                return true;
        }
        return false;
    }


    private void animRight(Button btn,final int x,final int y){
        final TableRow row = (TableRow)this.getChildAt(x);
        temp = (Button) row.getChildAt(y);
        final String text = temp.getText().toString();

        TranslateAnimation animRight = new TranslateAnimation(0,TRANSLATE_OFFSET, 0, 0);
        animRight.setDuration(200);
        btn.startAnimation(animRight);
        animRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                temp.setVisibility(View.INVISIBLE);
                temp = (Button) row.getChildAt(y + 1);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                temp.setVisibility(View.VISIBLE);
                temp.setText(text);
                pos_array[x][y] = 0;
                pos_array[x][y + 1] = 1;
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animLeft(Button btn,final int x,final int y){
        final  TableRow row = (TableRow)this.getChildAt(x);
        temp = (Button) row.getChildAt(y);
        final  String text = temp.getText().toString();

        TranslateAnimation animLeft = new TranslateAnimation(0,-TRANSLATE_OFFSET,0, 0);
        animLeft.setDuration(200);
        btn.startAnimation(animLeft);

        animLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                temp.setVisibility(View.INVISIBLE);
                temp = (Button) row.getChildAt(y - 1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                temp.setVisibility(View.VISIBLE);
                temp.setText(text);
                pos_array[x][y] = 0;
                pos_array[x][y - 1] = 1;
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void animTop(Button btn,final int x,final int y){

        final TableRow row = (TableRow)this.getChildAt(x);
        final TableRow row1 = (TableRow)this.getChildAt(x-1);
        temp = (Button) row.getChildAt(y);
        final String text = temp.getText().toString();

        TranslateAnimation animTop = new TranslateAnimation(0, 0, 0, -TRANSLATE_OFFSET);
        animTop.setDuration(200);
        btn.startAnimation(animTop);
        animTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                temp.setVisibility(View.INVISIBLE);
                temp = (Button) row1.getChildAt(y);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                temp.setVisibility(View.VISIBLE);
                temp.setText(text);
                pos_array[x][y] = 0;
                pos_array[x - 1][y] = 1;
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animDown(Button btn,final int x,final int y){
        final  TableRow row = (TableRow)this.getChildAt(x);
        final TableRow row1 = (TableRow)this.getChildAt(x+1);
        temp = (Button) row.getChildAt(y);
        final String text = temp.getText().toString();

        TranslateAnimation animDown = new TranslateAnimation(0, 0, 0, TRANSLATE_OFFSET);
        animDown.setDuration(200);
        btn.startAnimation(animDown);
        animDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                temp.setVisibility(View.INVISIBLE);
                temp = (Button) row1.getChildAt(y);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                temp.setVisibility(View.VISIBLE);
                temp.setText(text);
                pos_array[x][y] = 0;
                pos_array[x + 1][y] = 1;
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private boolean validateResult(){
        int result = 1;
        for(int i=0;i<no_rows;i++) {
            TableRow row =  (TableRow)this.getChildAt(i);
            for (int j = 0; j < no_cols; j++) {
                 Button btn = (Button)row.getChildAt(j);
                 int pos = i*no_rows+j;
                if(pos+1 == Integer.valueOf(btn.getText().toString()))
                    result = 1;
                else {
                    result = 0;
                    break;
                }
            }
        }
        if(result==1) {
            Toast.makeText(context,"Won...",Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


}
