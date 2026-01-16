package com.example.pos.model;

public class ReceiptItem {
    private final Product product;
    private double quantity;

    public ReceiptItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getTotal() {
        return product.getPrice() * quantity;
    }

    public void incrementQuantity(double amount) {
        this.quantity += amount;
    }

    // Gettery potrzebne dla tabeli w JavaFX
    public Product getProduct() { return product; }
    public String getProductName() { return product.getName(); }
    public double getQuantity() { return quantity; }
    public double getPrice() { return product.getPrice(); }
}