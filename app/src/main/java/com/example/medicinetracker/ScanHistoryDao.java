package com.example.medicinetracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScanHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScanHistoryEntity entity);

    @Query("SELECT * FROM scan_history_table ORDER BY medicineName ASC")
    LiveData<List<ScanHistoryEntity>> getAllHistory();

    @Query("DELETE FROM scan_history_table")
    void clearAll();
}
