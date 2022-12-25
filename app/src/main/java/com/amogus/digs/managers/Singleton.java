package com.amogus.digs.managers;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.amogus.digs.R;

import java.io.File;
import java.util.UUID;

import static androidx.core.app.ActivityCompat.requestPermissions;

//used for storing methods and variables that can be shared to other classes
public class Singleton {
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private static Context activityContext;
    private static Singleton instance;
    private final UUID App_UUID = UUID.fromString("16ac2bc2-82ce-11ed-a1eb-0242ac120002");

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

    public boolean isGPS_Enabled() {
        LocationManager locationManager = (LocationManager)activityContext.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public UUID getThisAppUUID() {
        return App_UUID;
    }

}
