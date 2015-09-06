package com.board.game.sasha.settings;

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
import android.preference.PreferenceCategory;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.board.game.sasha.R;
import com.board.game.sasha.commonutils.GlobalConstants;
import com.board.game.sasha.commonutils.Utils;

import java.io.File;


public class SettingsScreen extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
        , Preference.OnPreferenceClickListener {
    private ListPreference grid, sound, mode;
    private Preference image;
    private final String Grid = "grid";
    private final String Sound = "sound";
    private final String Mode = "mode";
    private final String Image = "image";
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    SharedPreferences sharedPreferences;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        //setContentView(R.layout.settingsplaceholder);
        pref = getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE);
        grid = (ListPreference) findPreference("grid");
        sound = (ListPreference) findPreference("sound");
        mode = (ListPreference) findPreference("mode");
        image = findPreference("image");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        sharedPreferences = getSharedPreferences(GlobalConstants.pref_file, Context.MODE_PRIVATE);
        //  updatePreferences();
        updateSettingsUI();
        image.setOnPreferenceClickListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("grid")) {
            grid.setSummary(grid.getValue());
        } else if (key.equalsIgnoreCase("sound")) {
            sound.setSummary(sound.getValue());
        } else if (key.equalsIgnoreCase("mode")) {
            mode.setSummary(mode.getValue());
            updateImagePreference();
        }
    }

    private void updateImagePreference() {
        PreferenceCategory category = (PreferenceCategory) findPreference("main_category");
        if (mode.getValue().equalsIgnoreCase("number")) {
            category.removePreference(image);
        } else {
            category.addPreference(image);
            File f = new File(pref.getString(GlobalConstants.image_path,"No File Found"));
            if(f.exists()){
                image.setSummary(f.getName());
            }else{
                image.setSummary(getResources().getString(R.string.default_image_selected));
            }
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

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                if(!(imgDecodableString.endsWith("jpg")||imgDecodableString.endsWith("png"))) {
                    image.setSummary("No File Choosen");
                    throw new IllegalArgumentException();
                }
                image.setSummary(imgDecodableString);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(GlobalConstants.image_path,imgDecodableString);
                editor.commit();

            } else {
                image.setSummary(getResources().getString(R.string.default_image_selected));
            }
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Choose jpg/png only",Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


    private void updateSettingsUI() {
        grid.setSummary(grid.getValue());
        sound.setSummary(sound.getValue());
        mode.setSummary(mode.getValue());
        updateImagePreference();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equalsIgnoreCase("image")) {
            loadImagefromGallery();
            return true;
        }
        return false;
    }
}
