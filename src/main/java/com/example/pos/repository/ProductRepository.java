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
        database.put("111", new Product("111", "Mleko", 3.50));
        database.put("222", new Product("222", "Chleb", 4.20));
        database.put("333", new Product("333", "Masło", 7.99));
        database.put("444", new Product("444", "Wódka", 39.99));
        database.put("555", new Product("555", "Chipsy", 6.50));
    }

    public Optional<Product> findByBarcode(String barcode) {
        return Optional.ofNullable(database.get(barcode));
    }

    // NOWA METODA: Zwraca listę wszystkich produktów
    public List<Product> findAll() {
        return new ArrayList<>(database.values());
    }
}