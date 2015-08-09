package com.board.game.sasha;

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
import android.widget.Toast;

import com.board.game.sasha.MainActivity;
import com.board.game.sasha.R;
import com.board.game.sasha.SettingsScreen;
import com.board.game.sasha.com.board.game.sasha.logutils.LogUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


public class FrontPage extends ActionBarActivity implements View.OnClickListener {
    public static final String MyPREFERENCES = "MyPrefs1" ;
    Button continue_last;
    Button new_game;
    Button configuration,share;
    SharedPreferences sharedPreferences;
    String grid;
    String sound;
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
                LogUtils.LOGD("boardgame","facebook share feature cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(),"Please enable wifi/mobile data for sharing",Toast.LENGTH_SHORT).show();
            }
        });

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        continue_last = (Button)findViewById(R.id.continue_last);
        new_game = (Button)findViewById(R.id.new_game);
        configuration = (Button)findViewById(R.id.config);
        share = (Button)findViewById(R.id.share);

        continue_last.setOnClickListener(this);
        new_game.setOnClickListener(this);
        configuration.setOnClickListener(this);
        share.setOnClickListener(this);

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_to_right);
        continue_last.setAnimation(anim);
        anim.start();
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right_to_left);
        new_game.setAnimation(anim);
        anim.start();
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_bottom_to_top);
        configuration.setAnimation(anim);
        anim.start();
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_to_right);
        share.setAnimation(anim);
        anim.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        grid = sharedPreferences.getString("grid","3");
        sound = sharedPreferences.getString("sound","on");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.continue_last:
                break;
            case R.id.new_game:
                Intent newgame = new Intent(this, MainActivity.class);
                newgame.putExtra("grid",grid);
                newgame.putExtra("sound",sound);
                startActivity(newgame);
                break;
            case R.id.config:
                Intent settings_intent= new Intent(this,SettingsScreen.class);
                startActivity(settings_intent);
                break;
            case R.id.share:
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
        }

    }


}
