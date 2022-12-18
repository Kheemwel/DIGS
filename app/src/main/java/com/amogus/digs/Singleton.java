package com.amogus.digs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;

import java.io.File;

//used for storing methods and variables that can be shared to other classes
public class Singleton {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static Context activityContext;

    private static Singleton instance;

    private Singleton(Context activityContext) {
        Singleton.activityContext = activityContext;
        preferences = Singleton.activityContext.getSharedPreferences("digs_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static Singleton getInstance(Context activityContext) {
        if (instance == null) {
            instance = new Singleton(activityContext);
        }
        return instance;
    }


    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public String getUser_name() {
        return preferences.getString("User Name", "");
    }

    public void setUser_name(String user_name) {
        editor.putString("User Name", user_name);
        editor.commit();
    }

    public String getContact_number() {
        return preferences.getString("Contact Number", "");
    }

    public void setContact_number(String contact_number) {
        editor.putString("Contact Number", contact_number);
        editor.commit();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable getImage(String imageName) {
        File file = new File(activityContext.getFilesDir(), imageName);
        Drawable image = null;
        if (file.exists()) {
            image = Drawable.createFromPath(file.toString());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image = activityContext.getDrawable(R.drawable.gg_profile_black);
            }
        }
        return image;
    }

    public boolean getGPSStatus() {
        LocationManager locationManager = (LocationManager)activityContext.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
