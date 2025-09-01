package com.example.medicinetracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

public class ReminderViewModel extends AndroidViewModel {

    private final ReminderDao reminderDao;
    private final LiveData<List<Reminder>> allReminders;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        ReminderDatabase db = ReminderDatabase.getInstance(application);
        reminderDao = db.reminderDao();
        allReminders = reminderDao.getAllReminders();
    }

    public void insert(Reminder reminder) {
        Executors.newSingleThreadExecutor().execute(() -> reminderDao.insert(reminder));
    }

    public void update(Reminder reminder) {
        Executors.newSingleThreadExecutor().execute(() -> reminderDao.update(reminder));
    }

    public void delete(Reminder reminder) {
        Executors.newSingleThreadExecutor().execute(() -> reminderDao.delete(reminder));
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return allReminders;
    }
}
