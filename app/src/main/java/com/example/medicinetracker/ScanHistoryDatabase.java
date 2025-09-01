package com.example.medicinetracker;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ScanHistoryEntity.class}, version = 1, exportSchema = false)
public abstract class ScanHistoryDatabase extends RoomDatabase {

    private static ScanHistoryDatabase instance;

    public abstract ScanHistoryDao scanHistoryDao();

    public static synchronized ScanHistoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ScanHistoryDatabase.class,
                            "scan_history_db"
                    ).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
