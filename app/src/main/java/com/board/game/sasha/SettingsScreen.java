package com.board.game.sasha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.board.game.sasha.commonutils.GlobalConstants;


public class SettingsScreen extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
,Preference.OnPreferenceClickListener{
    private  ListPreference grid,sound,mode;
    private Preference image;
    private  final String Grid = "grid";
    private  final String Sound = "sound";
    private  final String Mode = "mode";
    private  final String Image = "image";
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    SharedPreferences sharedPreferences;
    private SharedPreferences pref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        pref = getSharedPreferences(GlobalConstants.pref_file,Context.MODE_PRIVATE);
        grid = (ListPreference)findPreference("grid");
        sound = (ListPreference)findPreference("sound");
        mode = (ListPreference)findPreference("mode");
        image = findPreference("image");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        sharedPreferences = getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE);
      //  updatePreferences();
        updateSettingsUI();
        image.setOnPreferenceClickListener(this);
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
        }else if(key.equalsIgnoreCase("mode"))
        {
            String value = mode.getValue();
            mode.setSummary(value);
            updateImagePreference();
        }
        updatePreferences();
    }

    private void updateImagePreference() {
       if(mode.getValue().equalsIgnoreCase("number")){
           image.setEnabled(false);
       }else{
           image.setEnabled(true);
       }
    }


    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Toast.makeText(this, "picked Image",
                        Toast.LENGTH_LONG).show();

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("image_path",imgDecodableString);
                editor.commit();
                // Set the Image in ImageView after decoding the String
               // imgView.setImageBitmap(BitmapFactory
                 //       .decodeFile(imgDecodableString));

                Toast.makeText(this, "You have picked Image="+imgDecodableString,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


    private void updatePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Grid,grid.getValue());
        editor.putString(Sound,sound.getValue());
        editor.putString(Mode,mode.getValue());
        editor.commit();
    }

    private void updateSettingsUI() {
        grid.setValue(pref.getString("grid","3"));
        sound.setValue(pref.getString("sound","on"));
        mode.setValue(pref.getString("mode","Number Puzzle"));
        grid.setSummary(pref.getString("grid", "3"));
        sound.setSummary(pref.getString("sound","on"));
        mode.setSummary(pref.getString("mode","Number Puzzle"));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equalsIgnoreCase("image")){
            loadImagefromGallery();
            return true;
        }
        return false;
    }
}
