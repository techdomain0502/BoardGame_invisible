package com.board.game.sasha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.board.game.sasha.commonutils.GlobalConstants;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by sachin on 8/16/2015.
 */
public class SplashScreen extends Activity {
    private SharedPreferences.Editor editor;
    private ImageView start;
    private SharedPreferences gamePrefs;
    private TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  gamePrefs = getSharedPreferences(GlobalConstants.pref_file,Context.MODE_PRIVATE);
        if(gamePrefs.getBoolean("splash_done", false)){
            startActivity(new Intent(getApplicationContext(),FrontPage.class));
            finish();
        }
*/
        setContentView(R.layout.splash);
        tvTitle = (TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/letteromatic.ttf");
        tvTitle.setTypeface(font);
       //editor = getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE).edit();
        start = (ImageView)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         /*       editor.putBoolean("splash_done", true);
                editor.commit();
         */       startActivity(new Intent(getApplicationContext(), FrontPage.class));
                finish();
            }
        });
    }

}
