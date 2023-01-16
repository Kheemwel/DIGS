package com.amogus.digs.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private SharedPrefManager() {} // private constructor to prevent instantiation

    public static void initialize(Context context) {
        preferences = context.getSharedPreferences("digs_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static String getUser_name() {
        return preferences.getString("User Name", "");
    }

    public static void setUser_name(String user_name) {
        editor.putString("User Name", user_name);
        editor.apply();
    }

    public static String getContact_number() {
        return preferences.getString("Contact Number", "");
    }

    public static void setContact_number(String contact_number) {
        editor.putString("Contact Number", contact_number);
        editor.apply();
    }

    public static void saveDeviceBluetoothName(String bluetoothName) {
        editor.putString("Device Bluetooth Name", bluetoothName);
        editor.apply();
    }

    public static String getDeviceBluetoothName() {
        return preferences.getString("Device Bluetooth Name", "");
    }
}
