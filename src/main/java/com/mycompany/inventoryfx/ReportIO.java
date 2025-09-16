/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
/**
 *
 * @author Amani
 */
public class ReportIO {
    private ReportIO(){}

    public static Path writeLowStockReport(ObservableList<Product> products, int threshold)
            throws ReportIOException {
        String fileName = "low_stock_report_" + LocalDate.now() + ".txt";
        Path path = Paths.get(fileName);
        StringBuilder sb = new StringBuilder();

        sb.append("=== Low Stock Report (").append(LocalDate.now()).append(") ===\n");
        sb.append("Threshold: ").append(threshold).append("\n\n");

        boolean[] any = {false};
        products.stream()
                .filter(p -> {
                    try { return p.getQuantity() <= threshold; }
                    catch (Exception e) { return false; }
                })
                .forEach(p -> {
                    any[0] = true;
                    sb.append(String.format("ID: %d | Name: %s | Qty: %d | Price: %.2f%n",
                            p.getId(), p.getName(), p.getQuantity(), p.getPrice()));
                });

        if (!any[0]) sb.append("No items are low on stock.\n");
        sb.append("\n--- End of Report ---\n");

        // اكتب الملف (المجلد الحالي). لو فشل جرّب مجلد المستخدم
        try {
            write(path, sb.toString(), false);
            return path.toAbsolutePath();
        } catch (IOException e1) {
            Path fallback = Paths.get(System.getProperty("user.home"), fileName);
            try {
                write(fallback, sb.toString(), false);
                return fallback.toAbsolutePath();
            } catch (IOException e2) {
                throw new ReportIOException("Failed to save report in both current and user folders.", e2);
            }
        }
    }

    private static void write(Path path, String content, boolean append) throws IOException {
        try (FileWriter w = new FileWriter(path.toFile(), append)) {
            w.write(content);
            w.flush();
        }
    }
}
