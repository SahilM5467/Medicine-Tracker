package com.example.medicinetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class NotIntakeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("reminder_id", -1);
        String medicineName = intent.getStringExtra("medicine_name");

        if (reminderId != -1 && medicineName != null) {
            NotificationManagerCompat.from(context).cancel(reminderId);

            // Reschedule after 5 minutes
            long rescheduleTime = System.currentTimeMillis() + 5 * 60 * 1000;

            AlarmUtils.scheduleReminder(context, reminderId, medicineName, rescheduleTime);
        }
    }
}
