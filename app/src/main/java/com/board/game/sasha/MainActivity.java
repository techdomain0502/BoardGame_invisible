package com.board.game.sasha;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.board.game.sasha.customviews.ArcTimer;
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
    private RelativeLayout playContainer, counterContainer;
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private Animation anim;
    private AlphaAnimation alphaAnimation;
    private ScaleAnimation scaleAnimation;
    private AnimationSet set;
    private CountDownTimer timer;
    private ArcTimer arcTimer;
    private String gameStateObject;
    private SharedPreferences pref;
    private String grid;
    private String soundMode;
    private boolean saved;
    private TextView moves;
    private int moveCount = 0;
    private boolean runnablePosted = false;
    private ImageButton sound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        Bundle i = getIntent().getExtras();
        grid = i.get("grid").toString();
        soundMode = i.get("sound").toString();
        saved = i.getBoolean("saved", false);
        LogUtils.LOGD("boardgame", "saved=" + saved+" grid="+grid+" soundmode="+soundMode);
        board = (Board) findViewById(R.id.board);
        hr = (TextView) findViewById(R.id.hour);
        min = (TextView) findViewById(R.id.min);
        sec = (TextView) findViewById(R.id.sec);
        msec = (TextView) findViewById(R.id.msec);
        arcTimer = (ArcTimer) findViewById(R.id.arcTimer);
        moves = (TextView) findViewById(R.id.moves);
        playContainer = (RelativeLayout) findViewById(R.id.playButtonContainer);
        counterContainer = (RelativeLayout) findViewById(R.id.startTimerContainer);
        playguideText = (TextView) findViewById(R.id.guideText);
        playButton = (Button) findViewById(R.id.playButton);
        timer_text = (TextView) findViewById(R.id.timer_text);
        header = (TextView) findViewById(R.id.header);
        sound = (ImageButton)findViewById(R.id.sound);
        if(soundMode.equalsIgnoreCase("on")){
            sound.setImageResource(R.drawable.sound_on);
        }
        else
            sound.setImageResource(R.drawable.sound_off);
        sound.setOnClickListener(this);
        playButton.setOnClickListener(this);
        pref = getSharedPreferences("gameprefs", Context.MODE_PRIVATE);

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

        timer = new CountDownTimer(3500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtils.LOGD("timerdemo", "millisUntilFinished" + millisUntilFinished);
                arcTimer.updateSweepAngle();
                timer_text.setText("" + millisUntilFinished / 1000);
                timer_text.startAnimation(set);
            }

            @Override
            public void onFinish() {
                mHandler.removeCallbacks(startTimer);
                runnablePosted = mHandler.postDelayed(startTimer, 0);
                startTime = System.currentTimeMillis();
                alphaAnimation.cancel();
                counterContainer.setVisibility(View.GONE);
            }
        };

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
                board.initBoard(Integer.valueOf(grid), soundMode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initAnimation() {
        anim = new ScaleAnimation(
                1f, 2.5f, // Start and end values for the X axis scaling
                1f, 2.5f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setRepeatCount(Animation.INFINITE); // Needed to keep the result of the animation
        anim.setDuration(1000);
        anim.setRepeatMode(Animation.REVERSE);
        playguideText.startAnimation(anim);

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
        if (startTimer != null && !runnablePosted &&
                (playContainer.getVisibility() == View.GONE
                        && counterContainer.getVisibility() == View.GONE) && (board.getResult()!=1))
            resumeTimer();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.playButton:
                timer.start();
                playContainer.setVisibility(View.GONE);
                anim.cancel();
                playguideText.setVisibility(View.GONE);
                counterContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.sound:
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
            board.flushSoundPool();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (startTimer != null)
            pauseTimer();
    }

    @Override
    public void onBackPressed() {
        if (board != null && (playContainer.getVisibility() == View.GONE && counterContainer.getVisibility() == View.GONE)) {
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

    public void clearSavedGameState() {
        SharedPreferences.Editor editor = getSharedPreferences("gameprefs", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public void updateMoves() {
        moveCount++;
        moves.setText(String.valueOf(moveCount));
    }


}
