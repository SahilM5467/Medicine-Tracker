package com.example.medicinetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<Reminder> reminderList = new ArrayList<>();
    private Context context;
    private ReminderViewModel reminderViewModel;

    public ReminderAdapter(@NonNull Context context, @NonNull ReminderViewModel reminderViewModel) {
        this.context = context;
        this.reminderViewModel = reminderViewModel;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dosage, time;
        ImageView options;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_medicine_name);
            dosage = itemView.findViewById(R.id.text_dosage);
            time = itemView.findViewById(R.id.text_time);
            options = itemView.findViewById(R.id.image_View_Options);
        }
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminderList = reminders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.name.setText(reminder.getMedicineName());
        holder.dosage.setText(reminder.getDosage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        holder.time.setText(sdf.format(new Date(reminder.getReminderTime())));

        holder.options.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.options);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.item_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit) {
                    Intent intent = new Intent(context, AddReminderActivity.class);
                    intent.putExtra("reminder_id", reminder.getId());
                    intent.putExtra("medicine_name", reminder.getMedicineName());
                    intent.putExtra("dosage", reminder.getDosage());
                    intent.putExtra("time", reminder.getReminderTime());
                    context.startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Reminder")
                            .setMessage("Are you sure you want to delete this reminder?")
                            .setPositiveButton("Yes", (dialog, which) -> reminderViewModel.delete(reminder))
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }
}
