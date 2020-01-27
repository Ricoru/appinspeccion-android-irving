package me.buddyoruna.appinspeccion.domain.convert;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;

public class TipoInspeccionConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static TipoInspeccion to(String value) {
        if (value == null) {
            return null;
        }

        return gson.fromJson(value, TipoInspeccion.class);
    }

    @TypeConverter
    public static String toString(TipoInspeccion data) {
        return gson.toJson(data);
    }

}
