package com.example.medicinetracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface MedicineDao {

    // Insert new medicine
    @Insert
    void insert(Medicine medicine);

    // Update existing medicine
    @Update
    void update(Medicine medicine);

    // Delete a medicine
    @Delete
    void delete(Medicine medicine);

    // Get all medicines
    @Query("SELECT * FROM medicines ORDER BY expiry_date ASC")
    LiveData<List<Medicine>> getAllMedicines();

    // Get all medicines (synchronous) - for alarms
    @Query("SELECT * FROM medicines ORDER BY expiry_date ASC")
    List<Medicine> getAllMedicinesSync();

    // Get medicines that are expired or expiring within a date range
    @Query("SELECT * FROM medicines WHERE expiry_date BETWEEN :from AND :to ORDER BY expiry_date ASC")
    LiveData<List<Medicine>> getMedicinesExpiringBetween(Date from, Date to);

    // Get a single medicine by ID
    @Query("SELECT * FROM medicines WHERE id = :id")
    Medicine getMedicineById(int id);
}
