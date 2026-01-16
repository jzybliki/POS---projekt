package com.example.pos.strategy;

import com.example.pos.model.ReceiptItem;
import java.util.List;

public class HappyHourStrategy implements DiscountStrategy {

    private final DiscountStrategy next;

    // KONSTRUKTOR
    public HappyHourStrategy(DiscountStrategy next) {
        this.next = next;
    }

    @Override
    public double calculateDiscount(double totalAmount, List<ReceiptItem> cart) {
        double prevDiscount = next.calculateDiscount(totalAmount, cart);

        double baseForMe = totalAmount - prevDiscount;
        double myDiscount = baseForMe * 0.15; // 15%

        return prevDiscount + myDiscount;
    }

    @Override
    public String toString() {
        String nextDesc = next.toString();
        return "Happy Hour 15%" + (nextDesc.isEmpty() ? "" : ", " + nextDesc);
    }
}