package com.example.medicinetracker;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class BarcodeCsvHelper {

    private static final String TAG = "BarcodeCsvHelper";
    private static HashMap<String, String> barcodeMap;

    // Load CSV from raw/barcodes.csv and store in map
    public static void loadBarcodeData(Context context) {
        barcodeMap = new HashMap<>();

        try {
            Resources res = context.getResources();
            InputStream inputStream = res.openRawResource(R.raw.barcodes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String barcode = tokens[0].trim();
                    String medicineName = tokens[1].trim();
                    barcodeMap.put(barcode, medicineName);
                }
            }

            reader.close();
            inputStream.close();
            Log.d(TAG, "CSV loaded: " + barcodeMap.size() + " entries");
        } catch (Exception e) {
            Log.e(TAG, "Error loading CSV: " + e.getMessage());
        }
    }

    // Get medicine name by barcode
    public static String getMedicineName(String barcode) {
        if (barcodeMap == null || barcodeMap.isEmpty()) {
            Log.w(TAG, "CSV data not loaded. Call loadBarcodeData(context) first.");
            return null;
        }
        return barcodeMap.get(barcode);
    }
}
