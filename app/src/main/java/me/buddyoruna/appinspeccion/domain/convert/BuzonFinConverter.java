package me.buddyoruna.appinspeccion.domain.convert;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;

public class BuzonFinConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static BuzonFin to(String value) {
        if (value == null) {
            return null;
        }

        return gson.fromJson(value, BuzonFin.class);
    }

    @TypeConverter
    public static String toString(BuzonFin data) {
        return gson.toJson(data);
    }

}
