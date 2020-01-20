package me.buddyoruna.appinspeccion.domain.entity;

import androidx.annotation.Keep;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@Keep
@IgnoreExtraProperties
public class Formulario {

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

}
