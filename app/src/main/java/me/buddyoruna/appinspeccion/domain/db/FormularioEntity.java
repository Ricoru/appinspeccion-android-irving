package me.buddyoruna.appinspeccion.domain.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import me.buddyoruna.appinspeccion.domain.convert.BuzonFinConverter;
import me.buddyoruna.appinspeccion.domain.convert.BuzonInicioConverter;
import me.buddyoruna.appinspeccion.domain.convert.EstadoInspeccionConverter;
import me.buddyoruna.appinspeccion.domain.convert.ListFileInspeccionConverter;
import me.buddyoruna.appinspeccion.domain.convert.ListFotosConverter;
import me.buddyoruna.appinspeccion.domain.convert.TipoInspeccionConverter;
import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;
import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;
import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;

@Entity(tableName = "m_inspeccion_formulario")
public class FormularioEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int m_id;

    @ColumnInfo(name = "key")
    public String key;

    @ColumnInfo(name = "uid")
    public String uid;

    @ColumnInfo(name = "latitud")
    public double latitud;

    @ColumnInfo(name = "longitud")
    public double longitud;

    @TypeConverters(TipoInspeccionConverter.class)
    @ColumnInfo(name = "tipo_inspeccion")
    public TipoInspeccion tipoInspeccion;

    @TypeConverters(EstadoInspeccionConverter.class)
    @ColumnInfo(name = "estado")
    public EstadoInspeccion estado;

    @ColumnInfo(name = "calle")
    public String calle;

    @TypeConverters(BuzonInicioConverter.class)
    @ColumnInfo(name = "buz_inicio")
    public BuzonInicio buzInicio;

    @TypeConverters(BuzonFinConverter.class)
    @ColumnInfo(name = "buz_fin")
    public BuzonFin buzFin;

    @ColumnInfo(name = "longituda")
    public double longitudA;

    @ColumnInfo(name = "fecha_hora_str")
    public String fechaHoraStr;

    @ColumnInfo(name = "fecha_hora")
    public long fechaHora;

    @ColumnInfo(name = "distancia_entre_buzones")
    public double distanciaEntreBuzones;

    @ColumnInfo(name = "observaciones")
    public String observaciones;

    @TypeConverters(ListFotosConverter.class)
    @ColumnInfo(name = "fotos")
    public List<String> fotos;

    @TypeConverters(ListFileInspeccionConverter.class)
    @ColumnInfo(name = "file_inspeccions")
    public List<FileInspeccion> fileInspeccions;

    @ColumnInfo(name = "is_sync_formulario")
    public boolean isSycnFormulario;

    @ColumnInfo(name = "is_sync_foto")
    public boolean isSycnFoto;

}
