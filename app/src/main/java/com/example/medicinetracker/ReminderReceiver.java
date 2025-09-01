package com.example.medicinetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("reminder_id", -1);
        String medicineName = intent.getStringExtra("medicine_name");

        if (reminderId != -1 && medicineName != null) {
            Log.d("ReminderReceiver", "Reminder triggered for ID: " + reminderId);
            NotificationHelper.showReminderNotification(context, reminderId, medicineName);
        }
    }
}
