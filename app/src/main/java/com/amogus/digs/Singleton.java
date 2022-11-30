package com.amogus.digs;

//used for storing methods and variables that can be shared to other classes
public class Singleton {
    private static Singleton instance;
    private String user_name = "";
    private String contact_number = "";

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }
}
