package com.example.medicinetracker;

public class ScanHistoryModel {
    private final String barcode;
    private final String medicineName;
    private final String price;
    private final String brand;
    private final String manufacturer;
    private final String uses;
    private final String ingredients;
    private final String sideEffects;
    private final String imageUrl;

    public ScanHistoryModel(String barcode, String medicineName, String price, String brand,
                            String manufacturer, String uses, String ingredients,
                            String sideEffects, String imageUrl) {
        this.barcode = barcode;
        this.medicineName = medicineName;
        this.price = price;
        this.brand = brand;
        this.manufacturer = manufacturer;
        this.uses = uses;
        this.ingredients = ingredients;
        this.sideEffects = sideEffects;
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getUses() {
        return uses;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
