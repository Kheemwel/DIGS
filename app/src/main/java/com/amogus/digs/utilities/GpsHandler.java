package com.amogus.digs.utilities;

import android.content.Context;
import android.location.LocationManager;

public class GpsHandler {


    public static final int REQUESTCODE_LOCATION_PERMISSIONS = 7;

    private GpsHandler() {

    }
    public static boolean isGPS_Enabled(Context activityContext) {
        LocationManager locationManager = (LocationManager) activityContext.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
