package com.board.game.sasha;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;


public class SettingsScreen extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private  ListPreference theme,grid,sound;
    private  final String MyPREFERENCES = "gameprefs" ;
    private  final String Grid = "grid";
    private  final String Sound = "sound";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        grid = (ListPreference)findPreference("grid");
        sound = (ListPreference)findPreference("sound");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        updatePreferences();
        updateSettingsUI();
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equalsIgnoreCase("grid")){
            String value = grid.getValue();
            grid.setSummary(value);
        }
        else if(key.equalsIgnoreCase("sound")){
            String value = sound.getValue();
            sound.setSummary(value);
        }
        updatePreferences();
    }
    private void updatePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Grid, grid.getValue());
        editor.putString(Sound,sound.getValue());
        editor.commit();
    }

    private void updateSettingsUI() {
        grid.setSummary(grid.getValue());
        sound.setSummary(sound.getValue());
    }
}
