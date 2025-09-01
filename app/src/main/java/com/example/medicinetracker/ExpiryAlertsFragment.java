package com.example.medicinetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

public class ExpiryAlertsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpiryAlertAdapter adapter;
    private MedicineViewModel medicineViewModel;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchShowAll;

    public ExpiryAlertsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expiry_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_expiry);
        FloatingActionButton fabAddReminder = view.findViewById(R.id.fab_add_expiry);
        switchShowAll = view.findViewById(R.id.switch_show_all); // make sure this ID exists in XML

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpiryAlertAdapter();
        recyclerView.setAdapter(adapter);

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        // Load default view - expiring in next 7 days
        loadExpiringSoonMedicines();

        // Handle switch toggle
        switchShowAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all expiry entries
                medicineViewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
                    if (medicines == null || medicines.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setMedicineList(medicines);
                    }
                });
            } else {
                // Show only expiring soon
                loadExpiringSoonMedicines();
            }
        });

        fabAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddExpiryActivity.class);
            startActivity(intent);
        });

        adapter.setOnExpiryItemClickListener(new ExpiryAlertAdapter.OnExpiryItemClickListener() {
            @Override
            public void onEdit(Medicine medicine) {
                Intent intent = new Intent(getContext(), AddExpiryActivity.class);
                intent.putExtra("edit_id", medicine.getId());
                intent.putExtra("name", medicine.getName());
                intent.putExtra("expiry", medicine.getExpiryDate().getTime());
                startActivity(intent);
            }

            @Override
            public void onDelete(Medicine medicine) {
                medicineViewModel.delete(medicine);
                Toast.makeText(getContext(), "Medicine deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpiringSoonMedicines() {
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        Date today = startCal.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.DAY_OF_YEAR, 7);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        Date next7Days = endCal.getTime();

        medicineViewModel.getMedicinesExpiringBetween(today, next7Days)
                .observe(getViewLifecycleOwner(), medicines -> {
                    if (medicines == null || medicines.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setMedicineList(medicines);
                    }
                });
    }
}
