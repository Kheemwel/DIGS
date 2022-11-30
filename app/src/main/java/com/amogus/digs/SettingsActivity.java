package com.amogus.digs;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//This is the java activity for settings layout
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this will display the settings layout.
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        //this will display the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        //this will display the back button on the toolbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Settings");
    }

    @Override
    public boolean onSupportNavigateUp() {
        //this will close the activity and come back to the previous activity (which is the main activity)
        //when the back button on the toolbar or on the device is pressed.
        onBackPressed();
        return true;
    }
}