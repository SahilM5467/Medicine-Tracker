package com.example.medicinetracker;

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

public class ExpiryAlertAdapter extends RecyclerView.Adapter<ExpiryAlertAdapter.ExpiryViewHolder> {

    private List<Medicine> medicineList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public interface OnExpiryItemClickListener {
        void onEdit(Medicine medicine);
        void onDelete(Medicine medicine);
    }

    private OnExpiryItemClickListener listener;

    public void setOnExpiryItemClickListener(OnExpiryItemClickListener listener) {
        this.listener = listener;
    }

    public void setMedicineList(List<Medicine> list) {
        this.medicineList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpiryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expiry_alert, parent, false);
        return new ExpiryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpiryViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.medicineName.setText(medicine.getName());

        Date expiryDate = medicine.getExpiryDate();
        holder.expiryDate.setText("Expires on: " + dateFormat.format(expiryDate));

        Date today = new Date();
        if (expiryDate.before(today)) {
            holder.expiryStatus.setText("Expired");
            holder.expiryStatus.setBackgroundResource(R.drawable.bg_status_red);
        } else {
            holder.expiryStatus.setText("Expiring Soon");
            holder.expiryStatus.setBackgroundResource(R.drawable.bg_status_orange);
        }

        holder.optionsMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.optionsMenu);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.item_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    if (item.getItemId() == R.id.menu_edit) {
                        listener.onEdit(medicine);
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete) {
                        listener.onDelete(medicine);
                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    static class ExpiryViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, expiryDate, expiryStatus;
        ImageView optionsMenu;

        public ExpiryViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.tv_medicine_name);
            expiryDate = itemView.findViewById(R.id.tv_expiry_date);
            expiryStatus = itemView.findViewById(R.id.tv_expiry_status);
            optionsMenu = itemView.findViewById(R.id.image_View_Options);
        }
    }
}
