package com.example.medicinetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RemindersFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ReminderAdapter adapter;
    private ReminderViewModel viewModel;

    public RemindersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_reminders);
        fab = view.findViewById(R.id.fab_add_reminder);

        viewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        adapter = new ReminderAdapter(requireContext(), viewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getAllReminders().observe(getViewLifecycleOwner(), reminders -> {
            adapter.setReminders(reminders);
        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddReminderActivity.class);
            startActivity(intent);
        });
    }
}