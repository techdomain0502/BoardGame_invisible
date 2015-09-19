package com.board.game.sasha.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.board.game.sasha.R;
import com.board.game.sasha.commonutils.GlobalConstants;
import com.board.game.sasha.commonutils.Utils;
import com.board.game.sasha.dialog.AlertDialogFactory;
import com.board.game.sasha.gui.MainActivity;
import com.board.game.sasha.listeners.onFlingGestureListener;
import com.board.game.sasha.logutils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by sachin on 8/2/2015.
 */
public class Board extends TableLayout {
    private int BOARD_SIZE;
    private int board_width;
    private int no_rows;
    private int no_cols;
    int button_dimen;
    private Context context;
    private TableRow[] rowArr;
    private Button temp;
    private ArrayList<String> labelList;
    private int[][] pos_array;
    private HashMap<Integer, String> map;
    private int TRANSLATE_OFFSET = 100;
    private int drawable[] = {R.drawable.btn_green_glossy};
    private int pic_array[] = {R.drawable.baby,R.drawable.cute,R.drawable.koala,R.drawable.panda,R.drawable.penguin,R.drawable.daughter};
    boolean saved;
    int lastPos;
    int result;
    private String gameMode;
    private boolean picExists;
    private Bitmap[] array;
    private Button[] btn_arr;
    private Coord[] coord_arr;
    private onFlingGestureListenerImpl fling;
    private SoundPool sp;
    private int sound[];
    private boolean isSoundEnabled;
    private String imgPath;
    private LayerDrawable dArr[];

    public Board(Context ctxt, AttributeSet attr) {
        super(ctxt, attr);
        context = ctxt;
        this.setWillNotDraw(false);
        this.setClipChildren(false);
        fling = new onFlingGestureListenerImpl();
    }


    public void initSounds(String soundMode) {
        isSoundEnabled = (soundMode.equalsIgnoreCase("on")) ? true : false;
        if (isSoundEnabled) {
            sound = new int[2];
            sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            sound[0] = sp.load(context, R.raw.valid, 1);
            sound[1] = sp.load(context, R.raw.invalid, 1);
        }
    }

    private void initRows() {
        rowArr = new TableRow[no_rows];
        for (int i = 0; i < no_rows; i++) {
            rowArr[i] = new TableRow(context);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowArr[i].setLayoutParams(rowParams);
        }
    }

    private void initDimens(int size) {
        BOARD_SIZE = size;
        no_rows = size;
        no_cols = size;
        lastPos = no_rows * no_cols - 1;
    }

    private void initMap() {
        map = new HashMap<Integer, String>();
    }

    public void initBoard(int size, String soundMode, ArrayList<String> labels, boolean saved) {
        initSounds(soundMode);
        this.saved = saved;
        initDimens(size);
        initLabels(labels);
        initPosArray(labels);
        initRows();
        initMap();
        initButtons_Coords();
        gameMode = "number";
    }


    public void initBoard(int size, String soundMode,String gameMode) {
        initSounds(soundMode);
        initDimens(size);
        initLabels();
        initPosArray();
        initRows();
        initMap();
        initButtons_Coords();
        this.gameMode = gameMode;
        if(gameMode.equalsIgnoreCase("picture"))
           initBitmap();
        Collections.shuffle(labelList);
    }

    private void initButtons_Coords() {
        btn_arr = new Button[BOARD_SIZE * BOARD_SIZE];
        coord_arr = new Coord[BOARD_SIZE * BOARD_SIZE];
        for(int i=0;i<no_rows;i++)
            for(int j=0;j<no_cols;j++) {
                int index = i * no_rows + j;
                btn_arr[index] = new Button(context);
                coord_arr[index] = new Coord(i, j);
            }
    }

