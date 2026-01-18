package com.example.pos;

import com.example.pos.model.Product;
import com.example.pos.model.ReceiptItem;
import com.example.pos.repository.ProductRepository;
import com.example.pos.service.PosService;
import com.example.pos.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PosSystemTest {

    private ProductRepository repository;
    private PosService service;

    @BeforeEach
    void setUp() {
        // Przed każdym testem tworzymy czystą instancję serwisu
        repository = new ProductRepository();

        // Domyślna strategia: VIP -> 3za2 -> Koniec
        DiscountStrategy strategy = new VipDiscountStrategy(
                new ThreeForTwoStrategy(
                        new NoDiscountStrategy()
                )
        );
        service = new PosService(repository, strategy);
    }

    // --- TEST 1: CZY DZIAŁA BLOKADA MAGAZYNU? ---
    @Test
    void shouldBlockAddingProductIfNotEnoughStock() {
        // Mamy w bazie Mleko (111), które ma 50 sztuk na stanie
        String barcode = "111";

        // Próba kupienia 51 sztuk
        String result = service.scanProduct(barcode, 51.0);

        // Oczekujemy błędu
        assertTrue(result.startsWith("BŁĄD"), "Powinien być błąd braku towaru");
        assertEquals(0, service.getCart().size(), "Koszyk powinien być pusty");
    }

    @Test
    void shouldAllowAddingProductIfStockIsEnough() {
        String barcode = "111"; // Mleko (stan 50)

        // Kupujemy 10 sztuk
        String result = service.scanProduct(barcode, 10.0);

        assertEquals("OK", result);
        assertEquals(1, service.getCart().size());
        assertEquals(10.0, service.getCart().get(0).getQuantity());
    }

    // --- TEST 2: CZY RABATY KASKADOWE DOBRZE LICZĄ? ---
    @Test
    void testCascadeDiscountCalculation() {
        // Symulujemy koszyk: 3 produkty po 100 zł
        Product drogiProdukt = new Product("TEST", "Test", 100.0, 1000);
        List<ReceiptItem> cart = new ArrayList<>();
        cart.add(new ReceiptItem(drogiProdukt, 3.0));
        // Suma koszyka: 300 zł

        double totalAmount = 300.0;

        // Tworzymy kaskadę: VIP -> 3za2 -> NoDiscount
        DiscountStrategy strategy = new VipDiscountStrategy(
                new ThreeForTwoStrategy(
                        new NoDiscountStrategy()
                )
        );

        // OBLICZENIA RĘCZNE:
        // 1. "3 za 2": Odejmuje 1 produkt (100 zł). Zostaje 200 zł podstawy.
        // 2. "VIP": Suma 300 > 50, więc liczy 10% od POZOSTAŁYCH 200 zł = 20 zł.
        // Łączny rabat powinien wynosić: 100 + 20 = 120 zł.

        double discount = strategy.calculateDiscount(totalAmount, cart);

        assertEquals(120.0, discount, 0.01, "Rabat kaskadowy źle policzony!");
    }

    // --- TEST 3: CZY ZWROT TOWARU ZWIĘKSZA STAN MAGAZYNU? ---
    @Test
    void testReturnProductIncreasesStock() {
        // Mleko (111) ma na starcie 50 sztuk
        Product mleko = repository.findByBarcode("111").get();
        double startStock = mleko.getStock(); // 50.0

        // 1. Kupujemy 5 sztuk
        service.scanProduct("111", 5.0);
        service.checkout("GOTÓWKA"); // To zmniejsza stan o 5 -> Stan: 45.0

        assertEquals(startStock - 5.0, mleko.getStock(), "Stan nie zmalał po zakupie");

        // 2. Zwracamy 2 sztuki
        service.returnProduct("111", 2.0); // To powinno zwiększyć stan o 2 -> Stan: 47.0

        assertEquals(startStock - 3.0, mleko.getStock(), "Stan nie wzrósł po zwrocie");
    }
}