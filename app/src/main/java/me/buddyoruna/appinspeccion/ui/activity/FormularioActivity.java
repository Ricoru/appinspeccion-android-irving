package me.buddyoruna.appinspeccion.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.buddyoruna.appinspeccion.R;
import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;
import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;
import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;
import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.ui.fragment.ContentFotosFragment;
import me.buddyoruna.appinspeccion.ui.util.CustomDialog;
import me.buddyoruna.appinspeccion.ui.util.MessageUtil;
import me.buddyoruna.appinspeccion.viewmodel.FormularioViewModel;

public class FormularioActivity extends BaseActivity {

    @BindView(R.id.sp_tipo_inspeccion)
    Spinner sp_tipo_inspeccion;
    @BindView(R.id.sp_estado)
    Spinner sp_estado;
    @BindView(R.id.txt_direccion)
    EditText txt_direccion;
    @BindView(R.id.sp_buzon_inicio)
    Spinner sp_buzon_inicio;
    @BindView(R.id.sp_buzon_fin)
    Spinner sp_buzon_fin;
    @BindView(R.id.txt_longitud)
    EditText txt_longitud;
    @BindView(R.id.txt_distancia_buzones)
    EditText txt_distancia_buzones;
    @BindView(R.id.txt_observacion)
    EditText txt_observacion;
    @BindView(R.id.container)
    LinearLayout view;

    FormularioViewModel modelView;
    CustomDialog progressDialog;
    ContentFotosFragment fragmentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        FormularioViewModel.Factory formularioViewModelFactory = new FormularioViewModel.Factory(getApplication());
        modelView = ViewModelProviders.of(this, formularioViewModelFactory).get(FormularioViewModel.class);

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentContent = new ContentFotosFragment();
//            fragmentTransaction.remove(fragmentContent);
//            fragmentTransaction.commitAllowingStateLoss();

            fragmentTransaction.replace(R.id.content_fotos, fragmentContent);
            fragmentTransaction.commitAllowingStateLoss();

