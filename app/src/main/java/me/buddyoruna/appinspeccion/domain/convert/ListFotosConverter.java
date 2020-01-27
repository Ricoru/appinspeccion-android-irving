package me.buddyoruna.appinspeccion.domain.convert;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListFotosConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String toString(List<String> data) {
        return gson.toJson(data);
    }

}
