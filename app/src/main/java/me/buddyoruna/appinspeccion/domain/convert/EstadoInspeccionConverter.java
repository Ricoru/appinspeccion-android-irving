package me.buddyoruna.appinspeccion.domain.convert;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;

public class EstadoInspeccionConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static EstadoInspeccion to(String value) {
        if (value == null) {
            return null;
        }

        return gson.fromJson(value, EstadoInspeccion.class);
    }

    @TypeConverter
    public static String toString(EstadoInspeccion data) {
        return gson.toJson(data);
    }

}
