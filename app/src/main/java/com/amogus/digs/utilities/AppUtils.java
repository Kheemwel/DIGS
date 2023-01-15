package com.amogus.digs.utilities;

import android.content.Context;
import com.amogus.digs.R;

public class AppUtils {
    private AppUtils() {

    }

    public static String getApplicationName(Context activityContext) {
        return activityContext.getString(R.string.app_name);
    }
}
