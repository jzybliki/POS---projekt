package com.example.pos.model;

public class Product {
    // Chronione atrybuty (private)
    private final String barcode;
    private final String name;
    private final double price;
    private double stock;

    public Product(String barcode, String name, double price, double stock) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    public void decreaseStock(double amount) {
        this.stock -= amount;
    }

    public void increaseStock(double amount) {
        this.stock += amount;
    }

    public double getStock() { return stock; }
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}