package com.board.game.sasha.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.board.game.sasha.R;
import com.board.game.sasha.settings.SettingsScreen;
import com.board.game.sasha.commonutils.GlobalConstants;
import com.board.game.sasha.dialog.AlertDialogFactory;
import com.board.game.sasha.logutils.LogUtils;
import com.board.game.sasha.twitter.TwitterActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


public class FrontPage extends ActionBarActivity implements View.OnClickListener {

    Button continue_last;
    Button new_game;
    Button configuration/*, share_fb, share_tw*/;
    Button best_score;
    ImageView share_fb,share_tw;
    SharedPreferences sharedPreferences;
    String grid;
    String sound;
    String imgPath;
    boolean saved;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_front_page);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                LogUtils.LOGD("boardgame", "facebook share feature success");
            }

            @Override
            public void onCancel() {
                LogUtils.LOGD("boardgame", "facebook share feature cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "Please enable wifi/mobile data for sharing", Toast.LENGTH_SHORT).show();
            }
        });

        sharedPreferences = getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE);
        saved = sharedPreferences.getBoolean("saved", false);
        continue_last = (Button) findViewById(R.id.continue_last);
        new_game = (Button) findViewById(R.id.new_game);
        configuration = (Button) findViewById(R.id.config);
        best_score = (Button) findViewById(R.id.score);
        share_fb = (ImageView) findViewById(R.id.fb);
        share_tw = (ImageView) findViewById(R.id.tw);
        continue_last.setOnClickListener(this);
        new_game.setOnClickListener(this);
        configuration.setOnClickListener(this);
        best_score.setOnClickListener(this);
        share_fb.setOnClickListener(this);
        share_tw.setOnClickListener(this);
      //  Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_to_right);
        if (saved) {
            continue_last.setVisibility(View.VISIBLE);
           // continue_last.setAnimation(anim);
        }
     /*   best_score.setAnimation(anim);
        anim.start();
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right_to_left);
        new_game.setAnimation(anim);
        anim.start();
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_bottom_to_top);
        configuration.setAnimation(anim);
        anim.start();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  grid = sharedPreferences.getString("grid", "3");
       // sound = sharedPreferences.getString("sound", "on");
      //  imgPath = sharedPreferences.getString("image_path", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_last:
                Intent savedGame = new Intent(this, MainActivity.class);
                savedGame.putExtra("saved", true);
             //   savedGame.putExtra("grid", grid);
            //    savedGame.putExtra("sound", sound);
            //    savedGame.putExtra("image_path",imgPath);
                startActivity(savedGame);
                finish();
                break;
            case R.id.new_game:
                Intent newGame = new Intent(this, MainActivity.class);
              //  newGame.putExtra("grid", grid);
              //  newGame.putExtra("sound", sound);
                newGame.putExtra("saved", false);
              //  newGame.putExtra("image_path",imgPath);
                startActivity(newGame);
                finish();
                break;
            case R.id.config:
                Intent settings_intent = new Intent(this, SettingsScreen.class);
                startActivity(settings_intent);
                break;
            case R.id.score:
                String move = sharedPreferences.getString("best_move",null);
                String time = sharedPreferences.getString("best_time",null);
                AlertDialog dialog = new AlertDialogFactory(this, "SCORE",move,time).getDialog();
                if(dialog!=null)
                    dialog.show();
                break;
            case R.id.fb:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Board Game - Please share it.")
                            .setContentDescription(
                                    "https://www.facebook.com/BoardGame--" +
                                            "apk Download url:http://play.google.com/boardgame")
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .build();

                    shareDialog.show(linkContent);
                }
                break;
            case R.id.tw:
                Intent twitter_intent = new Intent(this, TwitterActivity.class);
                startActivity(twitter_intent);
                break;
        }


    }


}