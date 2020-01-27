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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

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
import me.buddyoruna.appinspeccion.ui.util.NetworkUtil;
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
    FragmentTransaction fragmentTransaction;
    ContentFotosFragment fragmentContent;

    CustomDialog progressDialog;
    MaterialDialog dialogConfirmarRegistro;

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
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentContent = new ContentFotosFragment();

            fragmentTransaction.replace(R.id.content_fotos, fragmentContent);
            fragmentTransaction.commitAllowingStateLoss();

            if (NetworkUtil.isNetworkAvailable(FormularioActivity.this)) {
                modelView.getTipoInspeccion().observe(this, resource -> {
                    if (resource.status.equals(Resource.Status.ERROR)) {
                        Snackbar.make(view, resource.message, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .show();
                        return;
                    }

                    ArrayAdapter<TipoInspeccion> tipoInspeccionArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, resource.data) {
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
            } else {
                ArrayAdapter<TipoInspeccion> tipoInspeccionArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                        mMasterSession.values.tipoInspeccionList) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView tv = (TextView) super.getView(position, convertView, parent);
                        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                        return tv;
                    }
                };
                sp_tipo_inspeccion.setAdapter(tipoInspeccionArrayAdapter);

                ArrayAdapter<EstadoInspeccion> estadoInspeccionArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                        mMasterSession.values.estadoInspeccionList) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView tv = (TextView) super.getView(position, convertView, parent);
                        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                        return tv;
                    }
                };
                sp_estado.setAdapter(estadoInspeccionArrayAdapter);

                ArrayAdapter<BuzonInicio> buzzonInicioArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                        mMasterSession.values.buzonInicioList) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView tv = (TextView) super.getView(position, convertView, parent);
                        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                        return tv;
                    }
                };
                sp_buzon_inicio.setAdapter(buzzonInicioArrayAdapter);

                ArrayAdapter<BuzonInicio> buzzonFinArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                        mMasterSession.values.buzonFinList) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView tv = (TextView) super.getView(position, convertView, parent);
                        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                        return tv;
                    }
                };
                sp_buzon_fin.setAdapter(buzzonFinArrayAdapter);
            }
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

        dialogConfirmarRegistro = new MaterialDialog.Builder(FormularioActivity.this)
                .title("Mensaje")
                .content("¿Estás seguro de guardar los datos registrados?")
                .negativeText(R.string.action_cancelar)
                .positiveText(R.string.action_aceptar)
                .positiveColorRes(R.color.colorAccent)
                .negativeColorRes(R.color.colorText)
                .onNegative((@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) -> {
                    dialogConfirmarRegistro.dismiss();
                })
                .onPositive((@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) -> {
                    dialogConfirmarRegistro.dismiss();
                    procesarRegistro(tipoInspeccion, txt_direccion.getText().toString(), buzonInicio, buzonFin, longitud,
                            estadoInspeccion, distance, txt_observacion.getText().toString());
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .build();
        dialogConfirmarRegistro.show();
    }

    private void procesarRegistro(TipoInspeccion tipoInspeccion, String calle,
                                  BuzonInicio buzonInicio, BuzonFin buzonFin, double longitud, EstadoInspeccion estadoInspeccion,
                                  double distance, String observaciones) {
        progressDialog = new CustomDialog(FormularioActivity.this, getString(R.string.lbl_porfavor_espere),
                getString(R.string.lbl_enviando_formulario));
        progressDialog.showDialog();

        if (!NetworkUtil.isNetworkAvailable(FormularioActivity.this)) {
            registrarFormularioLocal(tipoInspeccion, calle, buzonInicio, buzonFin, longitud,
                    estadoInspeccion, distance, observaciones);
            return;
        }

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
                        updateUploadFiles(listFiles);
                        if (validateFilesSubidos()) {
                            registrarFormularioFirebase(tipoInspeccion, calle, buzonInicio, buzonFin, longitud,
                                    estadoInspeccion, distance, observaciones);
                        } else {
                            progressDialog.close();
                            MessageUtil.message(FormularioActivity.this, "Error al intentar subir los archivos al servidor, volver a intentarlo");
                        }
                    }, err -> {
                        progressDialog.close();
                        MessageUtil.message(FormularioActivity.this, "Error al gurdar datos del Formulario, " + "\n" + " mensaje: " + err.getMessage());
                    });
        } else {
            registrarFormularioFirebase(tipoInspeccion, calle, buzonInicio, buzonFin, longitud,
                    estadoInspeccion, distance, observaciones);
        }
    }

    private void registrarFormularioFirebase(TipoInspeccion tipoInspeccion, String calle,
                                             BuzonInicio buzonInicio, BuzonFin buzonFin, double longitud, EstadoInspeccion estadoInspeccion,
                                             double distance, String observaciones) {
        modelView.registroFirebase(tipoInspeccion, calle, buzonInicio, buzonFin, longitud, estadoInspeccion, distance, observaciones)
                .observe(this, resource -> {
                    progressDialog.close();
                    if (resource.status.equals(Resource.Status.ERROR)) {
                        MessageUtil.message(FormularioActivity.this, "Error al gurdar datos del Formulario, " + "\n" + " mensaje: " + resource.message);
                        return;
                    }

                    showSuccessDialog("Registro Exitoso", resource.data, () -> limpiarDatos());
                });
    }

    private void registrarFormularioLocal(TipoInspeccion tipoInspeccion, String calle,
                                          BuzonInicio buzonInicio, BuzonFin buzonFin, double longitud, EstadoInspeccion estadoInspeccion,
                                          double distance, String observaciones) {
        modelView.registroLocal(tipoInspeccion, calle, buzonInicio, buzonFin, longitud, estadoInspeccion, distance, observaciones)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resource -> {
                    progressDialog.close();

                    if (resource.status.equals(Resource.Status.ERROR)) {
                        MessageUtil.message(FormularioActivity.this, "Error al gurdar datos del Formulario, " + "\n" + " mensaje: " + resource.message);
                        return;
                    }

                    showSuccessDialog("Registro Exitoso", resource.data, () -> limpiarDatos());
                }, err -> {
                    progressDialog.close();
                    MessageUtil.message(FormularioActivity.this, "Error al gurdar datos del Formulario, " + "\n" + " mensaje: " + err.getMessage());
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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
