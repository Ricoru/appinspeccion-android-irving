package me.buddyoruna.appinspeccion.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import me.buddyoruna.appinspeccion.R;

public class GPSUtil {

    public static final String INTENT_NAME_CHANGE_STATUS = "STATUS_GPS_CHANGE";

    private Activity activity;

    public GPSUtil(Activity activity) {
        this.activity = activity;
    }

    public static boolean isGPSEnabled(Context context) {
        boolean isGPSEnabled = false;
        LocationManager locationManager;
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isGPSEnabled;
    }

    public MaterialDialog showSettingsGPS(@StringRes int content) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(R.string.msj_gps_title)
                .content(content)
                .titleGravity(GravityEnum.CENTER)
                .backgroundColorRes(R.color.colorBackgroundDialog)
                .titleColorRes(R.color.colorText)
                .contentColorRes(R.color.colorText)
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.action_setting)
                .negativeText(R.string.action_cancel)
                .onNegative((@NonNull MaterialDialog dialog, @NonNull DialogAction which) -> {
                    dialog.dismiss();
                })
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                    dialog.dismiss();
                });
        MaterialDialog dialog = builder.build();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public MaterialDialog showSettingsGPSBlocked(@StringRes int content) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(R.string.msj_gps_title)
                .content(content)
                .titleGravity(GravityEnum.CENTER)
                .backgroundColorRes(R.color.colorBackgroundDialog)
                .titleColorRes(R.color.colorText)
                .contentColorRes(R.color.colorText)
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.action_setting)
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                    dialog.dismiss();
                });
        MaterialDialog dialog = builder.build();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
