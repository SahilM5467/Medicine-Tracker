package com.example.medicinetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ExpiredTodayReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");

        if (medicineName != null) {
            String title = "Medicine Expired";
            String message = "Today is the expiry date for: " + medicineName;
            int notificationId = (int) System.currentTimeMillis(); // unique ID
            NotificationUtils.showNotification(context, title, message, notificationId);
        }
    }
}
