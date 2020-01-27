package me.buddyoruna.appinspeccion.domain.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

public interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T obj);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertBatch(List<T> lista);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(T obj);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAll(List<T> lista);

    @Delete
    void delete(T obj);

}
