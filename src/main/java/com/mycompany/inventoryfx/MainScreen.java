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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainScreen {

    private final String role;
    private TableView<Product> table;

    public MainScreen(String role) {
        this.role = role;
    }

    public void show() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Inventory Manager - Dashboard");

        // الهيكل الرئيسي للوحة التحكم
        BorderPane mainLayout = new BorderPane();

        // 1. القائمة الجانبية للتنقل
        VBox navBar = createNavBar();
        mainLayout.setLeft(navBar);

        // 2. المحتوى الرئيسي (البطاقات والجدول)
        BorderPane contentPane = new BorderPane();
        contentPane.setPadding(new Insets(15));

        // -- إضافة البطاقات الإحصائية في الأعلى --
        HBox statsBar = createStatsBar();
        contentPane.setTop(statsBar);
        BorderPane.setMargin(statsBar, new Insets(0, 0, 15, 0));

        // -- إعداد جدول المنتجات --
        setupTable();
        contentPane.setCenter(new ScrollPane(table));

        mainLayout.setCenter(contentPane);

        Scene scene = new Scene(mainLayout, 950, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // دالة لإنشاء القائمة الجانبية
    private VBox createNavBar() {
        VBox navBar = new VBox(20);
        navBar.setPadding(new Insets(20));
        navBar.setStyle("-fx-background-color: #2C3E50;"); // لون كحلي غامق
        navBar.setAlignment(Pos.TOP_CENTER);

        Text logo = new Text("Inventory 🏪");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        logo.setFill(Color.WHITE);

        Button btnDashboard = createNavButton("Dashboard", FontAwesomeIcon.HOME);
        Button btnProducts = createNavButton("Products", FontAwesomeIcon.CUBES);
        Button btnReports = createNavButton("Reports", FontAwesomeIcon.CLIPBOARD);
        Button btnAdmin = createNavButton("Admin Panel", FontAwesomeIcon.USER_SECRET);

        // ربط الأوامر بالأزرار
        btnReports.setOnAction(e -> new ReportScreen().show());
        btnAdmin.setOnAction(e -> new AdminScreen().show());

        if (!role.equalsIgnoreCase("Admin")) {
            btnAdmin.setDisable(true); // تعطيل الزر بدلاً من إخفائه
        }
        
        navBar.getChildren().addAll(logo, new Separator(), btnDashboard, btnProducts, btnReports, btnAdmin);
        return navBar;
    }

    // دالة لإنشاء أزرار القائمة مع أيقونات
    private Button createNavButton(String text, FontAwesomeIcon iconName) {
        Button button = new Button(text);
        FontAwesomeIconView icon = new FontAwesomeIconView(iconName);
        icon.setFill(Color.WHITE);
        icon.setSize("1.5em");
        
        button.setGraphic(icon);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setGraphicTextGap(15);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        // إضافة تأثير الـ hover برمجياً
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
        
        return button;
    }

    // دالة لإنشاء بطاقات الإحصائيات
    private HBox createStatsBar() {
        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER);

        // بيانات وهمية (يمكن تحديثها لاحقاً)
        VBox card1 = createStatCard("Total Products", "4", "#2980B9");
        VBox card2 = createStatCard("Low Stock", "1", "#E67E22");
        VBox card3 = createStatCard("Stock Value", "$87", "#27AE60");
        VBox card4 = createStatCard("Newest Item", "Milk", "#8E44AD");

        statsBar.getChildren().addAll(card1, card2, card3, card4);
        return statsBar;
    }

    // دالة لإنشاء بطاقة إحصائية واحدة
    private VBox createStatCard(String title, String value, String bgColor) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;");

        Text titleText = new Text(title);
        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Text valueText = new Text(value);
        valueText.setFill(Color.WHITE);
        valueText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        card.getChildren().addAll(titleText, valueText);
        return card;
    }

    // إعداد الجدول (نفس الكود السابق تقريباً)
    private void setupTable() {
        table = new TableView<>();
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().addAll(idCol, nameCol, qtyCol, priceCol);
        refreshTable();
    }
    
    // تحديث بيانات الجدول
    private void refreshTable() {
        ObservableList<Product> products = FXCollections.observableArrayList(
                new Product(1, "Apple", 50, 1.5),
                new Product(2, "Banana", 20, 1.0),
                new Product(3, "Orange", 10, 2.0),
                new Product(4, "Milk", 5, 3.0)
        );
        table.setItems(products);
    }
}