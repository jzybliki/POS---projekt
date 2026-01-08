package com.example.pos.controller;

import com.example.pos.model.Product;
import com.example.pos.model.ReceiptItem;
import com.example.pos.service.PosService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PosController {

    private PosService posService;

    // --- ŚRODEK (KASA) ---
    @FXML private TextField barcodeField;
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

    // --- LEWA STRONA (INFO) ---
    @FXML private TextArea productListArea;
    @FXML private TextArea discountInfoArea;

    // --- PRAWA STRONA (PARAGON I ZWROTY) ---
    @FXML private TextArea receiptArea;
    @FXML private TextField returnField;

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
    }

    private void loadStaticInfo() {
        // Wypisz produkty po lewej stronie
        StringBuilder products = new StringBuilder();
        for (Product p : posService.getAllProducts()) {
            products.append(p.getName()).append(" (").append(p.getBarcode()).append(") - ").append(p.getPrice()).append(" zł\n");
        }
        productListArea.setText(products.toString());

        // Wypisz info o rabatach
        discountInfoArea.setText("RABATY:\n- Zakupy powyżej 50 zł: 10% zniżki\n- Zakupy poniżej 50 zł: Brak zniżki");
    }

    @FXML
    public void handleScan() {
        if (posService == null) return;
        String barcode = barcodeField.getText();
        ReceiptItem item = posService.scanProduct(barcode);

        if (item != null) {
            refreshView();
            barcodeField.clear();
            barcodeField.requestFocus(); // Utrzymaj kursor w polu
        } else {
            showAlert("Nie znaleziono produktu!");
        }
    }

    @FXML
    public void handleCheckout() {
        if (posService == null || posService.getCart().isEmpty()) {
            showAlert("Koszyk jest pusty!");
            return;
        }

        // Pobierz wybraną metodę płatności
        String method = rbCash.isSelected() ? "GOTÓWKA" : "KARTA";

        String receipt = posService.checkout(method);
        receiptArea.setText(receipt);
        refreshView();
    }

    @FXML
    public void handleReturn() {
        String barcode = returnField.getText();
        if (barcode.isEmpty()) return;

        String result = posService.returnProduct(barcode);
        showAlert(result); // Pokaż wynik zwrotu w okienku
        returnField.clear();
    }

    private void refreshView() {
        cartTable.getItems().setAll(posService.getCart());
        double sum = posService.getCart().stream().mapToDouble(ReceiptItem::getTotal).sum();
        totalLabel.setText(String.format("SUMA: %.2f PLN", sum));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
}