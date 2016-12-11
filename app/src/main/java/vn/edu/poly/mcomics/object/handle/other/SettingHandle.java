package vn.edu.poly.mcomics.object.handle.other;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.LinearLayoutManager;

import vn.edu.poly.mcomics.R;
import vn.edu.poly.mcomics.object.handle.eventlistener.OrientationChangeListener;

/**
 * Created by lucius on 06/12/2016.
 */

public final class SettingHandle {
    private SQLiteDatabase db;
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    private OrientationChangeListener orientationListener;

    public SettingHandle(Activity activity) {
        db = activity.openOrCreateDatabase(activity.getResources().getString(R.string.mcomics_database), Context.MODE_PRIVATE, null);
        createTable();
    }

    public void createTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS setting(" +
                "setting_name text," +
                "value text" +
                ")");
        setDefault();
    }

    public void setDefault() {
        Cursor cursor = db.rawQuery("SELECT * FROM setting WHERE setting_name = 'orientation'", null);
        if (cursor.getCount() <= 0) {
            db.execSQL("INSERT INTO setting VALUES ( 'orientation', " + VERTICAL + ")");
        }
    }

    public int getOrientation() {
        Cursor cursor = db.rawQuery("SELECT * FROM setting WHERE setting_name = 'orientation'", null);
        if (cursor.getCount() == 1) {
            if (cursor.moveToNext() && cursor.getInt(1) == LinearLayoutManager.HORIZONTAL) {
                return LinearLayoutManager.HORIZONTAL;
            }
        }
        return LinearLayoutManager.VERTICAL;
    }

    public void setOrientation(int orientation) {
        db.execSQL("UPDATE setting SET value= " + orientation + " WHERE setting_name == 'orientation'");
        if (this.orientationListener != null) {
            this.orientationListener.onChanged(orientation);
        }
    }

    public void setOrientationListener(OrientationChangeListener orientationListener) {
        this.orientationListener = orientationListener;
    }
}


