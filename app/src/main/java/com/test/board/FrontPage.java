package com.test.board;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;


public class FrontPage extends ActionBarActivity implements View.OnClickListener {
    public static final String MyPREFERENCES = "MyPrefs1" ;
    Button continue_last;
    Button new_game;
    Button configuration;
    SharedPreferences sharedPreferences;
    String theme;
    String grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        continue_last = (Button)findViewById(R.id.continue_last);
        new_game = (Button)findViewById(R.id.new_game);
        configuration = (Button)findViewById(R.id.config);

        continue_last.setOnClickListener(this);
        new_game.setOnClickListener(this);
        configuration.setOnClickListener(this);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_to_right);
        continue_last.setAnimation(anim);
        anim.start();
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right_to_left);
        new_game.setAnimation(anim);
        anim.start();
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_bottom_to_top);
        configuration.setAnimation(anim);
        anim.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        theme = sharedPreferences.getString("theme", "white");
        grid = sharedPreferences.getString("grid","3");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.continue_last:
                break;
            case R.id.new_game:
                Intent newgame = new Intent(this, MainActivity.class);
                newgame.putExtra("theme",theme);
                newgame.putExtra("grid",grid);
                startActivity(newgame);
                break;
            case R.id.config:
                Intent settings_intent= new Intent(this,SettingsScreen.class);
                startActivity(settings_intent);
                break;
        }

    }


}
