package com.test.board;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        theme = sharedPreferences.getString("theme","white");
        grid = sharedPreferences.getString("grid","3");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_front_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
