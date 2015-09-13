package com.board.game.sasha.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.board.game.sasha.R;
import com.board.game.sasha.commonutils.GlobalConstants;
import com.board.game.sasha.commonutils.Utils;
import com.board.game.sasha.customviews.ArcTimer;
import com.board.game.sasha.customviews.Board;
import com.board.game.sasha.dialog.AlertDialogFactory;
import com.board.game.sasha.logutils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private Board board;
    private TextView hr, min, sec, msec, playguideText, timer_text, header;
    private Button playButton;
    private long secs, mins, hrs;
    private String seconds, milliseconds, hours, minutes;
    private RelativeLayout counterContainer;
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private Animation anim;
    private AlphaAnimation alphaAnimation;
    private ScaleAnimation scaleAnimation;
    private AnimationSet set;
    private ArcTimer arcTimer;
    private String gameStateObject;
    private SharedPreferences pref;
    private String grid;
    private String soundMode;
    private boolean saved;
    private TextView moves;
    private int moveCount = 0;
    private boolean runnablePosted = false;
    private ImageView sound;
    private SharedPreferences preferenceManager;
    private String gameMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        pref = getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE);
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);


        grid = preferenceManager.getString("grid", "3");
        gameMode = preferenceManager.getString("mode", "number");
        soundMode = preferenceManager.getString("sound", "on");
        saved = getIntent().getBooleanExtra("saved", false);

        board = (Board) findViewById(R.id.board);
        hr = (TextView) findViewById(R.id.hour);
        min = (TextView) findViewById(R.id.min);
        sec = (TextView) findViewById(R.id.sec);
        msec = (TextView) findViewById(R.id.msec);
        arcTimer = (ArcTimer) findViewById(R.id.arcTimer);
        moves = (TextView) findViewById(R.id.moves);
        counterContainer = (RelativeLayout) findViewById(R.id.startTimerContainer);
        timer_text = (TextView) findViewById(R.id.timer_text);
        header = (TextView) findViewById(R.id.header);
        sound = (ImageView)findViewById(R.id.sound);
        if(soundMode.equalsIgnoreCase("on")){
            sound.setImageResource(R.drawable.sound_on);
        }
        else
            sound.setImageResource(R.drawable.sound_off);
        sound.setOnClickListener(this);


        if (saved) {
            moveCount = Integer.valueOf(pref.getString("moves", "0"));
            elapsedTime = pref.getLong("elapsedTime", 0);
            moves.setText(String.valueOf(moveCount));
        } else {
            elapsedTime = 0;
            moves.setText(String.valueOf(0));
        }

        parseSavedJsonGameState();

        initAnimation();


    }

    private void parseSavedJsonGameState() {
        try {
            ArrayList<String> labelList = new ArrayList<String>();

            if (saved) {
                String jsongString = pref.getString("gamestate", "");
                JSONObject GameObject = new JSONObject(jsongString);
                int size = GameObject.getInt("gamesize");
                int len = size * size;
                JSONObject obj = (JSONObject) GameObject.get("savedstate");

                for (int i = 0; i < len; i++) {
                    labelList.add(obj.get("id" + i).toString());
                    LogUtils.LOGD("boardgame", obj.get("id" + i).toString());
                }
                board.initBoard(size, soundMode, labelList, saved);
            } else {
                board.initBoard(Integer.valueOf(grid), soundMode,gameMode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initAnimation() {
/*
        anim = new ScaleAnimation(
                1f, 2.5f, // Start and end values for the X axis scaling
                1f, 2.5f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setRepeatCount(Animation.INFINITE); // Needed to keep the result of the animation
        anim.setDuration(1000);
        anim.setRepeatMode(Animation.REVERSE);
        playguideText.startAnimation(anim);
*/

        set = new AnimationSet(true);
        set.setFillAfter(true);
        alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        // alphaAnimation.setFillAfter(true);
        set.addAnimation(alphaAnimation);

        scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        set.addAnimation(scaleAnimation);

    }






    private void updateTimer(float time) {
        secs = (long) (time / 1000);
        mins = (long) ((time / 1000) / 60);
        hrs = (long) (((time / 1000) / 60) / 60);

		/* Convert the seconds to String
         * and format to ensure it has
		 * a leading zero when required
		 */
        secs = secs % 60;
        seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        }

    	/* Convert the hours to String and format the String */

        hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

    	/* Although we are not using milliseconds on the timer in this example
         * I included the code in the event that you wanted to include it on your own
    	 */
        milliseconds = String.valueOf((long) time);
        if (milliseconds.length() == 2) {
            milliseconds = "0" + milliseconds;
        }
        if (milliseconds.length() <= 1) {
            milliseconds = "00";
        } else {
            milliseconds = "0" + milliseconds.substring(milliseconds.length() - 3, milliseconds.length() - 2);
        }

		/* Setting the timer text to the elapsed time */
        (hr).setText(hours);
        (min).setText(minutes);
        (sec).setText(seconds);
        (msec).setText(milliseconds);
    }



    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = elapsedTime + REFRESH_RATE;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.LOGD("resume","resume");
       /* if(!arcTimer.getCountDownFinished()) {
            if(counterContainer.getVisibility()==View.INVISIBLE)
               counterContainer.setVisibility(View.VISIBLE);
            arcTimer.resetMe();
            arcTimer.beginCountDown();
        }*/
        if (startTimer != null && !runnablePosted /*&&
                (counterContainer.getVisibility() == View.GONE)*/ && (board.getResult()!=1))
            resumeTimer();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
          /*  case R.id.playButton:
                timer.start();
                playContainer.setVisibility(View.GONE);
                anim.cancel();
                playguideText.setVisibility(View.GONE);
                counterContainer.setVisibility(View.VISIBLE);
                break;
          */  case R.id.sound:
                int drawable = soundMode.equalsIgnoreCase("on")?R.drawable.sound_off:R.drawable.sound_on;
                sound.setImageResource(drawable);
                soundMode = soundMode.equalsIgnoreCase("on")?"off":"on";
                board.initSounds(soundMode);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (anim != null)
            anim.cancel();
        if (mHandler != null && startTimer != null)
            mHandler.removeCallbacks(startTimer);
        if (board != null)
            board.flushSoundPool_and_Bitmaps();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.LOGD("sachin","onPause calld");
        if (startTimer != null)
            pauseTimer();
      /*  if(!arcTimer.getCountDownFinished())
            arcTimer.clearAnim();*/
    }

    @Override
    public void onBackPressed() {
        if (board != null /*&& (counterContainer.getVisibility() == View.GONE)*/) {
          LogUtils.LOGD("boardgame","onbackpress if");
            AlertDialog dialog = new AlertDialogFactory(MainActivity.this, "EXIT").getDialog();
            if (dialog != null) {
                dialog.show();
                pauseTimer();
            }
        } else {
            LogUtils.LOGD("boardgame","onbackpress else");
            super.onBackPressed();
        }
    }


    public void resumeTimer() {
        mHandler.postDelayed(startTimer, 0);
        runnablePosted = true;
    }

    public void pauseTimer() {
        mHandler.removeCallbacks(startTimer);
        runnablePosted = false;
    }

    public void notifyBoardToSave() {
        if (board != null) {
            board.saveGameState(moveCount, elapsedTime);
        }
    }

    public void saveGameBestStats(){
        String saved_time = pref.getString("best_time",null);
        String saved_move = pref.getString("best_move",null);
        SharedPreferences.Editor editor = getSharedPreferences("gameprefs", Context.MODE_PRIVATE).edit();
        editor.clear();
        checkIfweHaveBestScoresAvailableNow(editor,saved_move,saved_time);
        editor.commit();

    }

    public void clearSavedGameState() {
        SharedPreferences.Editor editor = getSharedPreferences("gameprefs", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    private void checkIfweHaveBestScoresAvailableNow(SharedPreferences.Editor editor,String savedmove,String savedtime) {
        String moves = savedmove;
        String time = savedtime;
        if(!Utils.isNullorWhiteSpace(moves)){
            if(saveBestNeeded(moves,time)){
                editor.putString("best_move",String.valueOf(moveCount));
                editor.putString("best_time",hours+":"+minutes+":"+seconds+":"+milliseconds);
            }else{
                editor.putString("best_move",moves);
                editor.putString("best_time",time);
            }

        }else{
            editor.putString("best_move",String.valueOf(moveCount));
            editor.putString("best_time",hours+":"+minutes+":"+seconds+":"+milliseconds);
        }
    }

    private boolean saveBestNeeded(String moves,String time){
        int move = Integer.valueOf(moves);
        String time_tokens[] = time.split(":");
        int saved_hrs = Integer.valueOf(time_tokens[0]);
        int saved_min = Integer.valueOf(time_tokens[1]);
        int saved_sec = Integer.valueOf(time_tokens[2]);
        int saved_msec = Integer.valueOf(time_tokens[3]);
        if(moveCount<move || checktimediff(saved_hrs,saved_min,saved_sec,saved_msec)){
          return true;
        }
        else{
            return false;
        }
    }

    private boolean checktimediff(int saved_hrs, int saved_min, int saved_sec, int saved_msec) {
        if(Integer.valueOf(hours)>saved_hrs)
            return false;
        else{
            if(Integer.valueOf(hours)==saved_hrs){
                //check mins now, as hours are equal
                if(Integer.valueOf(minutes)>saved_min)
                    return false;
                else{
                    if(Integer.valueOf(minutes)==saved_min){
                        // check seconds now, as minutes are equal
                        if(Integer.valueOf(seconds)>saved_sec)
                            return false;
                        else{
                            if(Integer.valueOf(seconds)==saved_sec){
                                //check msec now, as seconds are equal
                                if(Integer.valueOf(milliseconds)>saved_msec)
                                    return false;
                                else{
                                    if(Integer.valueOf(milliseconds)==saved_msec)
                                        return false;
                                    else
                                        return true;
                                }
                            }else{
                                return true;
                            }
                        }
                    }else{
                       return true;
                    }
                }
            }else{
                return true;
            }
        }
    }


    public void updateMoves() {
        moveCount++;
        moves.setText(String.valueOf(moveCount));
    }

    public void startappTimer() {
       // counterContainer.setVisibility(View.GONE);
        mHandler.removeCallbacks(startTimer);
        runnablePosted = mHandler.postDelayed(startTimer, 0);
        startTime = System.currentTimeMillis();

    }
}
