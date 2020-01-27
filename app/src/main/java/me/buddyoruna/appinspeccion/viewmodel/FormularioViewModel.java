package me.buddyoruna.appinspeccion.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import me.buddyoruna.appinspeccion.domain.db.AppDatabase;
import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;
import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;
import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.Formulario;
import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;
import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.repository.FormularioRepository;
import me.buddyoruna.appinspeccion.repository.FormularioRepositoryImpl;
import me.buddyoruna.appinspeccion.ui.util.DateUtil;

public class FormularioViewModel extends AndroidViewModel {

    private final FormularioRepository repository;
    private MasterSession mMasterSession;

    public FormularioViewModel(@NonNull Application application, AppDatabase appDatabase) {
        super(application);
        this.mMasterSession = MasterSession.getInstance(application);
        this.repository = new FormularioRepositoryImpl(appDatabase);
    }

    public LiveData<Resource<String>> registroFirebase(TipoInspeccion tipoInspeccion, String calle,
                                                BuzonInicio buzInicio, BuzonFin buzFin, double longitudA, EstadoInspeccion estado,
                                                double distanciaEntreBuzones, String observaciones) {

        Formulario formulario = new Formulario();
        formulario.latitud = mMasterSession.values.currentMyPositionLatitude;
        formulario.longitud = mMasterSession.values.currentMyPositionLongitude;
        formulario.tipoInspeccion = tipoInspeccion;
        formulario.calle = calle;
        formulario.buzInicio = buzInicio;
        formulario.buzFin = buzFin;
        formulario.longitudA = longitudA;
        formulario.fechaHoraStr = DateUtil.getDateNowFormat("dd/MM/yyyy HH:mm");
        formulario.fechaHora = DateUtil.getDateNow().getTime();
        formulario.estado = estado;
        formulario.distanciaEntreBuzones = distanciaEntreBuzones;
        formulario.observaciones = observaciones;
        //Guardamos esto para cuando se trabaje en modo offline
        formulario.fileInspeccionList = mMasterSession.values.fileListFormDynamic;

        List<String> files = new ArrayList<>();
        for (FileInspeccion item : mMasterSession.values.fileListFormDynamic) {
            files.add(item.getPathUpload());
        }
        formulario.fotos = files;

        return repository.registrarFirebase(formulario);
    }

    public Single<Resource<String>> registroLocal(TipoInspeccion tipoInspeccion, String calle,
                                                  BuzonInicio buzInicio, BuzonFin buzFin, double longitudA, EstadoInspeccion estado,
                                                  double distanciaEntreBuzones, String observaciones) {

        Formulario formulario = new Formulario();
        formulario.latitud = mMasterSession.values.currentMyPositionLatitude;
        formulario.longitud = mMasterSession.values.currentMyPositionLongitude;
        formulario.tipoInspeccion = tipoInspeccion;
        formulario.calle = calle;
        formulario.buzInicio = buzInicio;
        formulario.buzFin = buzFin;
        formulario.longitudA = longitudA;
        formulario.fechaHoraStr = DateUtil.getDateNowFormat("dd/MM/yyyy HH:mm");
        formulario.fechaHora = DateUtil.getDateNow().getTime();
        formulario.estado = estado;
        formulario.distanciaEntreBuzones = distanciaEntreBuzones;
        formulario.observaciones = observaciones;
        //Guardamos esto para cuando se trabaje en modo offline
        formulario.fileInspeccionList = mMasterSession.values.fileListFormDynamic;
//        List<String> files = new ArrayList<>();
//        for (FileInspeccion item : mMasterSession.values.fileListFormDynamic) {
//            files.add(item.getPathUpload());
//        }
        formulario.fotos = new ArrayList<>();

        return repository.registrarLocal(formulario);
    }

    public Single<List<Formulario>> updateBatchLocal(List<Formulario> formularioList) {
        return repository.updateBatchLocal(formularioList);
    }

    public Completable updateBatchFotosLocal(Map<String, List<String>> mapFormulariosFiltrados) {
        return repository.updateBatchFotosLocal(mapFormulariosFiltrados);
    }

    public Observable<Resource<Formulario>> registrarFormularioPendienteEnvio(Formulario formulario)  {
        return repository.registrarPendienteEnvio(formulario);
    }

    public LiveData<Map<String, Object>> obtenerFormulariosPendientes() {
        return repository.obtenerPendientes();
    }

    public LiveData<Resource<List<TipoInspeccion>>> getTipoInspeccion() {
        return repository.loadTipoInspeccion();
    }

    public LiveData<Resource<List<EstadoInspeccion>>> getEstadoInspeccion() {
        return repository.loadEstadoInspeccion();
    }

    public LiveData<Resource<List<BuzonInicio>>> getBuzonInicio() {
        return repository.loadBuzonInicio();
    }

    public LiveData<Resource<List<BuzonFin>>> getBuzonFin() {
        return repository.loadBuzonFin();
    }

    public Observable<Resource<FileInspeccion>> uploadFile(FileInspeccion fileInspeccion) {
        return repository.loadFileInspeccion(fileInspeccion);
    }

    public Observable<Resource<String>> updateFotosFormulario(String key, List<String> fotos) {
        return repository.updateFotosFormulario(key, fotos);
    }

    public Completable eliminarFormulariosSincronizados(){
        return repository.eliminarFormularioSincronizados();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        @NonNull
        private final AppDatabase mDatabase;

        public Factory(@NonNull Application application) {
            mApplication = application;
            mDatabase = AppDatabase.getInstance(application);
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new FormularioViewModel(mApplication, mDatabase);
        }
    }

}
