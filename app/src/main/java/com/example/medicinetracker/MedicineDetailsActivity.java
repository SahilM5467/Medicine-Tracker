package com.example.medicinetracker;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MedicineDetailsActivity extends AppCompatActivity {

    private TextView medicineNameText, brandText, priceText,
            usesText, ingredientsText, sideEffectsText, manufacturerText;
    private ImageView medicineImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);

        // Initialize views
        medicineImage = findViewById(R.id.medicineImage);
        medicineNameText = findViewById(R.id.medicineNameText);
        brandText = findViewById(R.id.brandText);
        priceText = findViewById(R.id.priceText);
        usesText = findViewById(R.id.usesText);
        ingredientsText = findViewById(R.id.ingredientsText);
        sideEffectsText = findViewById(R.id.sideEffectsText);
        manufacturerText = findViewById(R.id.manufacturerText);

        // Get Intent data
        String name = getIntent().getStringExtra("medicineName");
        String brand = getIntent().getStringExtra("brand");
        String price = getIntent().getStringExtra("price");
        String uses = getIntent().getStringExtra("uses");
        String ingredients = getIntent().getStringExtra("ingredients");
        String sideEffects = getIntent().getStringExtra("sideEffects");
        String manufacturer = getIntent().getStringExtra("manufacturer");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Set TextViews
        medicineNameText.setText(name != null ? name : "N/A");
        brandText.setText("Brand: " + (brand != null ? brand : "N/A"));
        priceText.setText("Price: " + (price != null ? price : "N/A"));
        usesText.setText("Uses: " + (uses != null ? uses : "N/A"));
        ingredientsText.setText("Ingredients: " + (ingredients != null ? ingredients : "N/A"));
        sideEffectsText.setText("Side Effects: " + (sideEffects != null ? sideEffects : "N/A"));
        manufacturerText.setText("Manufacturer: " + (manufacturer != null ? manufacturer : "N/A"));

        // Load image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_medicine_placeholder)
                    .into(medicineImage);
        } else {
            medicineImage.setImageResource(R.drawable.ic_medicine_placeholder);
        }
    }
}
