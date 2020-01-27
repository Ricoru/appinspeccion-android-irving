package me.buddyoruna.appinspeccion.domain.convert;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;

public class BuzonInicioConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static BuzonInicio to(String value) {
        if (value == null) {
            return null;
        }

        return gson.fromJson(value, BuzonInicio.class);
    }

    @TypeConverter
    public static String toString(BuzonInicio data) {
        return gson.toJson(data);
    }

}
