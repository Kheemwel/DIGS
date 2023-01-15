package com.amogus.digs.utilities;

import android.content.Context;
import android.content.SharedPreferences;

//used for storing methods and variables that can be shared to other classes
public class SharedPrefManager {
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private static SharedPrefManager instance;

    private SharedPrefManager(Context activityContext) {
        preferences = activityContext.getSharedPreferences("digs_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static SharedPrefManager getInstance(Context activityContext) {
        if (instance == null) {
            instance = new SharedPrefManager(activityContext);
        }
        return instance;
    }

    public String getUser_name() {
        return preferences.getString("User Name", "");
    }

    public void setUser_name(String user_name) {
        editor.putString("User Name", user_name);
        editor.apply();
    }

    public String getContact_number() {
        return preferences.getString("Contact Number", "");
    }

    public void setContact_number(String contact_number) {
        editor.putString("Contact Number", contact_number);
        editor.apply();
    }

    public void saveDeviceBluetoothName(String bluetoothName) {
        editor.putString("Device Bluetooth Name", bluetoothName);
        editor.apply();
    }

    public String getDeviceBluetoothName() {
        return preferences.getString("Device Bluetooth Name", "");
    }

}
