package com.board.game.sasha.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.board.game.sasha.R;
import com.board.game.sasha.commonutils.GlobalConstants;

/**
 * Created by sachin on 8/16/2015.
 */
public class SplashScreen extends Activity {
    private ImageView start;
    private SharedPreferences gamePrefs;
    private TextView tvTitle,appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        gamePrefs = getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE);
        tvTitle = (TextView)findViewById(R.id.title);
        appVersion = (TextView)findViewById(R.id.version);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/letteromatic.ttf");
        tvTitle.setTypeface(font);

        appVersion.setText(String.format(getResources().getString(R.string.version),gamePrefs.getString("version","1.0")));
        start = (ImageView)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
              startActivity(new Intent(getApplicationContext(), FrontPage.class));
                finish();
            }
        });
    }

}
