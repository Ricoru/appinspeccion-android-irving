package me.buddyoruna.appinspeccion.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class MasterSession {

    private static final String TAG =  "pe.yapu.hortiplanner";
    private static final String KEY = SessionData.class.getSimpleName();

    private Gson gson;
    private SharedPreferences preferences;
    public SessionData values;

    private static MasterSession instance;

    private MasterSession(Context context) {
        this.gson = new Gson();
        this.preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public synchronized static MasterSession getInstance(Context context) {
        if(instance == null){
            instance = new MasterSession(context);
            instance.load();
        }
        return instance;
    }
    //} singleton

    private void load() {
        try {
            String jsonString = preferences.getString(KEY, "null");
            values = (jsonString.equals("null"))? new SessionData() : gson.fromJson(jsonString, SessionData.class);
        }catch (Exception e) {
            Log.e(TAG, "MasterSession:load", e);
        }
    }

    public void update() {
        try{
            SharedPreferences.Editor editor = preferences.edit();
            String jsonString =  gson.toJson(values);
            editor.putString(KEY, jsonString);
            editor.apply();
        }catch(Exception e) {
            Log.e(TAG, "MasterSession:update", e);
        }
    }

}
