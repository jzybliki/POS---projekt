package com.example.pos.model;

public class Product {
    // Chronione atrybuty (private)
    private String barcode;
    private String name;
    private double price;

    public Product(String barcode, String name, double price) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
    }

    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() { return name; }
}