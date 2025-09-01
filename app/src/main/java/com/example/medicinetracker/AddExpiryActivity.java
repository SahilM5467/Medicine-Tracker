package com.example.medicinetracker;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpiryActivity extends AppCompatActivity {

    private EditText editTextName;
    private Button btnPickDate, btnSave;
    private TextView tvSelectedDate;

    private Date selectedDate = null;
    private MedicineViewModel medicineViewModel;

    private int editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expiry);

        editTextName = findViewById(R.id.edit_text_name);
        btnPickDate = findViewById(R.id.btn_pick_date);
        btnSave = findViewById(R.id.btn_save);
        tvSelectedDate = findViewById(R.id.tv_selected_date);

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        // Check if this is edit mode
        Intent intent = getIntent();
        if (intent.hasExtra("edit_id")) {
            editId = intent.getIntExtra("edit_id", -1);
            String name = intent.getStringExtra("name");
            long expiryMillis = intent.getLongExtra("expiry", -1);

            if (editId != -1 && expiryMillis != -1) {
                editTextName.setText(name);
                selectedDate = new Date(expiryMillis);
                tvSelectedDate.setText("Selected: " + formatDate(selectedDate));
            }
        }

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveMedicine());

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
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = calendar.getTime();
                    tvSelectedDate.setText("Selected: " + formatDate(selectedDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void saveMedicine() {
        String name = editTextName.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Enter medicine name");
            return;
        }
        if (selectedDate == null) {
            Toast.makeText(this, "Please select an expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

        Medicine medicine = new Medicine(name, selectedDate);

        if (editId != -1) {
            medicine.setId(editId);
            medicineViewModel.update(medicine);
            Toast.makeText(this, "Medicine updated", Toast.LENGTH_SHORT).show();
        } else {
            medicineViewModel.insert(medicine);
            Toast.makeText(this, "Medicine added", Toast.LENGTH_SHORT).show();
        }

        // Schedule notifications
        scheduleExpiryAlarms(medicine.getName(), medicine.getExpiryDate(), medicine.getId());

        finish();
    }

    private void scheduleExpiryAlarms(String medicineName, Date expiryDate, int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 7 days before
        Calendar cal7 = Calendar.getInstance();
        cal7.setTime(expiryDate);
        cal7.add(Calendar.DAY_OF_YEAR, -7);

        Intent soonIntent = new Intent(this, ExpirySoonReceiver.class);
        soonIntent.putExtra("medicine_name", medicineName);

        PendingIntent soonPending = PendingIntent.getBroadcast(
                this, id, soonIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal7.getTimeInMillis(), soonPending);

        // On expiry day
        Intent todayIntent = new Intent(this, ExpiredTodayReceiver.class);
        todayIntent.putExtra("medicine_name", medicineName);

        PendingIntent todayPending = PendingIntent.getBroadcast(
                this, id + 10000, todayIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, expiryDate.getTime(), todayPending);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
