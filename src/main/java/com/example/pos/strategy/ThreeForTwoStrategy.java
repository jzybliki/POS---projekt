package com.example.pos.strategy;

import com.example.pos.model.ReceiptItem;
import java.util.List;

public class ThreeForTwoStrategy implements DiscountStrategy {

    private final DiscountStrategy next;

    // KONSTRUKTOR
    public ThreeForTwoStrategy(DiscountStrategy next) {
        this.next = next;
    }

    @Override
    public double calculateDiscount(double totalAmount, List<ReceiptItem> cart) {
        double myDiscount = 0.0;
        for (ReceiptItem item : cart) {
            int freeItems = (int) (item.getQuantity() / 3);
            if (freeItems > 0) {
                myDiscount += freeItems * item.getPrice();
            }
        }
        // Kaskadowość
        return myDiscount + next.calculateDiscount(totalAmount, cart);
    }

    @Override
    public String toString() {
        String nextDesc = next.toString();
        return "3 za 2" + (nextDesc.isEmpty() ? "" : ", " + nextDesc);
    }
}