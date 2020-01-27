package me.buddyoruna.appinspeccion.domain.db;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import me.buddyoruna.appinspeccion.domain.dao.FormularioDao;

@Database(entities = {FormularioEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    @VisibleForTesting
    public static final String DATABASE_NAME = "inspeccion-db";

    private static AppDatabase sInstance;
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public abstract FormularioDao formularioDao();

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabasePro(context.getApplicationContext());
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /* Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabasePro(final Context appContext) {
        AppDatabase database = Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .build();
        database.setDatabaseCreated();
        return database;
    }

    //TODO este metodo es solo para desarrollo
    private static AppDatabase buildDatabaseDev(final Context appContext) {
        AppDatabase database = Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration().build();
        database.setDatabaseCreated();
        return database;
    }

    //Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

}