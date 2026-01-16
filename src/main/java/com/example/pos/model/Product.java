package com.example.pos.model;

public class Product {
    private final String barcode;
    private final String name;
    private final double price;

    // NOWE POLE: Stan magazynowy
    private double stock;

    public Product(String barcode, String name, double price, double stock) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // Metody do zarządzania stanem
    public double getStock() { return stock; }

    public void decreaseStock(double amount) {
        this.stock -= amount;
    }

    public void increaseStock(double amount) {
        this.stock += amount;
    }

    // Gettery standardowe
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}