package com.example.medicinetracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ScanViewHolder> {

    private final Context context;
    private final List<ScanHistoryModel> historyList;

    public ScanHistoryAdapter(Context context, List<ScanHistoryModel> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_history, parent, false);
        return new ScanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanViewHolder holder, int position) {
        ScanHistoryModel model = historyList.get(position);

        holder.medicineName.setText(model.getMedicineName());
        holder.barcode.setText("Barcode: " + model.getBarcode());

        Glide.with(context)
                .load(model.getImageUrl())
                .placeholder(R.drawable.ic_medicine_placeholder) // replace with your placeholder image
                .into(holder.medicineImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MedicineDetailsActivity.class);
            intent.putExtra("barcode", model.getBarcode());
            intent.putExtra("medicineName", model.getMedicineName());
            intent.putExtra("price", model.getPrice());
            intent.putExtra("manufacturer", model.getManufacturer());
            intent.putExtra("brand", model.getBrand());
            intent.putExtra("uses", model.getUses());
            intent.putExtra("ingredients", model.getIngredients());
            intent.putExtra("sideEffects", model.getSideEffects());
            intent.putExtra("imageUrl", model.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ScanViewHolder extends RecyclerView.ViewHolder {
        ImageView medicineImage;
        TextView medicineName, barcode;

        public ScanViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.image_medicine);
            medicineName = itemView.findViewById(R.id.text_medicine_name);
            barcode = itemView.findViewById(R.id.text_barcode);
        }
    }
}
