package me.buddyoruna.appinspeccion.ui.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class MessageUtil {

    public static void message(Context context, @StringRes int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void message(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
