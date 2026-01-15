package com.example.pos;

import com.example.pos.model.ReceiptItem;
import com.example.pos.repository.ProductRepository;
import com.example.pos.service.PosService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PosTest {

    @Test
    void testScanProduct() {
        // Przygotowanie (Arrange)
        ProductRepository repo = new ProductRepository();
        PosService service = new PosService(repo, new VipDiscountStrategy());

        // Działanie (Act)
        ReceiptItem item = service.scanProduct("111");

        // Sprawdzenie (Assert)
        assertNotNull(item, "Produkt powinien zostać znaleziony");
        assertEquals("Mleko", item.getProductName());
        assertEquals(3.50, item.getPrice());
    }

    @Test
    void testDiscountLogic() {
        // Sprawdzenie czy rabat działa powyżej 50 zł
        VipDiscountStrategy strategy = new VipDiscountStrategy();

        double discountLow = strategy.calculateDiscount(40.00);
        assertEquals(0.0, discountLow, "Brak rabatu poniżej 50zł");

        double discountHigh = strategy.calculateDiscount(100.00);
        assertEquals(10.0, discountHigh, "Rabat 10% powyżej 50zł");
    }
}