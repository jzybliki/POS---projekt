package com.example.pos;

// PONIŻEJ SĄ POPRAWIONE IMPORTY
import com.example.pos.controller.PosController;
import com.example.pos.repository.ProductRepository;
import com.example.pos.service.PosService; // <--- To jest kluczowe! Wskazuje na folder service
import com.example.pos.strategy.DiscountStrategy;
import com.example.pos.strategy.VipDiscountStrategy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PosApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // --- 1. Ręczne Wstrzykiwanie Zależności ---

        // Tworzymy bazę danych
        ProductRepository repository = new ProductRepository();

        // Tworzymy strategię rabatową
        DiscountStrategy strategy = new VipDiscountStrategy();

        // Tworzymy Serwis i dajemy mu bazę oraz strategię
        // Teraz Java wie, że chodzi o PosService z folderu 'service'
        PosService service = new PosService(repository, strategy);

        // --- 2. Ładowanie Widoku FXML ---
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pos_view.fxml"));
        Parent root = loader.load();

        // --- 3. Konfiguracja Kontrolera ---
        // Pobieramy kontroler stworzony przez JavaFX
        PosController controller = loader.getController();
        // Przekazujemy mu nasz gotowy serwis
        controller.setPosService(service);

        // --- 4. Wyświetlenie Okna ---
        primaryStage.setTitle("System POS (Zaliczenie)");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}