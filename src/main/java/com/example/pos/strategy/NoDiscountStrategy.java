package com.example.pos.strategy;

import com.example.pos.model.ReceiptItem;
import java.util.List;

public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double totalAmount, List<ReceiptItem> cart) {
        return 0.0;
    }

    @Override
    public String toString() {
        return ""; // Pusty string na koniec łańcucha
    }
}