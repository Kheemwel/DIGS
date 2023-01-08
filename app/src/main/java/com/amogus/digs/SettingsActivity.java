package com.amogus.digs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.amogus.digs.utilities.SharedPrefManager;

//This is the java activity for settings layout
public class SettingsActivity extends AppCompatActivity {
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this will display the settings layout.
        setContentView(R.layout.activity_settings);

        //getting the instance of the Singleton
        sharedPrefManager = SharedPrefManager.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        //this will display the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        //this two line of code will display the back button on the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        EditText inputName = findViewById(R.id.editTextUserName);
        EditText inputContact  = findViewById(R.id.editTextContactNumber);
        ImageView profilePic = findViewById(R.id.imgProfile);

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
                sharedPrefManager.setUser_name(s.toString());
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
                sharedPrefManager.setContact_number(s.toString());
            }
        });

        //get the saved username from shared preference and set it to the edittext
        inputName.setText(sharedPrefManager.getUser_name());
        //get the saved contact from shared preference and set it to the edittext
        inputContact.setText(sharedPrefManager.getContact_number());
        //get the saved image from internal storage and set it to the image view
    }

    @Override
    public boolean onSupportNavigateUp() {
        //this will close the activity and come back to the previous activity (which is the main activity)
        //when the back button on the toolbar or on the device is pressed.
        onBackPressed();
        return true;
    }
}