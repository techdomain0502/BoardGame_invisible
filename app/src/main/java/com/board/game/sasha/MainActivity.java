package com.board.game.sasha;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private boolean saved;
    private String gameStateObject;
    private SharedPreferences pref;
    private String grid;
    private String soundMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        Bundle i = getIntent().getExtras();
        grid = i.get("grid").toString();
        soundMode = i.get("sound").toString();

        board = (Board) findViewById(R.id.board);
        hr = (TextView) findViewById(R.id.hour);
        min = (TextView) findViewById(R.id.min);
        sec = (TextView) findViewById(R.id.sec);
        msec = (TextView) findViewById(R.id.msec);
        arcTimer = (ArcTimer) findViewById(R.id.arcTimer);
        playContainer = (RelativeLayout) findViewById(R.id.playButtonContainer);
        counterContainer = (RelativeLayout) findViewById(R.id.startTimerContainer);
        playguideText = (TextView) findViewById(R.id.guideText);
        playButton = (Button) findViewById(R.id.playButton);
        timer_text = (TextView) findViewById(R.id.timer_text);
        header = (TextView) findViewById(R.id.header);
        playButton.setOnClickListener(this);
        pref = getSharedPreferences("gameprefs", Context.MODE_PRIVATE);
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
                mHandler.postDelayed(startTimer, 0);
                startTime = System.currentTimeMillis();
                alphaAnimation.cancel();
                counterContainer.setVisibility(View.GONE);
            }
        };

    }

    private void parseSavedJsonGameState() {
        try {
            boolean saved = false;
            saved = pref.getBoolean("saved", false);
            ArrayList<String> labelList = new ArrayList<String>();

            if (saved) {
                String jsongString = pref.getString("gamestate", "");

                JSONObject GameObject = new JSONObject(jsongString);
                int size = GameObject.getInt("gamesize");
                int len = size * size;
                JSONObject obj = (JSONObject) GameObject.get("gamestate");
                for (int i = 0; i < len; i++) {
                    labelList.add(obj.get("index" + i).toString());
                    LogUtils.LOGD("boardgame", obj.get("index" + i).toString());
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


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     /*   int id = item.getItemId();
        switch(id){
            case R.id.play:
                startTime = System.currentTimeMillis();
                mHandler.removeCallbacks(startTimer);
                mHandler.postDelayed(startTimer, 0);
                break;
        }
*/
        return super.onOptionsItemSelected(item);
    }


    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };

    @Override
    public void onClick(View v) {
        timer.start();
        playContainer.setVisibility(View.GONE);
        anim.cancel();
        playguideText.setVisibility(View.GONE);
        counterContainer.setVisibility(View.VISIBLE);
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
    public void onBackPressed() {

        if (board != null && (playContainer.getVisibility() == View.GONE && counterContainer.getVisibility() == View.GONE)) {
            new AlertDialogFactory(MainActivity.this, "EXIT").getDialog().show();
        } else
            super.onBackPressed();
    }
}
