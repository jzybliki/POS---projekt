package com.example.pos.utils;

import java.io.BufferedWriter; // Narzędzie do wydajnego zapisu tekstu do pliku.
import java.io.File; // Reprezentacja pliku na dysku.
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptPrinter implements Runnable {
    // Implementacja interfejsu Runnable pozwala na uruchomienie tej klasy w osobnym wątku
    private final String content;

    public ReceiptPrinter(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        // Symulacja czasu drukowania
        try {
            System.out.println("Rozpoczynam drukowanie paragonu...");
            Thread.sleep(1500);

            // Operacje I/O - Zapis do pliku
            saveReceiptToFile();

            System.out.println("Paragon wydrukowany i zapisany!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveReceiptToFile() {
        // Generujemy unikalną nazwę pliku z datą i godziną
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "paragon_" + timestamp + ".txt";

        // Blok try-with-resources automatycznie zamyka plik
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisu paragonu: " + e.getMessage());
        }
    }
}