package com.example.medicinetracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicineViewModel extends AndroidViewModel {

    private final MedicineDao medicineDao;
    private final ExecutorService executorService;

    public MedicineViewModel(@NonNull Application application) {
        super(application);
        MedicineDatabase db = MedicineDatabase.getInstance(application);
        medicineDao = db.medicineDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert medicine
    public void insert(Medicine medicine) {
        executorService.execute(() -> medicineDao.insert(medicine));
    }

    // Update medicine
    public void update(Medicine medicine) {
        executorService.execute(() -> medicineDao.update(medicine));
    }

    // Delete medicine
    public void delete(Medicine medicine) {
        executorService.execute(() -> medicineDao.delete(medicine));
    }

    // Get all medicines
    public LiveData<List<Medicine>> getAllMedicines() {
        return medicineDao.getAllMedicines();
    }

    // Get all medicines between 2 dates
    public LiveData<List<Medicine>> getMedicinesExpiringBetween(Date from, Date to) {
        return medicineDao.getMedicinesExpiringBetween(from, to);
    }

    // Get single medicine by ID (synchronous, for editing)
    public Medicine getMedicineById(int id) {
        return medicineDao.getMedicineById(id);
    }

    // Get all medicines synchronously (for alarm managers/workers)
    public List<Medicine> getAllMedicinesSync() {
        return medicineDao.getAllMedicinesSync();
    }
}