            modelView.getTipoInspeccion().observe(this, resource -> {
                if (resource.status.equals(Resource.Status.ERROR)) {
                    Snackbar.make(view, resource.message, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();
                    return;
                }

                ArrayAdapter<TipoInspeccion> tipoInspeccionArrayAdapter =
                        new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, resource.data) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView tv = (TextView) super.getView(position, convertView, parent);
                                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                                return tv;
                            }
                        };
                sp_tipo_inspeccion.setAdapter(tipoInspeccionArrayAdapter);
            });

            modelView.getEstadoInspeccion().observe(this, resource -> {
                if (resource.status.equals(Resource.Status.ERROR)) {
                    Snackbar.make(view, resource.message, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();
                    return;
                }

                ArrayAdapter<EstadoInspeccion> estadoInspeccionArrayAdapter =
                        new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, resource.data) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView tv = (TextView) super.getView(position, convertView, parent);
                                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                                return tv;
                            }
                        };
                sp_estado.setAdapter(estadoInspeccionArrayAdapter);
            });

            modelView.getBuzonInicio().observe(this, resource -> {
                if (resource.status.equals(Resource.Status.ERROR)) {
                    Snackbar.make(view, resource.message, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();
                    return;
                }

                ArrayAdapter<BuzonInicio> buzzonInicioArrayAdapter =
                        new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, resource.data) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView tv = (TextView) super.getView(position, convertView, parent);
                                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                                return tv;
                            }
                        };
                sp_buzon_inicio.setAdapter(buzzonInicioArrayAdapter);
            });

            modelView.getBuzonFin().observe(this, resource -> {
                if (resource.status.equals(Resource.Status.ERROR)) {
                    Snackbar.make(view, resource.message, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();
                    return;
                }

                ArrayAdapter<BuzonFin> buzzonFinArrayAdapter =
                        new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, resource.data) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView tv = (TextView) super.getView(position, convertView, parent);
                                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                                return tv;
                            }
                        };
                sp_buzon_fin.setAdapter(buzzonFinArrayAdapter);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.img_action_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.img_action_done)
    public void done() {
        double longitud = TextUtils.isEmpty(txt_longitud.getText().toString()) ? 0 : Double.parseDouble(txt_longitud.getText().toString());
        double distance = TextUtils.isEmpty(txt_distancia_buzones.getText().toString()) ? 0 : Double.parseDouble(txt_distancia_buzones.getText().toString());

        TipoInspeccion tipoInspeccion = (TipoInspeccion) sp_tipo_inspeccion.getSelectedItem();
        EstadoInspeccion estadoInspeccion = (EstadoInspeccion) sp_estado.getSelectedItem();
        BuzonInicio buzonInicio = (BuzonInicio) sp_buzon_inicio.getSelectedItem();
        BuzonFin buzonFin = (BuzonFin) sp_buzon_fin.getSelectedItem();

        if (tipoInspeccion == null) {
            MessageUtil.message(FormularioActivity.this, "Es necesario seleccionar un Tipo de Inspección");
        }

        if (estadoInspeccion == null) {
            MessageUtil.message(FormularioActivity.this, "Es necesario seleccionar un Estado de Inspección");
        }

        if (buzonInicio == null) {
            MessageUtil.message(FormularioActivity.this, "Es necesario seleccionar un Estado de Inspección");
        }

        if (buzonFin == null) {
            MessageUtil.message(FormularioActivity.this, "Es necesario seleccionar un Estado de Inspección");
        }

        progressDialog = new CustomDialog(FormularioActivity.this, getString(R.string.lbl_porfavor_espere),
                getString(R.string.lbl_enviando_formulario));
        progressDialog.showDialog();
        if (!mMasterSession.values.fileListFormDynamic.isEmpty()) {
            Observable
                    .just(mMasterSession.values.fileListFormDynamic)
                    .flatMap(listFiles -> {
                        List<FileInspeccion> fileInspeccionList = new ArrayList<>();
                        for (FileInspeccion item : listFiles) {
                            if (!item.isUpload()) {
                                fileInspeccionList.add(item);
                            }
                        }
                        return Observable.fromIterable(fileInspeccionList);
                    })
                    .flatMap(item -> modelView.uploadFile(item))
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(listFiles -> {
                        Log.i("INFO", "listaFiles subidos " + new Gson().toJson(listFiles));
                        updateUploadFiles(listFiles);
                        if (validateFilesSubidos()) {
                            registrarFormulario(tipoInspeccion, txt_direccion.getText().toString(), buzonInicio, buzonFin, longitud,
                                    estadoInspeccion, distance, txt_observacion.getText().toString());
                        } else {
                            progressDialog.close();
                            MessageUtil.message(FormularioActivity.this, "Error al intentar subir los archivos al servidor, volver a intentarlo");
                        }
                    }, err -> {
                        progressDialog.close();
                        MessageUtil.message(FormularioActivity.this, "Error al gurdar datos del Formulario, " + "\n" + " mensaje: " + err.getMessage());
                    });
        } else {
            registrarFormulario(tipoInspeccion, txt_direccion.getText().toString(), buzonInicio, buzonFin, longitud,
                    estadoInspeccion, distance, txt_observacion.getText().toString());
        }
    }

    private void registrarFormulario(TipoInspeccion tipoInspeccion, String calle,
                                     BuzonInicio buzonInicio, BuzonFin buzonFin, double longitud, EstadoInspeccion estadoInspeccion,
                                     double distance, String observaciones) {
        modelView.registrar(tipoInspeccion, calle, buzonInicio, buzonFin, longitud, estadoInspeccion, distance, observaciones)
                .observe(this, resource -> {
                    progressDialog.close();
                    if (resource.status.equals(Resource.Status.ERROR)) {
                        MessageUtil.message(FormularioActivity.this, "Error al gurdar datos del Formulario, " + "\n" + " mensaje: " + resource.message);
                        return;
                    }

                    limpiarDatos();
                    MessageUtil.message(FormularioActivity.this, resource.data);
                });
    }

    private void updateUploadFiles(List<Resource<FileInspeccion>> list) {
        for (Resource<FileInspeccion> resource : list) {
            if (!Resource.Status.ERROR.equals(resource.status)) {
                int j = 0;
                for (FileInspeccion inspeccion : mMasterSession.values.fileListFormDynamic) {
                    if (inspeccion.getFile().equals(resource.data.getFile())) {
                        mMasterSession.values.fileListFormDynamic.get(j).setUpload(resource.data.isUpload());
                        mMasterSession.values.fileListFormDynamic.get(j).setFile(resource.data.getFile());
                        mMasterSession.values.fileListFormDynamic.get(j).setPathUpload(resource.data.getPathUpload());
                        mMasterSession.update();
                        break;
                    }
                    j++;
                }
            }
        }
    }

    private boolean validateFilesSubidos() {
        for (FileInspeccion fileInspeccion : mMasterSession.values.fileListFormDynamic) {
            if (!fileInspeccion.isUpload()) {
                return false;
            }
        }
        return true;
    }

    private void limpiarDatos() {
        sp_tipo_inspeccion.setSelection(0);
        sp_estado.setSelection(0);
        sp_buzon_inicio.setSelection(0);
        sp_buzon_fin.setSelection(0);
        txt_direccion.setText("");
        txt_longitud.setText("");
        txt_distancia_buzones.setText("");
        txt_observacion.setText("");

        fragmentContent.removeImagenesFlexBox();

        mMasterSession.values.fileListFormDynamic = new ArrayList<>();
        mMasterSession.update();

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        ContentFotosFragment fragment = new ContentFotosFragment();
//        fragmentTransaction.remove(fragment);
//        fragmentTransaction.commitAllowingStateLoss();
//
//        fragmentTransaction.add(R.id.content_fotos, fragment);
//        fragmentTransaction.commitAllowingStateLoss();
    }

}
