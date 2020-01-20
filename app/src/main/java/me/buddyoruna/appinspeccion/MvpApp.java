package me.buddyoruna.appinspeccion;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

/**
 * Created by Ricoru on 7/02/18.
 */

public class MvpApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized Context getContext() {
        return context;
    }

}
