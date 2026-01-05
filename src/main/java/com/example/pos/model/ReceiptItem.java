package com.example.pos.model;

public class ReceiptItem {
    private final Product product;
    private int quantity;

    public ReceiptItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getTotal() {
        return product.getPrice() * quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    // Gettery potrzebne dla tabeli w JavaFX
    public Product getProduct() { return product; }
    public String getProductName() { return product.getName(); }
    public int getQuantity() { return quantity; }
    public double getPrice() { return product.getPrice(); }
}