package com.amogus.digs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.*;

//This is the java activity for settings layout
public class SettingsActivity extends AppCompatActivity {
    private Singleton singleton;
    private ImageView profilePic;
    private static final int SELECT = 100;
    private static final String imageName = "profile_pic.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this will display the settings layout.
        setContentView(R.layout.activity_settings);

        //getting the instance of the Singleton
        singleton = Singleton.getInstance(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        //this will display the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        //this two line of code will display the back button on the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        EditText inputName = findViewById(R.id.editTextUserName);
        EditText inputContact  = findViewById(R.id.editTextContactNumber);
        profilePic = findViewById(R.id.imgProfile);

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

        //get the saved username from shared preference and set it to the edittext
        inputName.setText(singleton.getUser_name());
        //get the saved contact from shared preference and set it to the edittext
        inputContact.setText(singleton.getContact_number());
        //get the saved image from internal storage and set it to the image view
        profilePic.setImageDrawable(singleton.getImage(imageName));

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT && resultCode == RESULT_OK) {
            //get the selected image from gallery
            Uri select = data.getData();
            InputStream inputStream = null;

            try {
                assert select != null;
                inputStream = getContentResolver().openInputStream(select);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //convert the uri image to bitmap
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            File pictureFile = new File(getFilesDir(), imageName);
            try {
                //save the selected image to the internal storage (data/data/package/files/)
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //get the saved image from internal storage and set it to the image view
            profilePic.setImageDrawable(singleton.getImage(imageName));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //this will close the activity and come back to the previous activity (which is the main activity)
        //when the back button on the toolbar or on the device is pressed.
        onBackPressed();
        return true;
    }
}