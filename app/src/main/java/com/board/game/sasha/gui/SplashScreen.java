package com.board.game.sasha.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.board.game.sasha.R;

/**
 * Created by sachin on 8/16/2015.
 */
public class SplashScreen extends Activity {
    private ImageView start;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        tvTitle = (TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/letteromatic.ttf");
        tvTitle.setTypeface(font);
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
