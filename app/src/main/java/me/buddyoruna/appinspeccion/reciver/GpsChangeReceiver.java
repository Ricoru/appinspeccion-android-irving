package me.buddyoruna.appinspeccion.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import me.buddyoruna.appinspeccion.BuildConfig;
import me.buddyoruna.appinspeccion.ui.util.GPSUtil;

public class GpsChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Intent it = new Intent(BuildConfig.APPLICATION_ID + "." + GPSUtil.INTENT_NAME_CHANGE_STATUS);
            LocalBroadcastManager.getInstance(context).sendBroadcast(it);
        }
    }

}