package com.amogus.digs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ResourceBusyException;
import android.os.Build;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.UUID;

//used for storing methods and variables that can be shared to other classes
public class Singleton {
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private static Context activityContext;
    private static Singleton instance;

    private final int REQUESTCODE_BLUETOOTH_PERMISSIONS = 8;
    private final int REQUESTCODE_LOCATION_PERMISSIONS = 7;

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

    public void saveDeviceBluetoothName(String bluetoothName) {
        editor.putString("Device Bluetooth Name", bluetoothName);
        editor.commit();
    }

    public String getDeviceBluetoothName() {
        return preferences.getString("Device Bluetooth Name", "");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable getImage(String imageName) {
        File file = new File(activityContext.getFilesDir(), imageName);
        Drawable image = null;
        if (file.exists()) {
            image = Drawable.createFromPath(file.toString());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image = activityContext.getDrawable(R.drawable.baseline_account_circle_24_gray);
            }
        }
        return image;
    }

    public boolean isGPS_Enabled() {
        LocationManager locationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public int getREQUESTCODE_BLUETOOTH_PERMISSIONS() {
        return REQUESTCODE_BLUETOOTH_PERMISSIONS;
    }

    public int getREQUESTCODE_LOCATION_PERMISSIONS() {
        return REQUESTCODE_LOCATION_PERMISSIONS;
    }

    public String getAPP_NAME() {
        return activityContext.getString(R.string.app_name);
    }
}
