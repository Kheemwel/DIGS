package com.amogus.digs;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

//This is the java activity for settings layout
public class SettingsActivity extends AppCompatActivity {
    private Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this will display the settings layout.
        setContentView(R.layout.activity_settings);

        //getting the instance of the Singleton
        singleton = Singleton.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        //this will display the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        //this two line of code will display the back button on the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        EditText inputName = findViewById(R.id.editTextUserName);
        EditText inputContact  = findViewById(R.id.editTextContactNumber);

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                singleton.setUser_name(s.toString());
            }
        });

        inputContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                singleton.setContact_number(s.toString());
            }
        });

        inputName.setText(singleton.getUser_name());
        inputContact.setText(singleton.getContact_number());
    }

    @Override
    public boolean onSupportNavigateUp() {
        //this will close the activity and come back to the previous activity (which is the main activity)
        //when the back button on the toolbar or on the device is pressed.
        onBackPressed();
        return true;
    }
}