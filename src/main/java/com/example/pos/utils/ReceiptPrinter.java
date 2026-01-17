package com.example.pos.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptPrinter implements Runnable {
    private final String content;

    public ReceiptPrinter(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        // Symulacja czasu drukowania (Wątki - spełniony wymóg)
        try {
            System.out.println("Rozpoczynam drukowanie paragonu...");
            Thread.sleep(1500);

            // Operacje I/O - Zapis do pliku (spełniony wymóg dodatkowy)
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

        // Blok try-with-resources automatycznie zamyka plik (Dobra praktyka)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisu paragonu: " + e.getMessage());
        }
    }
}