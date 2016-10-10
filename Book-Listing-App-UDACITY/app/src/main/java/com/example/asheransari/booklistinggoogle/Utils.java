package com.example.asheransari.booklistinggoogle;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by asher.ansari on 10/10/2016.
 */
public class Utils {

    private Utils()
    {

    }
    public static void hideKeyboard(Activity activity, IBinder windowTOken)
    {
        final InputMethodManager manager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(windowTOken, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public static boolean hasActiveNetwork(Context context)
    {
        final ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = manager.getActiveNetworkInfo();
        return info !=null && info.isConnectedOrConnecting();
    }
    public static void showToast(Context context, String msg)
    {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
