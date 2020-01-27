package me.buddyoruna.appinspeccion.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;
import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;
import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.Formulario;
import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;
import me.buddyoruna.appinspeccion.domain.response.Resource;

/**
 * Created by Ricoru on 17/01/18.
 */

public interface FormularioRepository {

    Single<Resource<String>> registrarLocal(Formulario formulario);

    Single<List<Formulario>> updateBatchLocal(List<Formulario> formularios);

    Completable updateBatchFotosLocal(Map<String, List<String>> mapFormulariosFiltrados);

    Completable eliminarFormularioSincronizados();

    LiveData<Resource<String>> registrarFirebase(Formulario formulario);

    Observable<Resource<Formulario>> registrarPendienteEnvio(Formulario fileInspeccion);

    LiveData<Map<String, Object>> obtenerPendientes();

    LiveData<Resource<List<TipoInspeccion>>> loadTipoInspeccion();

    LiveData<Resource<List<EstadoInspeccion>>> loadEstadoInspeccion();

    LiveData<Resource<List<BuzonInicio>>> loadBuzonInicio();

    LiveData<Resource<List<BuzonFin>>> loadBuzonFin();

    Observable<Resource<FileInspeccion>> loadFileInspeccion(FileInspeccion fileInspeccion);

    Observable<Resource<String>> updateFotosFormulario(String key, List<String> fotos);

}
