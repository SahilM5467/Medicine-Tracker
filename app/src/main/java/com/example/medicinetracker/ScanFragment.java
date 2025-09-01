package com.example.medicinetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class ScanFragment extends Fragment {

    private Button btnScan;
    private EditText searchBar;
    private RecyclerView recyclerView;
    private ScanHistoryAdapter adapter;

    private List<ScanHistoryModel> fullList = new ArrayList<>();
    private ScanHistoryViewModel viewModel;

    public ScanFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        btnScan = view.findViewById(R.id.btnScan);
        searchBar = view.findViewById(R.id.searchBar);
        recyclerView = view.findViewById(R.id.scannedRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScanHistoryAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Setup ViewModel to observe Room DB
        viewModel = new ViewModelProvider(this).get(ScanHistoryViewModel.class);
        viewModel.getAllHistory().observe(getViewLifecycleOwner(), historyList -> {
            fullList.clear();
            for (ScanHistoryEntity entity : historyList) {
                fullList.add(new ScanHistoryModel(
                        entity.barcode,
                        entity.medicineName,
                        entity.price,
                        entity.brand,
                        entity.manufacturer,
                        entity.uses,
                        entity.ingredients,
                        entity.sideEffects,
                        entity.imageUrl
                ));
            }
            filterList(searchBar.getText().toString());
        });

        btnScan.setOnClickListener(v -> openBarcodeScanner());

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void openBarcodeScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Scan Medicine Barcode");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedBarcode = result.getContents();

                // Start LoadingActivity with scanned barcode
                Intent intent = new Intent(getContext(), LoadingActivity.class);
                intent.putExtra("barcodeNumber", scannedBarcode);
                startActivity(intent);

                // Save temporary placeholder entry into Room
                ScanHistoryEntity entity = new ScanHistoryEntity();
                entity.barcode = scannedBarcode;
                entity.medicineName = "Loading...";
                entity.price = "";
                entity.brand = "";
                entity.manufacturer = "";
                entity.uses = "";
                entity.ingredients = "";
                entity.sideEffects = "";
                entity.imageUrl = "";

                viewModel.insert(entity);

            } else {
                Toast.makeText(getContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void filterList(String keyword) {
        List<ScanHistoryModel> filtered = new ArrayList<>();
        for (ScanHistoryModel model : fullList) {
            if (model.getMedicineName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(model);
            }
        }
        adapter = new ScanHistoryAdapter(getContext(), filtered);
        recyclerView.setAdapter(adapter);
    }
}
