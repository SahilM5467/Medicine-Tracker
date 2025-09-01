package com.example.medicinetracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    private EditText editTextMedicineName, editTextDosage;
    private TextView textViewDate, textViewTime;
    private Button buttonSave;
    private Spinner repeatspinner;

    private Calendar selectedDateTime;
    private ReminderViewModel reminderViewModel;

    private boolean isEditMode = false;
    private int existingReminderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reminder);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        editTextMedicineName = findViewById(R.id.editTextMedicineName);
        editTextDosage = findViewById(R.id.editTextDosage);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        repeatspinner = findViewById(R.id.spinner_repeat);
        buttonSave = findViewById(R.id.buttonSave);

        selectedDateTime = Calendar.getInstance();
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        // Handle Edit Mode
        if (getIntent().hasExtra("reminder_id")) {
            isEditMode = true;
            existingReminderId = getIntent().getIntExtra("reminder_id", -1);
            String name = getIntent().getStringExtra("medicine_name");
            String dosage = getIntent().getStringExtra("dosage");
            long time = getIntent().getLongExtra("time", System.currentTimeMillis());
            String repeat = getIntent().getStringExtra("repeat");

            selectedDateTime.setTimeInMillis(time);
            editTextMedicineName.setText(name);
            editTextDosage.setText(dosage);
            textViewDate.setText(DateFormat.getDateInstance().format(selectedDateTime.getTime()));
            textViewTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedDateTime.getTime()));
            setSpinnerSelection(repeatspinner, repeat);
        }

        textViewDate.setOnClickListener(v -> showDatePicker());
        textViewTime.setOnClickListener(v -> showTimePicker());
        buttonSave.setOnClickListener(v -> saveReminder());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    textViewDate.setText(DateFormat.getDateInstance().format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    selectedDateTime.set(Calendar.SECOND, 0);
                    textViewTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    private void saveReminder() {
        String name = editTextMedicineName.getText().toString().trim();
        String dosage = editTextDosage.getText().toString().trim();
        String repeatType = repeatspinner.getSelectedItem().toString();
        long timeInMillis = selectedDateTime.getTimeInMillis();

        if (name.isEmpty() || dosage.isEmpty() || timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder = new Reminder();
        reminder.setMedicineName(name);
        reminder.setDosage(dosage);
        reminder.setReminderTime(timeInMillis);
        reminder.setIsActive(true);

        if (isEditMode) {
            reminder.setId(existingReminderId);
            reminderViewModel.update(reminder);
            Toast.makeText(this, "Reminder updated", Toast.LENGTH_SHORT).show();
        } else {
            reminder.setId((int) System.currentTimeMillis());
            reminderViewModel.insert(reminder);
            Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
        }

        // Schedule or reschedule notification
        AlarmUtils.scheduleReminder(this, reminder.getId(), name, timeInMillis);

        finish();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
