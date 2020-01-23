package me.buddyoruna.appinspeccion.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.buddyoruna.appinspeccion.BuildConfig;
import me.buddyoruna.appinspeccion.R;
import me.buddyoruna.appinspeccion.reciver.GpsChangeReceiver;
import me.buddyoruna.appinspeccion.ui.util.GPSUtil;
import me.buddyoruna.appinspeccion.ui.util.MessageUtil;
import me.buddyoruna.appinspeccion.ui.util.PermisosUtil;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private GPSUtil mGPSUtil;
    private Location myLocation;
    private LatLng myLocationLatLng;
    private MaterialDialog progressDialogRequired;
    private MaterialDialog gpsDialog;
    private BroadcastReceiver gpsChangeReceiver;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGPSUtil = new GPSUtil(this);
        gpsDialog = mGPSUtil.showSettingsGPSBlocked(R.string.msg_gps_configutarion_mapa);

        isPermisosLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

//        mMap.setOnMyLocationChangeListener(location -> {
//            myLocation = location;
//            //Log.i("INFO", "location changeListener " + location);
//        });

        mMap.setOnCameraMoveStartedListener(i -> {
            Log.i("INFO", "setOnCameraMoveStartedListener");
        });

        mMap.setOnCameraIdleListener(() -> {
            Log.i("INFO", "setOnCameraIdleListener");
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                myLocationLatLng = marker.getPosition();
            }
        });

        mMap.setOnMarkerClickListener(marker -> {
            myLocationLatLng = marker.getPosition();
            return false;
        });

        getLastLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GPSUtil.isGPSEnabled(MainActivity.this)) {
            gpsDialog.show();
        }

        if (gpsChangeReceiver == null) {
            gpsChangeReceiver = new GpsChangeReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    super.onReceive(context, intent);
                    if (gpsDialog.isShowing()) gpsDialog.dismiss();
                    if (!GPSUtil.isGPSEnabled(MainActivity.this)) {
                        gpsDialog.show();
                    } else {
                        getLastLocation();
                    }
                }
            };
        }

        IntentFilter filter = new IntentFilter(BuildConfig.APPLICATION_ID + "." + GPSUtil.INTENT_NAME_CHANGE_STATUS);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(gpsChangeReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_inspeccion:
                if (myLocationLatLng == null) {
                    MessageUtil.message(MainActivity.this, "Es necesario seleccionar una ubicación primero para registrar la inspección");
                } else {
                    mMasterSession.values.currentMyPositionLatitude = myLocationLatLng.latitude;
                    mMasterSession.values.currentMyPositionLongitude = myLocationLatLng.longitude;
                    mMasterSession.values.fileListFormDynamic = new ArrayList<>();
                    mMasterSession.update();

                    Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.action_salir:
                closeSessionDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeSessionDialog() {
        progressDialogRequired = new MaterialDialog.Builder(this)
                .content(R.string.main_logout_content)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .negativeText(R.string.action_no)
                .positiveText(R.string.action_ok)
                .positiveColorRes(R.color.colorPrimary)
                .onNegative((materialDialog, dialogAction) -> progressDialogRequired.dismiss())
                .onPositive((materialDialog, dialogAction) -> {
                    progressDialogRequired.dismiss();
                    limpiarSession();
                })
                .build();
        progressDialogRequired.show();
    }

    private void limpiarSession() {
        mMasterSession.values.currentUser = null;
        mMasterSession.values.currentMyPositionLatitude = 0;
        mMasterSession.values.currentMyPositionLongitude = 0;
        mMasterSession.values.fileListFormDynamic = new ArrayList<>();
        mMasterSession.update();
        FirebaseAuth.getInstance().signOut();//Limpiar sessión Firebase
        finish();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        pintarMapa();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        myLocation = location;
        myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void pintarMapa() {
        mMap.clear();
        try {
            if (myLocation != null) {
                mMap.addMarker(new MarkerOptions()
                        .title("Ubicación actual")
                        .snippet("Realizar inspección")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                        14f));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    Log.i("INFO", "location " + location);
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        myLocation = location;
                        myLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        pintarMapa();
                        // Logic to handle location object
                    }
                });
    }

    private boolean isPermisosLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (checkLocationPermission()) {
                getLastLocation();
                return true;
            }
            return false;
        }
        getLastLocation();
        return true;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permiso requerido")
                        .setMessage("Esta aplicación necesita acceder a tu ubicación para mostrar el mapa")
                        .setPositiveButton("OK", (DialogInterface dialogInterface, int i) -> {
                            //Prompt the user once explanation has been shown
                            PermisosUtil.askLocationPermission(MainActivity.this);
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                PermisosUtil.askLocationPermission(MainActivity.this);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gpsChangeReceiver != null) {
            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(gpsChangeReceiver);
        }
    }
}
