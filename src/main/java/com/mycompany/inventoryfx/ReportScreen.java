/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

/**
 *
 * @author ragha
 */

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportScreen {
    
    private TextArea txtArea;

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Low Stock Report");

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #FFFFFF;");

        // العنوان
        Label title = new Label("Low Stock Report");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-padding: 15;");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        layout.setTop(title);

        // منطقة النص
        txtArea = new TextArea();
        txtArea.setEditable(false);
        txtArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 13px;");
        BorderPane.setMargin(txtArea, new Insets(0, 15, 15, 15));
        layout.setCenter(txtArea);

        // زر تحميل التقرير
        Button btnLoad = new Button("Load Report");
        btnLoad.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        btnLoad.setMaxWidth(Double.MAX_VALUE);
        String defaultStyle = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
        String hoverStyle = "-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
        btnLoad.setStyle(defaultStyle);
        btnLoad.setOnMouseEntered(e -> btnLoad.setStyle(hoverStyle));
        btnLoad.setOnMouseExited(e -> btnLoad.setStyle(defaultStyle));
        btnLoad.setOnAction(e -> loadReport(stage));
        layout.setBottom(btnLoad);

        Scene scene = new Scene(layout, 550, 400);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private void loadReport(Stage owner) {
        try (BufferedReader br = new BufferedReader(new FileReader("low_stock_report.txt"))) {
            txtArea.clear();
            String line;
            while ((line = br.readLine()) != null) {
                txtArea.appendText(line + "\n");
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(owner);
            alert.setTitle("Error");
            alert.setHeaderText("Report file not found!");
            alert.setContentText("Please make sure 'low_stock_report.txt' exists in the project directory.");
            alert.showAndWait();
        }
    }
}