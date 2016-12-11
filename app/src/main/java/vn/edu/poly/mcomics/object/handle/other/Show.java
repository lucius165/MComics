package vn.edu.poly.mcomics.object.handle.other;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by lucius on 04/12/2016.
 */

public final class Show {
    public static void toastSHORT(Context Context, String text) {
        Toast.makeText(Context, text + "", Toast.LENGTH_SHORT).show();
    }

    public static void toastLONG(Context Context, String text) {
        Toast.makeText(Context, text + "", Toast.LENGTH_LONG).show();
    }

    public static void log(String tag, String content) {
//        Log.e(tag, content + "");
    }

}
