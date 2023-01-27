package com.amogus.digs.utilities;

import android.app.AlertDialog;
import android.content.Context;
import com.amogus.digs.R;

//utility class that holds static variables and methods that can be used by any classes without creating an instance
//this class contains the variables and methods that are related to application
public class AppUtils {

    public static String user_civilian = "Civilian";
    public static String user_authority = "Authority";

    //the constructor is made to be private so that it can't be instantiated
    private AppUtils() {}

    //return the name of the applcation
    public static String getApplicationName(Context activityContext) {
        return activityContext.getString(R.string.app_name);
    }

    //a method that create a single instance of an alertdialog
    //the title and message it display is based on the inputted value in the parameters
    public static void showSimpleDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setNeutralButton("OK",((dialog, which) -> dialog.dismiss())) //delete/hide this dialog
                .show(); //show this dialog
    }
}
