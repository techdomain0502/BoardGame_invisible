package com.board.game.sasha;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.board.game.sasha.commonutils.Utils;
import com.board.game.sasha.logutils.LogUtils;
import com.board.game.sasha.dialog.AlertDialogFactory;

import org.json.JSONException;
import org.json.JSONObject;

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
    private int board_height;
    private int cell_spacing;
    private int board_padding;
    private int no_rows;
    private int no_cols;
    private Context context;
    private TableRow[] rowArr;
    private Button temp;
    private ArrayList<String> labelList;
    private int[][] pos_array;
    private HashMap<Integer, String> map;
    private int TRANSLATE_OFFSET = 100;
    private int drawable[] = {R.drawable.btn_green_glossy};
    boolean saved;
    int lastPos;
    int result;

    public Board(Context ctxt, AttributeSet attr) {
        super(ctxt, attr);
        context = ctxt;
        this.setWillNotDraw(false);
        this.setClipChildren(false);
    }

    private SoundPool sp;
    private int sound[];
    private boolean isSoundEnabled;

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
        LogUtils.LOGD("sachin", size + "=size");
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
        for (int i = 0; i < labels.size(); i++) {
            LogUtils.LOGD("savetest", "intboard 2nd version labels=" + labels.get(i));
        }
    }

    public void initBoard(int size, String soundMode) {
        initSounds(soundMode);
        initDimens(size);
        initLabels();
        initPosArray();
        initRows();
        initMap();
        Collections.shuffle(labelList);
    }

    private void initLabels() {
        labelList = new ArrayList<String>();
        for (int i = 0; i < no_rows * no_cols; i++) {
            if (i == (no_rows * no_cols - 1))
                labelList.add("");
            else
                labelList.add(String.valueOf(i));
            LogUtils.LOGD("savetest", "initLabels labelist@" + i + " " + labelList.get(i));
        }
    }

    private void initLabels(ArrayList<String> savedList) {
        labelList = new ArrayList<String>();
        for (int i = 0; i < no_rows * no_cols; i++) {
            labelList.add(savedList.get(i));
            LogUtils.LOGD("savetest", "initlabels " + savedList.get(i));
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
                LogUtils.LOGD("savetest", "initPosArray " + pos_array[i][j]);
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
                LogUtils.LOGD("savedstate", "pos=" + pos + " val=" + val);
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

    private void initialize() {
        LogUtils.LOGD("savetest", "board... initialize");
        int button_dimen = board_width / no_cols;
        TRANSLATE_OFFSET = button_dimen;
        int count = 0;
        for (int i = 0; i < no_rows; i++) {
            this.addView(rowArr[i]);
            for (int j = 0; j < no_cols; j++) {
                TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(button_dimen, button_dimen);
                Button button = new Button(context);
                button.setTextAppearance(context, R.style.ButtonText);
                button.setLayoutParams(buttonParams);
                button.setTag(new Coord(i, j));
                button.setBackgroundResource(drawable[(i * no_rows + j) % drawable.length]);
                button.setOnTouchListener(new onFlingGestureListenerImpl());

                if (pos_array[i][j] == 1) {
                    if (!Utils.isNullorWhiteSpace(labelList.get(count))) {
                        map.put(i * no_rows + j, labelList.get(count));
                        button.setText(labelList.get(count));
                        LogUtils.LOGD("savetest", i + " " + j + " button.getText()" + button.getText() + " count=" + count + " " + labelList.get(count));
                        rowArr[i].addView(button);
                        count++;
                    } else {
                        count++;
                        map.put(i * no_rows + j, labelList.get(count));
                        button.setText(labelList.get(count));
                        LogUtils.LOGD("savetest", i + " " + j + " button.getText()" + button.getText() + " count=" + count + " " + labelList.get(count));
                        rowArr[i].addView(button);
                        count++;
                    }
                } else if (pos_array[i][j] == 0) {
                    map.put(i * no_rows + j, "");
                    button.setText("");
                    LogUtils.LOGD("savetest", i + " " + j + " " + button.getText());
                    rowArr[i].addView(button);
                    rowArr[i].getChildAt(j).setVisibility(View.INVISIBLE);
                }

            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        board_width = this.getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }


    private class Coord {
        int x;
        int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Coord getObj() {
            return this;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setXY(int x, int y) {
            this.x = x;
            this.y = y;
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


    public void flushSoundPool() {
        if (sp != null) {
            sp.release();
            sp = null;
            LogUtils.LOGD("BoardGame", "flushSoundPool-- Board");
        }
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
            LogUtils.LOGD("boardgame", stateObject.toString());
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


}
