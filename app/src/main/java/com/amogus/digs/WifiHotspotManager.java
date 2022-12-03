package com.amogus.digs;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;

public class WifiHotspotManager {


    public static final String TAG = "WifiHotspotManager";
    private final WifiManager wifiManager;
    private Context context;

    public WifiHotspotManager(Context context) {
        this.context = context;
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }

    public void showWritePermissionSettings(boolean force) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (force || !Settings.System.canWrite(this.context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(intent);
            }
        }
    }

    //check whether wifi hotspot on or off
    public boolean isWifiHotspotOn() {
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiHotspotEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // Turn wifi hotspot on
    public boolean turnWifiHotspotOn() {
        WifiConfiguration wificonfiguration = null;
        try {
            Method method = wifiManager.getClass().getMethod("setWifiHotspotEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, wificonfiguration, true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Turn wifiAp hotspot off
    public boolean turnWifiHotspotOff() {
        WifiConfiguration wificonfiguration = null;
        try {
            Method method = wifiManager.getClass().getMethod("setWifiHotspotEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, wificonfiguration, false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean createNewNetwork(String ssid, String password) {
        wifiManager.setWifiEnabled(false); // turn off Wifi
        if (isWifiHotspotOn()) {
            turnWifiHotspotOff();
            return false;
        } else {
            Log.e(TAG, "Wifi Hotspot is turned off");

        }

        // creating new wifi configuration
        WifiConfiguration myConfig = new WifiConfiguration();
        myConfig.SSID = ssid; // SSID name of netwok
        //myConfig.preSharedKey = password; // password for network
        myConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // 4 is for KeyMgmt.WPA2_PSK which is not exposed by android KeyMgmt class
        myConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN); // Set Auth Algorithms to open
        try {
            Method method = wifiManager.getClass().getMethod("setWifiHotspotEnabled", WifiConfiguration.class, boolean.class);
            return (Boolean) method.invoke(wifiManager, myConfig, true);  // setting and turing on android wifiap with new configrations
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
