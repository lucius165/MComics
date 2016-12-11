package vn.edu.poly.mcomics.object.handle.backgroundtask;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import vn.edu.poly.mcomics.R;

/**
 * Created by Tu on 12/5/2016.
 */

public final class CheckInternet {

    public static boolean check(Activity activity) {
        ConnectivityManager conMgr = (ConnectivityManager) activity.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}
