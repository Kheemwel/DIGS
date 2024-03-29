package com.amogus.digs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.amogus.digs.utilities.AppUtils;
import com.amogus.digs.utilities.SharedPrefManager;

public class LogInActivity extends AppCompatActivity {
    private String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //intialize the SharedPrefUtils to use the shared preferences
        SharedPrefManager.initialize(this);
        userType = SharedPrefManager.getUserType();

        //check if there is already a data of user type, if not empty proceed to main activity
        if (!userType.equals("")) {
            //open MainActivity
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
        } else {
            //display the layout if the condition above is not met
            setContentView(R.layout.activity_log_in);
        }

        EditText edtxt_FullName = findViewById(R.id.edtxt_fullname);
        EditText edtxt_Contact = findViewById(R.id.edtxt_contact);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton rbtn_Civilian = findViewById(R.id.rbtn_civilian);
        RadioButton rbtn_Authority = findViewById(R.id.rbtn_authority);
        Button btnLogIn = findViewById(R.id.btnLogIn);

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
        edtxt_FullName.setFilters(new InputFilter[]{filterLetters}); //set the filter created above
        edtxt_Contact.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)}); //the filter only accepts 11 digits

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_civilian:
                        userType = AppUtils.user_civilian;
                        break;
                    case R.id.rbtn_authority:
                        userType = AppUtils.user_authority;
                        break;
                }
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtxt_FullName.getText().toString().trim().isEmpty()) {
                    edtxt_FullName.setError("This field is required"); //show a warning in the full name textbox if it is empty
                } else if (edtxt_Contact.getText().toString().trim().isEmpty()) {
                    edtxt_Contact.setError("This field is required"); //show a warning in the contact textbox if it is empty
                } else if (!rbtn_Civilian.isChecked() && !rbtn_Authority.isChecked()) {
                    //this method is from the AppUtils class and this will show a dialog without creating a new instance of AlertDialog
                    //we just need to pass the necessary arguments and the method will make all the work
                    AppUtils.showSimpleDialog(LogInActivity.this,"", "Please choose a type of user who will use this app.");
                } else {
                    //create a new instance of AlertDialog for customization
                    new AlertDialog.Builder(LogInActivity.this)
                            .setMessage("Are you sure you the information you provided is correct? You can still change the name and contact later except the user type")
                            .setPositiveButton("Yes", ((dialog, which) -> {
                                SharedPrefManager.setFullName(edtxt_FullName.getText().toString());
                                SharedPrefManager.setContactNumber(edtxt_Contact.getText().toString());
                                SharedPrefManager.setUserType(userType);

                                //open MainActivity
                                startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                finish();
                            })) //create a button with a function
                            .setNegativeButton("No", ((dialog, which) -> dialog.dismiss())) //delete/hide this dialog
                            .show(); //show this dialog
                }
            }
        });
    }
}