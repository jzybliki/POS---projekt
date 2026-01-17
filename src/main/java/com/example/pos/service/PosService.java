package com.example.pos.service;

import com.example.pos.model.Product;
import com.example.pos.model.ReceiptItem;
import com.example.pos.repository.ProductRepository;
import com.example.pos.strategy.DiscountStrategy;
import com.example.pos.utils.ReceiptPrinter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PosService {
    private final ProductRepository repository;
    private  DiscountStrategy discountStrategy;

    private final List<ReceiptItem> cart = new ArrayList<>();
    private final List<ReceiptItem> salesHistory = new ArrayList<>();

    public PosService(ProductRepository repository, DiscountStrategy discountStrategy) {
        this.repository = repository;
        this.discountStrategy = discountStrategy;
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    // Metoda pomocnicza do wyświetlania listy produktów w GUI
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public String scanProduct(String barcode, double amount) {
        Optional<Product> productOpt = repository.findByBarcode(barcode);
        if (productOpt.isPresent()) {
            Product p = productOpt.get();

            // 1. Sprawdzam, ile tego produktu już mamy w koszyku
            double currentQtyInCart = 0.0;
            ReceiptItem existingItem = null;

            for (ReceiptItem item : cart) {
                if (item.getProduct().getBarcode().equals(p.getBarcode())) {
                    currentQtyInCart = item.getQuantity();
                    existingItem = item;
                    break;
                }
            }
            // 2. Sprawdzam czy nie przekraczamy stanu
            if (currentQtyInCart + amount > p.getStock()) {
                return "BŁĄD: Za mało towaru! Na stanie: " + p.getStock() + ", w koszyku: " + currentQtyInCart;
            }

            // 3. Dodaj do koszyka
            if (existingItem != null) {
                existingItem.addQuantity(amount);
            } else {
                cart.add(new ReceiptItem(p, amount));
            }
            return "OK";
        }
        return "BŁĄD: Nie znaleziono produktu.";
    }

    public List<ReceiptItem> getCart() { return cart; }

    public String checkout(String paymentMethod) {
        if (cart.isEmpty()) return "Koszyk jest pusty!";

        double total = cart.stream().mapToDouble(ReceiptItem::getTotal).sum();
        double discount = discountStrategy.calculateDiscount(total, cart);
        double toPay = total - discount;

        StringBuilder sb = new StringBuilder();
        sb.append("--- SKLEP SPOŻYWCZY ---\n");
        sb.append("--- PARAGON FISKALNY ---\n\n");

        for (ReceiptItem item : cart) {
            sb.append(String.format("%-15s x%.3f  %.2f PLN\n",
                    item.getProductName(), item.getQuantity(), item.getTotal()));

            // --- ZMIANA: Aktualizacja stanu magazynowego (trwała) ---
            item.getProduct().decreaseStock(item.getQuantity());
        }

        sb.append("\n------------------------\n");
        sb.append(String.format("SUMA:          %.2f PLN\n", total));
        sb.append(String.format("RABAT:        -%.2f PLN\n", discount));
        sb.append(String.format("DO ZAPŁATY:    %.2f PLN\n", toPay));
        sb.append("------------------------\n");
        sb.append("PŁATNOŚĆ: " + paymentMethod + "\n");
        sb.append("Dziękujemy za zakupy!");

        String receiptContent = sb.toString();

        Thread printerThread = new Thread(new ReceiptPrinter(receiptContent));
        printerThread.start();

        salesHistory.addAll(new ArrayList<>(cart));
        cart.clear();
        return receiptContent;
    }

    public String returnProduct(String barcode, double amountToReturn) {
        Iterator<ReceiptItem> iterator = salesHistory.iterator();
        while (iterator.hasNext()) {
            ReceiptItem soldItem = iterator.next();
            if (soldItem.getProduct().getBarcode().equals(barcode)) {

                if (amountToReturn > soldItem.getQuantity() + 0.001) {
                    return String.format("BŁĄD: Próbujesz zwrócić %.3f, a kupiono tylko %.3f!", amountToReturn, soldItem.getQuantity());
                }

                double refundAmount = soldItem.getPrice() * amountToReturn;
                soldItem.addQuantity(-amountToReturn);

                // --- ZMIANA: Towar wraca na półkę (zwiększamy stan) ---
                soldItem.getProduct().increaseStock(amountToReturn);

                if (soldItem.getQuantity() <= 0.001) {
                    iterator.remove();
                }
                return "ZWRÓCONO: " + soldItem.getProductName() +
                        "\nIlość: " + String.format("%.3f", amountToReturn) +
                        "\nKwota: " + String.format("%.2f", refundAmount) + " PLN";
            }
        }
        return "BŁĄD: Nie można zwrócić produktu (brak w historii).";
    }
}