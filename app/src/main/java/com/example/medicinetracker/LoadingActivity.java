package com.example.medicinetracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URLEncoder;

public class LoadingActivity extends AppCompatActivity {

    private ImageView loadingAnimation;
    private ImageView noInternetImage, noDataGif;
    private Button retryButton;

    private String scannedBarcode;
    private ScanHistoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loadingAnimation = findViewById(R.id.loadingAnimation);
        noInternetImage = findViewById(R.id.noInternetImage);
        noDataGif = findViewById(R.id.noDataGif);
        retryButton = findViewById(R.id.retryButton);

        scannedBarcode = getIntent().getStringExtra("barcodeNumber");

        viewModel = new ViewModelProvider(this).get(ScanHistoryViewModel.class);
        BarcodeCsvHelper.loadBarcodeData(this);

        retryButton.setOnClickListener(v -> startProcessing());
        startProcessing();
    }

    private void startProcessing() {
        showLoadingState();

        new Handler().postDelayed(() -> {
            if (!isNetworkAvailable()) {
                showNoInternetState();
                return;
            }

            if (scannedBarcode == null || scannedBarcode.isEmpty()) {
                showNoDataState();
                return;
            }

            String medicineName = BarcodeCsvHelper.getMedicineName(scannedBarcode);
            if (medicineName == null) {
                showNoDataState();
                return;
            }

            fetchMedicineDetails(medicineName);

        }, 1000);
    }

    private void fetchMedicineDetails(String medicineName) {
        new Thread(() -> {
            try {
                String searchUrl = "https://www.1mg.com/search/all?name=" + URLEncoder.encode(medicineName, "UTF-8");
                Document searchDoc = Jsoup.connect(searchUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                Elements productLinks = searchDoc.select("a[href^=/drugs/]");

                if (productLinks.isEmpty()) {
                    runOnUiThread(this::showNoDataState);
                    return;
                }

                String productUrl = "https://www.1mg.com" + productLinks.first().attr("href");

                Document doc = Jsoup.connect(productUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                String title = doc.selectFirst("h1") != null ? doc.selectFirst("h1").text() : "N/A";
                String price = doc.select("div:contains(₹)").first() != null ? doc.select("div:contains(₹)").first().text() : "N/A";
                String imageUrl = doc.selectFirst("img[src^=https]") != null ? doc.selectFirst("img[src^=https]").attr("src") : "";

                Elements metaValues = doc.select("div[class*=DrugHeader__meta-value]");
                String brand = metaValues.size() > 0 ? metaValues.get(0).text() : "N/A";
                String manufacturer = metaValues.size() > 1 ? metaValues.get(1).text() : "N/A";

                String uses = doc.select("div[class*=DrugOverview]").text();
                String ingredients = doc.select("div[class*=Composition__content]").text();
                String sideEffects = doc.select("div[class*=side-effects] > div").text();

                ScanHistoryEntity entity = new ScanHistoryEntity();
                entity.barcode = scannedBarcode;
                entity.medicineName = title;
                entity.price = price;
                entity.manufacturer = manufacturer;
                entity.brand = brand;
                entity.uses = uses;
                entity.ingredients = ingredients;
                entity.sideEffects = sideEffects;
                entity.imageUrl = imageUrl;

                viewModel.insert(entity);

                Intent intent = new Intent(LoadingActivity.this, MedicineDetailsActivity.class);
                intent.putExtra("barcode", scannedBarcode);
                intent.putExtra("medicineName", title);
                intent.putExtra("price", price);
                intent.putExtra("manufacturer", manufacturer);
                intent.putExtra("brand", brand);
                intent.putExtra("uses", uses);
                intent.putExtra("ingredients", ingredients);
                intent.putExtra("sideEffects", sideEffects);
                intent.putExtra("imageUrl", imageUrl);

                runOnUiThread(() -> {
                    loadingAnimation.setVisibility(View.GONE);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(this::showNoDataState);
                e.printStackTrace();
            }
        }).start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private void showLoadingState() {
        Glide.with(this)
                .asGif()
                .load(R.drawable.loading_animation) // your GIF file in res/drawable
                .into(loadingAnimation);

        loadingAnimation.setVisibility(View.VISIBLE);
        noInternetImage.setVisibility(View.GONE);
        noDataGif.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
    }

    private void showNoInternetState() {
        loadingAnimation.setVisibility(View.GONE);
        noInternetImage.setVisibility(View.VISIBLE);
        noDataGif.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    private void showNoDataState() {
        Glide.with(this)
                .asGif()
                .load(R.drawable.no_data_animation) // your GIF file in res/drawable
                .into(noDataGif);

        loadingAnimation.setVisibility(View.GONE);
        noInternetImage.setVisibility(View.GONE);
        noDataGif.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
    }
}
