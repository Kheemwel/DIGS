package com.amogus.digs.managers;

public class GPSManager {
    private static GPSManager gPSManagerInstance;

    private GPSManager() {
    }

    public static GPSManager getInstance() {
        if (gPSManagerInstance == null) {
            gPSManagerInstance = new GPSManager();
        }
        return gPSManagerInstance;
    }

}
