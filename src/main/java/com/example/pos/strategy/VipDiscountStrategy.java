package com.example.pos.strategy;

public class VipDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double totalAmount) {
        // Jeśli zakupy powyżej 50 zł, daj 10% rabatu
        return totalAmount > 50.00 ? totalAmount * 0.10 : 0.0;
    }
}