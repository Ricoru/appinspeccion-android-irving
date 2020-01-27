package me.buddyoruna.appinspeccion.domain.entity;

import androidx.annotation.Keep;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@Keep
@IgnoreExtraProperties
public class Formulario {

    @Exclude
    public int id;
    @Exclude
    public String key;
    public String uid; //userUID
    public double latitud;
    public double longitud;
    public TipoInspeccion tipoInspeccion;
    public String calle;
    public BuzonInicio buzInicio;
    public BuzonFin buzFin;
    public double longitudA;
    public String fechaHoraStr;
    public long fechaHora;
    public EstadoInspeccion estado;
    public double distanciaEntreBuzones;
    public String observaciones;
    public List<String> fotos;

    @Exclude
    public boolean isSycnFormulario;
    @Exclude
    public boolean isSycnFoto;

    @Exclude
    public List<FileInspeccion> fileInspeccionList;

}
