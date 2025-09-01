package com.example.medicinetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class IntakeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("reminder_id", -1);
        if (reminderId != -1) {
            NotificationManagerCompat.from(context).cancel(reminderId);
            // You can optionally save intake history here
        }
    }
}
