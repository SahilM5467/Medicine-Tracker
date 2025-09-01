package com.example.medicinetracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanHistoryViewModel extends AndroidViewModel {

    private final ScanHistoryDao dao;
    private final LiveData<List<ScanHistoryEntity>> allHistory;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScanHistoryViewModel(@NonNull Application application) {
        super(application);
        ScanHistoryDatabase db = ScanHistoryDatabase.getInstance(application);
        dao = db.scanHistoryDao();
        allHistory = dao.getAllHistory();
    }

    public LiveData<List<ScanHistoryEntity>> getAllHistory() {
        return allHistory;
    }

    public void insert(ScanHistoryEntity entity) {
        executor.execute(() -> dao.insert(entity));
    }

    public void clearHistory() {
        executor.execute(dao::clearAll);
    }
}
