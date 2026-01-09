package com.example.pos.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReceiptPrinter implements Runnable {
    private final String content;

    public ReceiptPrinter(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        try {
            // Symulacja opóźnienia drukarki (wątek czeka 1.5 sekundy)
            Thread.sleep(1500);

            // Zapis do pliku (I/O) - to tworzy plik tekstowy na dysku
            String fileName = "paragon_" + System.currentTimeMillis() + ".txt";
            try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
                out.println(content);
            }
            System.out.println(">>> Wydrukowano paragon: " + fileName);

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}