package me.buddyoruna.appinspeccion.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Observable;
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

    LiveData<Resource<String>> registrar(Formulario formulario);

    LiveData<Resource<List<TipoInspeccion>>> loadTipoInspeccion();

    LiveData<Resource<List<EstadoInspeccion>>> loadEstadoInspeccion();

    LiveData<Resource<List<BuzonInicio>>> loadBuzonInicio();

    LiveData<Resource<List<BuzonFin>>> loadBuzonFin();

    Observable<Resource<FileInspeccion>> loadFileInspeccion(FileInspeccion fileInspeccion);

}
