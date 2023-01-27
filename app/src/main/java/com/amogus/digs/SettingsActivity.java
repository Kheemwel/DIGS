package com.amogus.digs;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.amogus.digs.utilities.AppUtils;
import com.amogus.digs.utilities.SharedPrefManager;

import static com.amogus.digs.utilities.SharedPrefManager.*;

//This is the java activity for settings layout
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this will display the settings layout.
        setContentView(R.layout.activity_settings);

        //intialize the SharedPrefUtils to use the shared preferences
        SharedPrefManager.initialize(this);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        //this will display the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        //this two line of code will display the back button on the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        EditText inputName = findViewById(R.id.editTextUserName);
        EditText inputContact  = findViewById(R.id.editTextContactNumber);
        TextView txtUserType = findViewById(R.id.txtUserType);
        ImageView profilePic = findViewById(R.id.imgProfile);

        //this filter is made to block other characters and only accept letters, space, and dot
        InputFilter filterLetters = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i)) && source.charAt(i) != '.') {
                        return "";
                    }
                }
                return null;
            }
        };
        inputName.setFilters(new InputFilter[]{filterLetters}); //set the filter created above
        inputContact.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)}); //the filter only accepts 11 digits
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            //this will be called after the user type in the textbox
            @Override
            public void afterTextChanged(Editable s) {
                setFullName(s.toString()); //get the text in the full name textbox and save it to the sharedpreferences
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

            //this will be called after the user type in the textbox
            @Override
            public void afterTextChanged(Editable s) {
                setContactNumber(s.toString()); //get the text in the contact textbox and save it to the sharedpreferences
            }
        });

        //get the saved username from shared preference and set it to the edittext
        inputName.setText(getFullName());
        //get the saved contact from shared preference and set it to the edittext
        inputContact.setText(getContactNumber());
        //set the image based on the user type
        if (SharedPrefManager.getUserType().equals(AppUtils.user_civilian)) {
            profilePic.setImageDrawable(getResources().getDrawable(R.drawable.emoji_person_female));
        } else {
            profilePic.setImageDrawable(getResources().getDrawable(R.drawable.emoji_police));
        }

        txtUserType.setText(SharedPrefManager.getUserType());
    }

    @Override
    public boolean onSupportNavigateUp() {
        //this will close the activity and come back to the previous activity (which is the main activity)
        //when the back button on the toolbar or on the device is pressed.
        onBackPressed();
        return true;
    }
}