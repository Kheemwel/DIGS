package com.amogus.digs.utilities;

import android.app.AlertDialog;
import android.content.Context;
import com.amogus.digs.R;

public class AppUtils {

    public static String user_civilian = "Civilian";
    public static String user_authority = "Authority";

    private AppUtils() {

    }

    public static String getApplicationName(Context activityContext) {
        return activityContext.getString(R.string.app_name);
    }

    public static void showSimpleDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setNeutralButton("OK",((dialog, which) -> dialog.dismiss()))
                .show();
    }
}
