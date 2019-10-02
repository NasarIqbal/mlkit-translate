package com.google.firebase.samples.apps.mlkit.translate.java.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.google.firebase.samples.apps.mlkit.translate.java.model.History;

@androidx.room.Database(entities = {History.class},version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    private static LocalDatabase INSTANCE;
    public abstract QuaryInterface userDao();

    public static LocalDatabase getLocalDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, "history")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
