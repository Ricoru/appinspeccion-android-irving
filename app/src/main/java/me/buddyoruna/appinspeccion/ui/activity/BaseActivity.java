package me.buddyoruna.appinspeccion.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.ui.util.GPSUtil;
import me.buddyoruna.appinspeccion.ui.util.MessageUtil;
import me.buddyoruna.appinspeccion.ui.util.PermisosUtil;

public class BaseActivity extends AppCompatActivity {

    protected MasterSession mMasterSession;

    protected GPSUtil mGpsUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMasterSession = MasterSession.getInstance(getApplicationContext());
        mGpsUtil = new GPSUtil(BaseActivity.this);

        if (!PermisosUtil.hasLocationPermission(BaseActivity.this)) {
            MessageUtil.message(BaseActivity.this, "Es aplicación requiere permisos de GPS, por favor habilitar los permisos para continuar");
            PermisosUtil.askLocationPermission(BaseActivity.this);
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
