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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class AdminScreen {

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Admin Panel - Manage Products");

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #F4F4F9;");

        // شريط العنوان
        Label title = new Label("Product Information");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(10));
        layout.setTop(title);

        // حاوية الحقول
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(12);
        grid.setPadding(new Insets(20));

        // الحقول والنصوص
        grid.add(new Label("Product ID:"), 0, 0);
        TextField txtId = new TextField();
        grid.add(txtId, 1, 0);

        grid.add(new Label("Name:"), 0, 1);
        TextField txtName = new TextField();
        grid.add(txtName, 1, 1);

        grid.add(new Label("Quantity:"), 0, 2);
        TextField txtQty = new TextField();
        grid.add(txtQty, 1, 2);

        grid.add(new Label("Price:"), 0, 3);
        TextField txtPrice = new TextField();
        grid.add(txtPrice, 1, 3);

        layout.setCenter(grid);

        // حاوية الأزرار
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(15));
        buttonBar.setStyle("-fx-background-color: #ECF0F1;");

        Button btnAdd = createStyledButton("Add", FontAwesomeIcon.PLUS, "#27AE60");
        btnAdd.setOnAction(e -> showAlert("Add Product Clicked"));

        Button btnUpdate = createStyledButton("Update", FontAwesomeIcon.PENCIL, "#2980B9");
        btnUpdate.setOnAction(e -> showAlert("Update Product Clicked"));

        Button btnDelete = createStyledButton("Delete", FontAwesomeIcon.TRASH, "#C0392B");
        btnDelete.setOnAction(e -> showAlert("Delete Product Clicked"));

        buttonBar.getChildren().addAll(btnAdd, btnUpdate, btnDelete);
        layout.setBottom(buttonBar);

        Scene scene = new Scene(layout, 450, 350);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // دالة مساعدة لإنشاء الأزرار مع أيقونات وتصميم موحد
    // دالة مساعدة لإنشاء الأزرار مع أيقونات وتصميم موحد
    private Button createStyledButton(String text, FontAwesomeIcon icon, String color) {
        Button button = new Button(text);
        button.setGraphic(new FontAwesomeIconView(icon, "1.2em"));
        String defaultStyle = "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
        button.setStyle(defaultStyle);

        // --- هذا هو الجزء الذي تم تعديله بالكامل ---
        button.setOnMouseEntered(e -> {
            // 1. تحويل لون الهيكس إلى كائن لون خاص بـ JavaFX
            Color baseColor = Color.web(color);
            // 2. استخدام دالة .darker() لتغميق اللون
            Color darkerColor = baseColor.darker();
            // 3. تحويل اللون الجديد إلى نص CSS
            String hoverStyle = String.format("-fx-background-color: #%02x%02x%02x; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;",
                    (int) (darkerColor.getRed() * 255),
                    (int) (darkerColor.getGreen() * 255),
                    (int) (darkerColor.getBlue() * 255));
            button.setStyle(hoverStyle);
        });
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));

        return button;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Action");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
