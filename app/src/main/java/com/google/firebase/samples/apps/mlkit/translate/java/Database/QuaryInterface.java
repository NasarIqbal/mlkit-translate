package com.google.firebase.samples.apps.mlkit.translate.java.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.firebase.samples.apps.mlkit.translate.java.model.History;

import java.util.List;

@Dao
public interface QuaryInterface {

    @Query("Select * from history")
    LiveData<List<History>> getAll();

    @Insert
    void addNewHistory(History... histories);

    @Delete
    void deleteHistory(History ... histories);
}