    private void initBitmap() {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE);
        imgPath = preferences.getString(GlobalConstants.image_path,"No file Found");
        File f = new File(imgPath);
        if(f.exists()){
            picExists = true;
        }
    }

    private void initLabels() {
        labelList = new ArrayList<String>();
        for (int i = 0; i < no_rows * no_cols; i++) {
            if (i == (no_rows * no_cols - 1))
                labelList.add("");
            else
                labelList.add(String.valueOf(i));
        }
    }

    private void initLabels(ArrayList<String> savedList) {
        labelList = new ArrayList<String>();
        for (int i = 0; i < no_rows * no_cols; i++) {
            labelList.add(savedList.get(i));
        }
    }

    private void initPosArray() {
        pos_array = new int[no_rows][];
        for (int i = 0; i < no_rows; i++)
            pos_array[i] = new int[no_cols];
        Random rand = new Random();
        int index = rand.nextInt(no_cols * no_rows);
        for (int i = 0; i < no_rows; i++)   //i*SIZE+j
            for (int j = 0; j < no_cols; j++) {
                int pos = i * no_rows + j;
                if (pos == index)
                    pos_array[i][j] = 0;
                else
                    pos_array[i][j] = 1;
            }
    }

    private void initPosArray(ArrayList<String> list) {
        pos_array = new int[no_rows][];
        for (int i = 0; i < no_rows; i++)
            pos_array[i] = new int[no_cols];
        for (int i = 0; i < no_rows; i++)   //i*SIZE+j
            for (int j = 0; j < no_cols; j++) {
                int pos = i * no_rows + j;

                String val = list.get(pos);
                if (Utils.isNullorWhiteSpace(val))
                    pos_array[i][j] = 0;
                else
                    pos_array[i][j] = 1;
            }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initialize();
    }

    private LayerDrawable[] splitBitmap(Bitmap bmp){
        array = new Bitmap[no_rows*no_cols];
        int startX = 0;
        int startY =0;
        int width = bmp.getWidth()/no_cols;
        int height = bmp.getHeight()/no_rows;
        dArr = new LayerDrawable[no_rows*no_cols];
        for(int ii=0;ii<no_rows*no_cols;ii++){
            if(ii!=0 && ii%BOARD_SIZE==0) {
                startX = 0;
                startY = startY + height;
            }
            array[ii] = Bitmap.createBitmap(bmp, startX, startY, width, height);
            Drawable layers[] = new Drawable[2];
            layers[0]= context.getResources().getDrawable(R.drawable.border);
            layers[1]= new BitmapDrawable(context.getResources(),array[ii]);

                dArr[ii]= new LayerDrawable(layers);
                startX = startX + width;

        }
        return dArr;
    }

    private void initialize() {
        if(gameMode.equalsIgnoreCase("picture")) {
            Bitmap bmp = decodeSampledBitmapFromResource(context.getResources()
                    , imgPath
                    , board_width, 350 * 2);

            dArr = splitBitmap(bmp);
            bmp.recycle();
            bmp = null;
        }

        TRANSLATE_OFFSET = button_dimen;
        int count = 0;
        for (int i = 0; i < no_rows; i++) {
            this.addView(rowArr[i]);
            for (int j = 0; j < no_cols; j++) {
                int btn_index = i*no_rows +j;
                TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(button_dimen, button_dimen);
                if(!gameMode.equalsIgnoreCase("picture"))
                    btn_arr[btn_index].setTextAppearance(context, R.style.ButtonText);
                else
                    btn_arr[btn_index].setTextAppearance(context, R.style.ButtonTextTransparent);
                btn_arr[btn_index].setLayoutParams(buttonParams);
                btn_arr[btn_index].setTag(coord_arr[i * no_rows + j]);
                if(!gameMode.equalsIgnoreCase("picture"))
                    btn_arr[btn_index].setBackgroundResource(drawable[(i * no_rows + j) % drawable.length]);

                btn_arr[btn_index].setOnTouchListener(fling);

                if (pos_array[i][j] == 1) {
                    if (!Utils.isNullorWhiteSpace(labelList.get(count))) {
                        map.put(i * no_rows + j, labelList.get(count));
                            btn_arr[btn_index].setText(labelList.get(count));
                        if(gameMode.equalsIgnoreCase("picture"))
                            btn_arr[btn_index].setBackgroundDrawable(dArr[Integer.valueOf(labelList.get(count))]);
                        rowArr[i].addView(btn_arr[btn_index]);
                        count++;
                    } else {
                        count++;
                        map.put(i * no_rows + j, labelList.get(count));
                        if(!gameMode.equalsIgnoreCase("picture"))
                            btn_arr[btn_index].setText(labelList.get(count));
                        if(gameMode.equalsIgnoreCase("picture"))
                            btn_arr[btn_index].setBackgroundDrawable(dArr[Integer.valueOf(labelList.get(count))]);
                        rowArr[i].addView(btn_arr[btn_index]);
                        count++;
                    }
                } else if (pos_array[i][j] == 0) {
                    map.put(i * no_rows + j, "");
                    btn_arr[btn_index].setText("");
                    rowArr[i].addView(btn_arr[btn_index]);
                    rowArr[i].getChildAt(j).setVisibility(View.INVISIBLE);
                }

            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        board_width = this.getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        button_dimen = board_width / no_cols;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)this.getLayoutParams();
        params.height = button_dimen * no_rows;
        setLayoutParams(params);
    }


    private class Coord {
        int x;
        int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }


    }


    public class onFlingGestureListenerImpl extends onFlingGestureListener {
        @Override
        public void toBottom(View v, Object o) {
            int x = ((Coord) o).getX();
            int y = ((Coord) o).getY();

            if (validate_DownMove(x, y)) {
                ((MainActivity) context).updateMoves();
                animDown((Button) v, x, y);
            } else {
                if (isSoundEnabled)
                    sp.play(sound[1], 1, 1, 1, 0, (float) 1.0);
                Toast.makeText(context, "InValid Move", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void toLeft(View v, Object o) {
            int x = ((Coord) o).getX();
            int y = ((Coord) o).getY();

            if (validate_LeftMove(x, y)) {
                ((MainActivity) context).updateMoves();
                animLeft((Button) v, x, y);
            } else {
                if (isSoundEnabled)
                    sp.play(sound[1], 1, 1, 1, 0, (float) 1.0);
                Toast.makeText(context, "InValid Move", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void toRight(View v, Object o) {
            //Your code here
            int x = ((Coord) o).getX();
            int y = ((Coord) o).getY();

            if (validate_RightMove(x, y)) {
                ((MainActivity) context).updateMoves();
                animRight((Button) v, x, y);
            } else {
                if (isSoundEnabled)
                    sp.play(sound[1], 1, 1, 1, 0, (float) 1.0);
                Toast.makeText(context, "InValid Move", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void toTop(View v, Object o) {
            //Your code here
            int x = ((Coord) o).getX();
            int y = ((Coord) o).getY();
            if (validate_UpMove(x, y)) {
                ((MainActivity) context).updateMoves();
                animTop((Button) v, x, y);
            } else {
                if (isSoundEnabled)
                    sp.play(sound[1], 1, 1, 1, 0, (float) 1.0);
                Toast.makeText(context, "InValid Move", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private boolean validate_RightMove(int x, int y) {
        if (x < BOARD_SIZE && y < BOARD_SIZE && y + 1 < BOARD_SIZE) {
            if (pos_array[x][y + 1] == 0)
                return true;
        }
        return false;
    }

    private boolean validate_LeftMove(int x, int y) {
        if (x < BOARD_SIZE && y < BOARD_SIZE && y - 1 >= 0) {
            if (pos_array[x][y - 1] == 0)
                return true;
        }
        return false;
    }

    private boolean validate_UpMove(int x, int y) {
        if (x < BOARD_SIZE && y < BOARD_SIZE && x - 1 >= 0) {
            if (pos_array[x - 1][y] == 0)
                return true;
        }
        return false;
    }

    private boolean validate_DownMove(int x, int y) {
        if (x < BOARD_SIZE && y < BOARD_SIZE && x + 1 < BOARD_SIZE) {
            if (pos_array[x + 1][y] == 0)
                return true;
        }
        return false;
    }


    private void animRight(Button btn, final int x, final int y) {
        if (isSoundEnabled)
            sp.play(sound[0], 1, 1, 1, 0, (float) 1.0);
        final TableRow row = (TableRow) this.getChildAt(x);
        temp = (Button) row.getChildAt(y);
        final String text = temp.getText().toString();
        final Drawable background = temp.getBackground();

        TranslateAnimation animRight = new TranslateAnimation(0, TRANSLATE_OFFSET, 0, 0);
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
                temp.setBackgroundDrawable(background);
                pos_array[x][y] = 0;
                pos_array[x][y + 1] = 1;
                map.put(x * no_rows + y, "");
                map.put((x) * no_rows + (y + 1), (text));
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animLeft(Button btn, final int x, final int y) {
        if (isSoundEnabled)
            sp.play(sound[0], 1, 1, 1, 0, (float) 1.0);
        final TableRow row = (TableRow) this.getChildAt(x);
        temp = (Button) row.getChildAt(y);
        final String text = temp.getText().toString();
        final Drawable background = temp.getBackground();

        TranslateAnimation animLeft = new TranslateAnimation(0, -TRANSLATE_OFFSET, 0, 0);
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
                temp.setBackgroundDrawable(background);
                pos_array[x][y] = 0;
                pos_array[x][y - 1] = 1;
                map.put(x * no_rows + y, "");
                map.put((x) * no_rows + (y - 1), (text));
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void animTop(Button btn, final int x, final int y) {
        if (isSoundEnabled)
            sp.play(sound[0], 1, 1, 1, 0, (float) 1.0);
        final TableRow row = (TableRow) this.getChildAt(x);
        final TableRow row1 = (TableRow) this.getChildAt(x - 1);
        temp = (Button) row.getChildAt(y);
        final String text = temp.getText().toString();
        final Drawable background = temp.getBackground();

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
                temp.setBackgroundDrawable(background);
                pos_array[x][y] = 0;
                pos_array[x - 1][y] = 1;
                map.put(x * no_rows + y, "");
                map.put((x - 1) * no_rows + y, (text));
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animDown(Button btn, final int x, final int y) {
        if (isSoundEnabled)
            sp.play(sound[0], 1, 1, 1, 0, (float) 1.0);
        final TableRow row = (TableRow) this.getChildAt(x);
        final TableRow row1 = (TableRow) this.getChildAt(x + 1);
        temp = (Button) row.getChildAt(y);
        final String text = temp.getText().toString();
        final Drawable background = temp.getBackground();
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
                temp.setBackgroundDrawable(background);
                pos_array[x][y] = 0;
                pos_array[x + 1][y] = 1;
                map.put(x * no_rows + y, "");
                map.put((x + 1) * no_rows + y, (text));
                validateResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public int getResult() {
        return result;
    }

    private void validateResult() {
        result = 1;
        outer: for (int i = 0; i < no_rows; i++) {
            TableRow row = (TableRow) this.getChildAt(i);
           inner:  for (int j = 0; j < no_cols; j++) {
                Button btn = (Button) row.getChildAt(j);
                int pos = i * no_rows + j;
                String str = map.get(pos);
                if (!Utils.isNullorWhiteSpace(str) && pos == Integer.valueOf(str)) {
                    result = 1;
                } else {
                    if (pos == (lastPos)) {
                        break;
                    }
                    result = 0;
                    break outer;
                }
            }
        }


        if (result == 1) {
            AlertDialog dialog = new AlertDialogFactory(context, "FINISH").getDialog();
            if (dialog != null) {
                dialog.show();
                if (context instanceof MainActivity)
                    ((MainActivity) context).pauseTimer();
            }
        }
    }


    public void flushSoundPool_and_Bitmaps() {
        if (sp != null) {
            sp.release();
            sp = null;
            LogUtils.LOGD("BoardGame", "flushSoundPool-- Board");
        }

        int size = 0;
        if(array!=null)
            size = array.length;
        for(int i=0;i<size;i++){
             array[i].recycle();
             array[i] = null;
        }


        for(int index=0;index<no_rows*no_cols;index++){
            coord_arr[index] = null;
            btn_arr[index] = null;
        }

        context = null;
    }

    public void saveGameState(int moveCount, long elapsedTime) {
        JSONObject GameObject = new JSONObject();
        JSONObject stateObject = new JSONObject();
        try {
            int length = map.size();
            GameObject.put("gamesize", BOARD_SIZE);
            for (int i = 0; i < length; i++) {
                stateObject.put("id" + i, map.get(i).toString());
            }
            GameObject.put("savedstate", stateObject);
            SharedPreferences storedPrefs = context.getSharedPreferences("gameprefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = context.getSharedPreferences("gameprefs", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.putString("gamestate", GameObject.toString());
            editor.putBoolean("saved", true);
            editor.putString("sound", storedPrefs.getString("sound", "on"));
            editor.putString("grid", storedPrefs.getString("grid", "3"));
            editor.putString("moves", String.valueOf(moveCount));
            editor.putLong("elapsedTime", elapsedTime);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


     private  Bitmap decodeSampledBitmapFromResource(Resources res, String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
         int index =  -1;
        if(!picExists) {
            index = new Random().nextInt(pic_array.length);
            BitmapFactory.decodeResource(res, pic_array[index], options);
        }
        else
           BitmapFactory.decodeFile(path,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        if(options.outHeight < reqHeight && options.outWidth < reqWidth){
            int rawWidth = options.outWidth;
            int rawHeight = options.outHeight;
            float scaleWidth = ((float)reqWidth)/rawWidth;
            float scaleHeight = ((float)reqWidth)/rawHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            options.inJustDecodeBounds = false;
            Bitmap resizedBmp ;
            if(picExists)
               resizedBmp = Bitmap.createBitmap(BitmapFactory.decodeFile(path,options),0, 0, options.outWidth, options.outHeight, matrix, false);
            else
                resizedBmp = Bitmap.createBitmap(BitmapFactory.decodeResource(res, pic_array[index], options),0, 0, options.outWidth, options.outHeight, matrix, false);

            return resizedBmp;
        }

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap resizedBmp =  null;
         if(!picExists) {
         resizedBmp =   BitmapFactory.decodeResource(res, pic_array[index], options);
         }
         else
             resizedBmp =   BitmapFactory.decodeFile(path,options);

         return Bitmap.createScaledBitmap(resizedBmp,reqWidth,reqHeight,false);
    }



}
