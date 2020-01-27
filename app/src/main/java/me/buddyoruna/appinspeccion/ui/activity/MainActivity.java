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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.DialogAction;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.buddyoruna.appinspeccion.BuildConfig;
import me.buddyoruna.appinspeccion.R;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.Formulario;
import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.reciver.GpsChangeReceiver;
import me.buddyoruna.appinspeccion.ui.util.CustomDialog;
import me.buddyoruna.appinspeccion.ui.util.GPSUtil;
import me.buddyoruna.appinspeccion.ui.util.MessageUtil;
import me.buddyoruna.appinspeccion.ui.util.NetworkUtil;
import me.buddyoruna.appinspeccion.ui.util.PermisosUtil;
import me.buddyoruna.appinspeccion.viewmodel.FormularioViewModel;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    protected final CompositeDisposable mDisposableGeneric = new CompositeDisposable();
    protected final CompositeDisposable mDisposableUpdateForm = new CompositeDisposable();
    protected final CompositeDisposable mDisposableUpdateFormFotosFirebase = new CompositeDisposable();

    private GoogleMap mMap;
    private GPSUtil mGPSUtil;
    private Location myLocation;
    private LatLng myLocationLatLng;
    private CustomDialog progressDialog;
    private MaterialDialog progressDialogRequired;
    private MaterialDialog gpsDialog;
    private MaterialDialog dialogConfirmarRegistro;
    private BroadcastReceiver gpsChangeReceiver;

    private FusedLocationProviderClient fusedLocationClient;
    private FormularioViewModel modelView;

    private boolean isValidarPendientes = false;
    private List<Formulario> formularioListPendientes;
    private List<FileInspeccion> fileInspeccionListPendientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FormularioViewModel.Factory formularioViewModelFactory = new FormularioViewModel.Factory(getApplication());
        modelView = ViewModelProviders.of(this, formularioViewModelFactory).get(FormularioViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGPSUtil = new GPSUtil(this);
        gpsDialog = mGPSUtil.showSettingsGPSBlocked(R.string.msg_gps_configutarion_mapa);

        isPermisosLocation();

        limpiarTabla();
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
                if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
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
                } else {
                    mMasterSession.values.currentMyPositionLatitude = myLocationLatLng != null ? myLocationLatLng.latitude : 0;
                    mMasterSession.values.currentMyPositionLongitude = myLocationLatLng != null ? myLocationLatLng.longitude : 0;
                    mMasterSession.values.fileListFormDynamic = new ArrayList<>();
                    mMasterSession.update();

                    Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.action_sincronizar:
                if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
                    isValidarPendientes = true;
                    showInspeccionesPendientes();
                } else {
                    MessageUtil.message(MainActivity.this, "No hay conexión a internet para sincronizar la información.");
                }
                break;
            case R.id.action_salir:
                closeSessionDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

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

    private void limpiarTabla(){
        mDisposableGeneric.add(modelView.eliminarFormulariosSincronizados()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    private void showInspeccionesPendientes() {
        modelView.obtenerFormulariosPendientes().observe(this, mapFormularios -> {
            if (!isValidarPendientes) {
                return;
            }
            isValidarPendientes = false;

            formularioListPendientes = new ArrayList<>();
            if (mapFormularios.containsKey("formularios")) {
                formularioListPendientes = (List<Formulario>) mapFormularios.get("formularios");
            }

            fileInspeccionListPendientes = new ArrayList<>();
            if (mapFormularios.containsKey("formulariosFotos")) {
                fileInspeccionListPendientes = (List<FileInspeccion>) mapFormularios.get("formulariosFotos");
            }

            if (formularioListPendientes.isEmpty() && fileInspeccionListPendientes.isEmpty()) {
                showMessageDialog("No hay inspecciones almacenadas en el equipo para sincronizar.", () -> {
                });
                return;
            }

            String mensaje = "";
            if (!formularioListPendientes.isEmpty()) {
                mensaje = "¿Hay " + formularioListPendientes.size() +
                        (formularioListPendientes.size() > 0 ? " inspecciones pendientes por enviar" : " inspección pendiente por enviar") + ", deseas sincronizar ahora?";
            } else if (!fileInspeccionListPendientes.isEmpty()) {
                mensaje = "¿Hay" + fileInspeccionListPendientes.size() +
                        (fileInspeccionListPendientes.size() > 0 ? " fotos de inspecciones pendientes por enviar" : " foto de inspección pendiente por enviar") + ", deseas sincronizar ahora?";
            }

            dialogConfirmarRegistro = new MaterialDialog.Builder(MainActivity.this)
                    .title("Mensaje")
                    .content(mensaje)
                    .negativeText("No")
                    .positiveText("Si")
                    .positiveColorRes(R.color.colorAccent)
                    .negativeColorRes(R.color.colorText)
                    .onNegative((@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) -> dialogConfirmarRegistro.dismiss())
                    .onPositive((@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) -> {
                        dialogConfirmarRegistro.dismiss();
                        verificarInspeccionesPendientesEnvio(formularioListPendientes, fileInspeccionListPendientes);
                    })
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .build();
            dialogConfirmarRegistro.show();

        });
    }

    private void verificarInspeccionesPendientesEnvio(List<Formulario> formularioList, List<FileInspeccion> fileInspeccionList) {
        boolean isFormularioSync = false;
        for (Formulario item : formularioList) {
            if (!item.isSycnFormulario) {
                isFormularioSync = true;
                break;
            }
        }

        if (isFormularioSync) {
            enviarFormulariosPendientes(formularioList);
        } else {
            enviarFotosFirebase(formularioList, fileInspeccionList);
        }
    }

    private void enviarFormulariosPendientes(List<Formulario> formularioList) {
        progressDialog = new CustomDialog(MainActivity.this, getString(R.string.lbl_porfavor_espere),
                getString(R.string.lbl_enviando_formulario));
        progressDialog.showDialog();
        mDisposableGeneric.add(Observable
                .just(formularioList)
                .flatMap(listFiles -> Observable.fromIterable(listFiles))
                .flatMap(item -> modelView.registrarFormularioPendienteEnvio(item))
                .filter(formularioResource -> formularioResource.status.equals(Resource.Status.SUCCESS))
                .toList()
                .flatMap(dataResources -> {
                    List<Formulario> formularios = new ArrayList<>();
                    for (Resource<Formulario> item : dataResources) {
                        formularios.add(item.data);
                    }
                    return Single.fromCallable(() -> formularios);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateFormularios, err -> {
                    progressDialog.close();
                    MessageUtil.message(MainActivity.this, "Error al enviar las inspecciones registradas" + "\n" + " mensaje: " + err.getMessage());
                }));
    }

    private void updateFormularios(List<Formulario> formularioList) {
        mDisposableUpdateForm.add(modelView
                .updateBatchLocal(formularioList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listFormularios -> {
                    List<FileInspeccion> fileInspeccionList = new ArrayList<>();
                    for (Formulario item : listFormularios) {
                        for (FileInspeccion fileInspeccion : item.fileInspeccionList) {
                            fileInspeccion.setKey(item.key);
                            fileInspeccionList.add(fileInspeccion);
                        }
                    }
                    mDisposableUpdateForm.clear();
                    progressDialog.close();

                    enviarFotosFirebase(listFormularios, fileInspeccionList);
                }, err -> {
                    progressDialog.close();
                    MessageUtil.message(MainActivity.this, "Error al enviar las inspecciones registradas" + "\n" + " mensaje: " + err.getMessage());
                }));
    }

    private void enviarFotosFirebase(List<Formulario> formularioList, List<FileInspeccion> fileInspeccionList) {
        progressDialog = new CustomDialog(MainActivity.this, getString(R.string.lbl_porfavor_espere),
                getString(R.string.lbl_enviando_fotos_formulario));
        progressDialog.showDialog();
        mDisposableGeneric.add(Observable
                .just(fileInspeccionList)
                .flatMap(listFiles -> Observable.fromIterable(listFiles))
                .flatMap(item -> modelView.uploadFile(item))
                .filter(formularioResource -> formularioResource.status.equals(Resource.Status.SUCCESS))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileInspeccionResource -> {
                    Map<String, List<String>> mapFileFormuarios = new HashMap<>();
                    for (Resource<FileInspeccion> item : fileInspeccionResource) {
                        if (!mapFileFormuarios.containsKey(item.data.getKey())) {
                            List<String> mapFileInspeccion = new ArrayList<>();
                            mapFileInspeccion.add(item.data.getPathUpload());
                            mapFileFormuarios.put(item.data.getKey(), mapFileInspeccion);
                        } else {
                            mapFileFormuarios.get(item.data.getKey()).add(item.data.getPathUpload());
                        }
                    }

                    Map<String, List<String>> mapFormulariosFiltrados = new HashMap<>();
                    for (Formulario formulario : formularioList) {
                        if (mapFileFormuarios.containsKey(formulario.key)) {
                            if (formulario.fileInspeccionList.size() == mapFileFormuarios.get(formulario.key).size()) {
                                mapFormulariosFiltrados.put(formulario.key, mapFileFormuarios.get(formulario.key));
                            }
                        }
                    }

                    actualizarFotosFormularioFirebase(mapFormulariosFiltrados);
                }, err -> {
                    progressDialog.close();
                    MessageUtil.message(MainActivity.this, "Error al enviar las inspecciones registradas" + "\n" + " mensaje: " + err.getMessage());
                }));
    }

    private void actualizarFotosFormularioFirebase(Map<String, List<String>> mapFormulariosFiltrados) {
        mDisposableUpdateFormFotosFirebase.add(Observable
                .just(mapFormulariosFiltrados)
                .flatMap(listFiles -> Observable.fromIterable(listFiles.entrySet()))
                .flatMap(item -> modelView.updateFotosFormulario(item.getKey(), item.getValue()))
                .filter(formularioResource -> formularioResource.status.equals(Resource.Status.SUCCESS))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileInspeccionResource -> {
                    mDisposableUpdateFormFotosFirebase.clear();
                    updateFormulariosFotos(mapFormulariosFiltrados);
                }, err -> {
                    progressDialog.close();
                    MessageUtil.message(MainActivity.this, "Error al enviar inspecciones registradas" + "\n" + " mensaje: " + err.getMessage());
                }));
    }

    private void updateFormulariosFotos(Map<String, List<String>> mapFormulariosFiltrados) {
        mDisposableGeneric.add(modelView
                .updateBatchFotosLocal(mapFormulariosFiltrados)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    progressDialog.close();
                    showMessageDialog("Sincronización exitosa, se envió la información.", () -> {
                    });
                }, err -> {
                    progressDialog.close();
                    MessageUtil.message(MainActivity.this, "Error al enviar las inspecciones registradas" + "\n" + " mensaje: " + err.getMessage());
                }));
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
        mMasterSession.values.estadoInspeccionList = new ArrayList<>();
        mMasterSession.values.buzonInicioList = new ArrayList<>();
        mMasterSession.values.buzonFinList = new ArrayList<>();
        mMasterSession.values.tipoInspeccionList = new ArrayList<>();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposableGeneric.clear();
    }


}
