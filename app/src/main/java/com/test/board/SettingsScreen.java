package com.test.board;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class SettingsScreen extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private ListPreference theme,grid;
    public static final String MyPREFERENCES = "MyPrefs1" ;
    public static final String Grid = "grid";
    public static final String Theme = "theme";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        theme = (ListPreference)findPreference("theme");
        grid = (ListPreference)findPreference("grid");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        updatePreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_screen, menu);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equalsIgnoreCase("theme")){
            String value  = theme.getValue();
           theme.setSummary(value);
        }
        else if(key.equalsIgnoreCase("grid")){
            String value = grid.getValue();
            grid.setSummary(value);

        }
        //updatePreferences();
    }
    private void updatePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Grid, grid.getValue());
        editor.putString(Theme, theme.getValue());
        editor.commit();
    }

    private void updateSettingsUI() {
    }
}
