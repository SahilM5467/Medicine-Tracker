package com.example.medicinetracker;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_history_table")
public class ScanHistoryEntity {

    @PrimaryKey
    @NonNull
    public String barcode;

    public String medicineName;
    public String price;
    public String brand;
    public String manufacturer;
    public String uses;
    public String ingredients;
    public String sideEffects;
    public String imageUrl;

    // Optional constructor (Room uses reflection)
    public ScanHistoryEntity() {}
}
