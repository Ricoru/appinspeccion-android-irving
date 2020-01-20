package me.buddyoruna.appinspeccion.ui.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.buddyoruna.appinspeccion.R;

/**
 * Created by Ricoru on 12/11/16.
 */

public class Util {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private Context context;

    public Util(Context context){
        this.context = context;
    }

    public String number2Digits(int number){
        if(number<10){
            return ("0"+number);
        }else{
            return String.valueOf(number);
        }
    }

    public File getTempFile(String url) {
        File file=null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        else{
            return false;
        }
    }

    public Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (Util.isExternalStorageAvailable()) {
            //Get Uri
            //1. Get ex storage directory
            File mediaStorageDir=null;
            String appName = "Aplicacion";
            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
            } else if (mediaType == MEDIA_TYPE_VIDEO) {
                mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), appName);
            }

            //2. Create own subdirectory
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e("INFO", "Failed to create directory");
                    return null;
                }
            }
            //3.Create file name
            //4.Create the file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpeg");
            } else if (mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(path + "VID_" + timestamp + ".mp4");
            } else {
                return null;
            }
            Log.d("INFO", "File:: " + Uri.fromFile(mediaFile));
            //5. return file's Uri
            return Uri.fromFile(mediaFile);
        }
        else {
            return null;
        }
    }

    public static Date stringTofecha(String fecha, String strformat_ini){
        Date newDate=null;
        try{
            SimpleDateFormat format = new SimpleDateFormat(strformat_ini);
            newDate = format.parse(fecha);
        }catch (Exception e){
            e.printStackTrace();
        }
        return newDate;
    }

    public static String getDateNow(String formato){
        SimpleDateFormat sm = new SimpleDateFormat(formato);
        String strDate = sm.format(new Date());
        return strDate;
    }

    public static String getDateNow(Date date, String formato){
        SimpleDateFormat sm = new SimpleDateFormat(formato);
        String strDate = sm.format(date);
        return strDate;
    }

    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context,9000).show();
            } else {
                Toast.makeText(context,
                        context.getResources().getString(R.string.msg_check_playservice), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    public boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
        //return email.contains("@");
    }

    public static Calendar restarDiasToFecha(Date fecha, int dias){
        Calendar cal = new GregorianCalendar();
        cal.setTime(fecha);
        cal.set(GregorianCalendar.DAY_OF_YEAR, cal.get(GregorianCalendar.DAY_OF_YEAR)-dias);
        return cal;
    }

    public static Calendar sumarDiasToFecha(Date fecha, int dias){
        Calendar cal = new GregorianCalendar();
        cal.setTime(fecha);
        cal.set(GregorianCalendar.DAY_OF_YEAR, cal.get(GregorianCalendar.DAY_OF_YEAR)+dias);
        return cal;
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    public void requestFocus(Activity activity, View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean existeInternet()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();

        if (network != null)
        {
            return network.isAvailable();
        }

        return false;
    }

    public static void hideSoftKeyboard(Activity activity){
        if(activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}
