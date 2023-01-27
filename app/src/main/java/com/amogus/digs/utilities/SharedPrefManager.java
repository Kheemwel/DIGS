package com.amogus.digs.utilities;

import android.content.Context;
import android.content.SharedPreferences;

//this singleton class is made to hold methods for sharedpreferences
public class SharedPrefManager {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private SharedPrefManager() {} // private constructor to prevent instantiation

    //this acts as a constructor since the constructor can't be instantiated
    //this methods should be call first so that the other methods can be use
    //is it recommended to call this method in the constructor of a class
    public static void initialize(Context context) {
        preferences = context.getSharedPreferences("digs_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    //return the fullname based on the User Name key
    //return a default value (blank string or "") if the User Name key is not found in the sharedpreferences
    public static String getFullName() {
        return preferences.getString("User Name", "");
    }

    //set the value of the User Name key base on the inputted value in the parameter
    public static void setFullName(String user_name) {
        editor.putString("User Name", user_name);
        editor.apply(); //you can use .commit() but apply() is better since it also close the resources after being used
    }

    //get the value of the Contact Number key and return it
    public static String getContactNumber() {
        return preferences.getString("Contact Number", "");
    }

    public static void setContactNumber(String contact_number) {
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

    public static void setUserType(String userType) {
        editor.putString("User Type", userType);
        editor.apply();
    }

    public static String getUserType() {
        return preferences.getString("User Type", "");
    }
}
