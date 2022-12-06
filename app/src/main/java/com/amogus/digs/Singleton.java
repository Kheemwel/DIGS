package com.amogus.digs;

import android.content.Context;
import android.content.SharedPreferences;

//used for storing methods and variables that can be shared to other classes
public class Singleton {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static Singleton instance;

    private Singleton(Context applicationContext) {
        preferences = applicationContext.getSharedPreferences("digs_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static Singleton getInstance(Context applicationContext) {
        if (instance == null) {
            instance = new Singleton(applicationContext);
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
}
