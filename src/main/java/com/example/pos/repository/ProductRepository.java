package com.example.pos.repository;

import com.example.pos.model.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductRepository {
    private final Map<String, Product> database = new HashMap<>();

    public ProductRepository() {
        // Kod, Nazwa, Cena, ILOŚĆ NA STANIE
        database.put("111", new Product("111", "Mleko 2%", 3.50, 50));     // 50 sztuk
        database.put("222", new Product("222", "Chleb Wiejski", 4.20, 20)); // 20 sztuk
        database.put("333", new Product("333", "Masło Extra", 7.99, 30));
        database.put("444", new Product("444", "Wódka 0.5L", 39.99, 100));
        database.put("555", new Product("555", "Chipsy Solone", 6.50, 40));

        // Napoje
        database.put("666", new Product("666", "Woda Niegaz.", 1.99, 200));
        database.put("777", new Product("777", "Sok Pomarańcza", 5.50, 30));
        database.put("888", new Product("888", "Coca-Cola 1L", 6.99, 60));
        database.put("999", new Product("999", "Piwo Jasne", 3.80, 150));

        // Produkty na wagę (np. 10.5 kg na stanie)
        database.put("101", new Product("101", "Banany (kg)", 6.99, 15.5));
        database.put("102", new Product("102", "Jabłka (kg)", 3.49, 50.0));

        database.put("201", new Product("201", "Czekolada", 4.50, 25));
    }

    public Optional<Product> findByBarcode(String barcode) {
        return Optional.ofNullable(database.get(barcode));
    }

    public List<Product> findAll() {
        return new ArrayList<>(database.values());
    }
}