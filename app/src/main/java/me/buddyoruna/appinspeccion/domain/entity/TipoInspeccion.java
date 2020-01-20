package me.buddyoruna.appinspeccion.domain.entity;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
public class TipoInspeccion {

    @Exclude
    public String key;

    public String descripcion;

    @NonNull
    @Override
    public String toString() {
        return descripcion;
    }

}
