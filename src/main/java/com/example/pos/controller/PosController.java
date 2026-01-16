package com.example.pos.controller;

import com.example.pos.model.Product;
import com.example.pos.model.ReceiptItem;
import com.example.pos.service.PosService;
import com.example.pos.strategy.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PosController {

    private PosService posService;

    // --- ELEMENTY GUI ---
    @FXML private TextField barcodeField;
    @FXML private TextField quantityField;
    @FXML private TableView<ReceiptItem> cartTable;
    @FXML private TableColumn<ReceiptItem, String> colName;
    @FXML private TableColumn<ReceiptItem, Integer> colQty;
    @FXML private TableColumn<ReceiptItem, Double> colPrice;
    @FXML private TableColumn<ReceiptItem, Double> colTotal;
    @FXML private Label totalLabel;

    // Metody płatności
    @FXML private RadioButton rbCash;
    @FXML private RadioButton rbCard;
    @FXML private ToggleGroup paymentGroup;
    @FXML private ComboBox<String> discountBox;
    // --- LEWA STRONA (INFO) ---
    @FXML private TextArea productListArea;
    @FXML private TextArea discountInfoArea;

    // --- PRAWA STRONA (PARAGON I ZWROTY) ---
    @FXML private TextArea receiptArea;
    @FXML private TextField returnField;
    @FXML private TextField returnQuantityField;

    public void setPosService(PosService posService) {
        this.posService = posService;
        loadStaticInfo(); // Załaduj info o produktach i rabatach na starcie
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Formatowanie liczb (3 miejsca po przecinku dla wagi/ilości)
        colQty.setCellFactory(tc -> new TableCell<ReceiptItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : String.format("%.3f", item));
            }
        });

        // Formatowanie cen (2 miejsca po przecinku + waluta)
        colTotal.setCellFactory(tc -> new TableCell<ReceiptItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : String.format("%.2f zł", item));
            }
        });

        // --- 2. Konfiguracja Listy Rabatów (Tylko dodatki) ---
        discountBox.getItems().addAll(
                "Brak dodatkowego rabatu",
                "Senior (+5%)",
                "Happy Hour (+15%)"
        );
        discountBox.setValue("Brak dodatkowego rabatu");

        // --- 3. Stałe Info (Zielony Tekst) ---
        discountInfoArea.setText("ZASADY RABATÓW (KASKADOWE):\n" +
                "Zawsze aktywne są: VIP + 3za2.\n" +
                "Do nich dodajemy wybrany rabat dodatkowy.\n" +
                "Każdy kolejny rabat liczony jest od kwoty pomniejszonej o poprzednie.");
        discountInfoArea.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold; -fx-font-size: 11px;");
    }

    private void loadStaticInfo() {
        if (posService == null) return;
        StringBuilder products = new StringBuilder();
        for (Product p : posService.getAllProducts()) {
            // Dodano [Stan: X.X] do opisu
            products.append(String.format("%s (%s) - %.2f zł [Stan: %.1f]\n",
                    p.getName(), p.getBarcode(), p.getPrice(), p.getStock()));
        }
        productListArea.setText(products.toString());
    }
    @FXML
    public void handleDiscountChange() {
        if (posService == null) return;

        String selected = discountBox.getValue();

        // --- BUDOWANIE ŁAŃCUCHA DEKORATORÓW (Matrioszka) ---
        DiscountStrategy baseChain = new VipDiscountStrategy(
                new ThreeForTwoStrategy(
                        new NoDiscountStrategy()
                )
        );

        switch (selected) {
            case "Senior (+5%)":
                posService.setDiscountStrategy(new SeniorDiscountStrategy(baseChain));
                break;
            case "Happy Hour (+15%)":
                posService.setDiscountStrategy(new HappyHourStrategy(baseChain));
                break;
            case "Brak dodatkowego rabatu":
            default:
                posService.setDiscountStrategy(baseChain);
                break;
        }
    }

    @FXML
    public void handleScan() {
        if (posService == null) return;
        String barcode = barcodeField.getText();
        String qtyText = quantityField.getText();

        double amount = 1.0;
        try {
            if (!qtyText.isEmpty()) amount = Double.parseDouble(qtyText.replace(",", "."));
        } catch (NumberFormatException e) {
            showAlert("Błędna ilość! Wpisz liczbę (np. 1.5)");
            return;
        }

        // --- ZMIANA: Obsługa komunikatu tekstowego (walidacja magazynu) ---
        String result = posService.scanProduct(barcode, amount);

        if ("OK".equals(result)) {
            // Sukces
            refreshView();
            barcodeField.clear();
            quantityField.clear();
            barcodeField.requestFocus();
        } else {
            // Błąd (np. brak towaru na stanie)
            showAlert(result);
        }
    }

    @FXML
    public void handleCheckout() {
        if (posService == null || posService.getCart().isEmpty()) {
            showAlert("Koszyk jest pusty!");
            return;
        }

        handleDiscountChange();

        // Pobierz wybraną metodę płatności
        String method = rbCash.isSelected() ? "GOTÓWKA" : "KARTA";
        String receipt = posService.checkout(method);
        receiptArea.setText(receipt);
        refreshView();
        loadStaticInfo();
    }

    @FXML
    public void handleReturn() {
        String barcode = returnField.getText();
        String qtyText = returnQuantityField.getText();

        if (barcode.isEmpty()) { showAlert("Wpisz kod!"); return; }

        double amount = 1.0;
        try {
            if (!qtyText.isEmpty()) amount = Double.parseDouble(qtyText.replace(",", "."));
        } catch (NumberFormatException e) { showAlert("Błędna ilość!"); return; }

        String result = posService.returnProduct(barcode, amount);
        showAlert(result);

        // --- ZMIANA: Odświeżamy listę produktów (towar wrócił na półkę) ---
        loadStaticInfo();

        returnField.clear();
        returnQuantityField.clear();
    }

    private void refreshView() {
        cartTable.getItems().setAll(posService.getCart());
        double sum = posService.getCart().stream().mapToDouble(ReceiptItem::getTotal).sum();
        totalLabel.setText(String.format("SUMA: %.2f PLN", sum));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}