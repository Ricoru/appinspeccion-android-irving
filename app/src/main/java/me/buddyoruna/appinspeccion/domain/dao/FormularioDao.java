package me.buddyoruna.appinspeccion.domain.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import me.buddyoruna.appinspeccion.domain.db.FormularioEntity;

@Dao
public interface FormularioDao extends BaseDao<FormularioEntity> {

    @Query("DELETE FROM m_inspeccion_formulario where is_sync_formulario=1 and is_sync_foto=1")
    void eliminarFormularioMigrados();

    @Query("SELECT * FROM m_inspeccion_formulario where is_sync_formulario=0")
    LiveData<List<FormularioEntity>> getFormulariosPendientes();

    @Query("SELECT * FROM m_inspeccion_formulario where `key`=:key ")
    FormularioEntity findKey(String key);

    @Query("SELECT m_id FROM m_inspeccion_formulario where `key`=:key ")
    int getIdByKey(String key);

}
