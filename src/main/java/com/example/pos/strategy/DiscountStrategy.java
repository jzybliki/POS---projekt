package com.example.pos.strategy;

import com.example.pos.model.ReceiptItem;

import java.util.List;

public interface DiscountStrategy {
    double calculateDiscount(double totalAmount, List<ReceiptItem> cart);
}