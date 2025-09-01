package com.example.medicinetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "reminder_channel";
    private static final String CHANNEL_NAME = "Medicine Reminder";
    private static final String CHANNEL_DESC = "Reminders for medicine intake";

    public static void showReminderNotification(Context context, int reminderId, String medicineName) {
        createNotificationChannel(context);

        // Android 13+ requires runtime notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, exit early
            return;
        }

        // Action: Intake
        Intent intakeIntent = new Intent(context, IntakeReceiver.class);
        intakeIntent.putExtra("reminder_id", reminderId);
        PendingIntent intakePendingIntent = PendingIntent.getBroadcast(
                context, reminderId, intakeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Action: Not Intake
        Intent notIntakeIntent = new Intent(context, NotIntakeReceiver.class);
        notIntakeIntent.putExtra("reminder_id", reminderId);
        notIntakeIntent.putExtra("medicine_name", medicineName);
        PendingIntent notIntakePendingIntent = PendingIntent.getBroadcast(
                context, reminderId + 1000, notIntakeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_logo) // Transparent background icon (white)
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take: " + medicineName)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Reminder: Please take your medicine: " + medicineName))
                .setAutoCancel(false)
                .addAction(R.drawable.ic_check, "Intake", intakePendingIntent)
                .addAction(R.drawable.ic_close, "Not Intake", notIntakePendingIntent);

        NotificationManagerCompat.from(context).notify(reminderId, builder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}
