package me.buddyoruna.appinspeccion.domain.entity;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
public class EstadoInspeccion {

    public String key;

    public String nombre;

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }

}
