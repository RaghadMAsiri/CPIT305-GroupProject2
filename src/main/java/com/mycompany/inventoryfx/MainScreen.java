package com.mycompany.inventoryfx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.FlowPane;

public class MainScreen {

    private final String role;               // "Admin" or "User"/"Employee"

    // Center container where pages are swapped
    private BorderPane contentPane;

    // Products table (Products page)
    private TableView<Product> table;

    // Dashboard texts (updated via refreshStats)
    private Text totalTxt, lowTxt, valueTxt, newestTxt;

    // Nav button refs + active tracking
    private Button btnDashboard, btnProducts, btnReports, btnAdmin, activeBtn;

    // ---- NEW: Filtering controls for Products page ----
    private TextField searchField;
    private CheckBox lowOnlyCheck;

    // Reusable styles for nav buttons
    private static final String NAV_BASE
            = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;";
    private static final String NAV_ACTIVE
            = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px;";

    public MainScreen(String role) {
        this.role = role;
    }

    public void show() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Inventory Manager");

        BorderPane mainLayout = new BorderPane();

        // Sidebar
        VBox navBar = createNavBar();
        mainLayout.setLeft(navBar);

        // Center container (we'll swap content here)
        contentPane = new BorderPane();
        contentPane.setPadding(new Insets(15));
        mainLayout.setCenter(contentPane);

        // Default page: Dashboard
        contentPane.setCenter(buildDashboardPage());
        activate(btnDashboard);

        Scene scene = new Scene(mainLayout, 1000, 620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ===================== Sidebar =====================
    private VBox createNavBar() {
        VBox navBar = new VBox(20);
        navBar.setPadding(new Insets(20));
        navBar.setStyle("-fx-background-color: #2C3E50;");
        navBar.setAlignment(Pos.TOP_CENTER);

        Text logo = new Text("Inventory 🏪");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        logo.setFill(Color.WHITE);

        btnDashboard = createNavButton("Dashboard", FontAwesomeIcon.HOME);
        btnProducts = createNavButton("Products", FontAwesomeIcon.CUBES);
        btnReports = createNavButton("Reports", FontAwesomeIcon.CLIPBOARD);

        // Swap pages
        btnDashboard.setOnAction(e -> {
            contentPane.setCenter(buildDashboardPage());
            activate(btnDashboard);
        });
        btnProducts.setOnAction(e -> {
            contentPane.setCenter(buildProductsPage());
            activate(btnProducts);
        });
        btnReports.setOnAction(e -> {
            contentPane.setCenter(buildReportsPage());
            activate(btnReports);
        });

        navBar.getChildren().addAll(logo, new Separator(), btnDashboard, btnProducts, btnReports);

        // Only show Admin Panel if role is Admin
        if (role != null && role.equalsIgnoreCase("Admin")) {
            btnAdmin = createNavButton("Admin Panel", FontAwesomeIcon.USER_SECRET);
            btnAdmin.setOnAction(e -> {
                contentPane.setCenter(buildAdminPage());
                activate(btnAdmin);
            });
            navBar.getChildren().add(btnAdmin);
        }

        // Logout button
        Button btnLogout = createNavButton("Logout", FontAwesomeIcon.SIGN_OUT);
        btnLogout.setOnAction(e -> {
            // Close current window
            ((Stage) btnLogout.getScene().getWindow()).close();
            // Reopen login screen
            try {
                new LoginScreen().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        navBar.getChildren().add(new Separator());
        navBar.getChildren().add(btnLogout);

        return navBar;
    }

    private Button createNavButton(String text, FontAwesomeIcon iconName) {
        Button button = new Button(text);
        FontAwesomeIconView icon = new FontAwesomeIconView(iconName);
        icon.setFill(Color.WHITE);
        icon.setSize("1.5em");

        button.setGraphic(icon);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setGraphicTextGap(15);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(NAV_BASE);

        // Hover only if not active
        button.setOnMouseEntered(e -> {
            if (button != activeBtn) {
                button.setStyle(NAV_ACTIVE);
            }
        });
        button.setOnMouseExited(e -> {
            if (button != activeBtn) {
                button.setStyle(NAV_BASE);
            }
        });

        return button;
    }

    private void activate(Button b) {
        if (activeBtn != null) {
            activeBtn.setStyle(NAV_BASE);
        }
        b.setStyle(NAV_ACTIVE);
        activeBtn = b;
    }

    private HBox pageHeader(String title) {
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        HBox box = new HBox(lbl);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 10, 0));
        return box;
    }

    // ===================== Dashboard Page =====================
    private BorderPane buildDashboardPage() {
        BorderPane pane = new BorderPane();
        VBox top = new VBox(pageHeader("Dashboard"));
        pane.setTop(top);

        FlowPane statsBar = createStatsBar();
        pane.setCenter(statsBar);
        refreshStats(); // compute values based on Product store
        return pane;
    }

    private FlowPane createStatsBar() {
        FlowPane stats = new FlowPane(20, 20); // hgap, vgap
        stats.setPadding(new Insets(10, 0, 20, 0));
        stats.setAlignment(Pos.CENTER_LEFT);

        totalTxt = new Text("-");
        lowTxt = new Text("-");
        valueTxt = new Text("-");
        newestTxt = new Text("-");

        stats.getChildren().addAll(
                makeCard("Total Products", totalTxt, FontAwesomeIcon.CUBES, "#1E88E5", "#1565C0"),
                makeCard("Low Stock", lowTxt, FontAwesomeIcon.EXCLAMATION, "#FB8C00", "#EF6C00"),
                makeCard("Stock Value", valueTxt, FontAwesomeIcon.DOLLAR, "#2ECC71", "#27AE60"),
                makeCard("Newest Item", newestTxt, FontAwesomeIcon.STAR, "#8E44AD", "#6C3483")
        );
        return stats;
    }

    private VBox makeCard(String title, Text valueText, FontAwesomeIcon iconName,
            String colorStart, String colorEnd) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(230);
        card.setMinHeight(120);
        card.setStyle(
                "-fx-background-radius: 16;"
                + "-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + ");"
        );

        DropShadow ds = new DropShadow();
        ds.setRadius(12);
        ds.setOffsetY(2);
        ds.setColor(Color.color(0, 0, 0, 0.18));
        card.setEffect(ds);

        FontAwesomeIconView icon = new FontAwesomeIconView(iconName);
        icon.setFill(Color.WHITE);
        icon.setSize("22px");

        Text titleText = new Text(title.toUpperCase());
        titleText.setFill(Color.WHITE);
        titleText.setOpacity(0.95);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        valueText.setFill(Color.WHITE);
        valueText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));

        HBox topRow = new HBox(10, icon, titleText);
        topRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(topRow, valueText);

        card.setOnMouseEntered(e -> {
            card.setCursor(Cursor.HAND);
            card.setScaleX(1.02);
            card.setScaleY(1.02);
            card.setTranslateY(-2);
        });
        card.setOnMouseExited(e -> {
            card.setCursor(Cursor.DEFAULT);
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            card.setTranslateY(0);
        });

        return card;
    }

    private void refreshStats() {
        totalTxt.setText(String.valueOf(Product.totalCount()));
        lowTxt.setText(String.valueOf(Product.lowStockCount()));
        valueTxt.setText(String.format("$%.2f", Product.stockValue()));
        newestTxt.setText(Product.newestItemName());
    }

    // ===================== Products Page (view-only with search/filter) =====================
    private BorderPane buildProductsPage() {
        BorderPane pane = new BorderPane();

        // Header
        VBox header = new VBox(10, pageHeader("Products"));
        header.setPadding(new Insets(0, 0, 5, 0));

        // Filter bar
        searchField = new TextField();
        searchField.setPromptText("Search by ID or Name...");
        lowOnlyCheck = new CheckBox("Low stock only");

        HBox filterBar = new HBox(10, new Label("Filter:"), searchField, lowOnlyCheck);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        header.getChildren().add(filterBar);
        pane.setTop(header);

        // Table (view-only)
        table = buildTableBoundTo(Product.all()); // columns setup
        table.getSelectionModel().setCellSelectionEnabled(false);
        table.setEditable(false);

        // Row highlight for low stock
        installLowStockRowHighlight(table);

        // Hook up filtering
        setupFiltering();

        pane.setCenter(new ScrollPane(table));
        return pane;
    }

    // Bind table to a filtered view of Product.all(), driven by search + checkbox
    private void setupFiltering() {
        FilteredList<Product> filtered = new FilteredList<>(Product.all(), p -> true);

        Runnable apply = () -> {
            String q = (searchField.getText() == null) ? "" : searchField.getText().trim().toLowerCase();
            boolean lowOnly = lowOnlyCheck.isSelected();
            int threshold = Product.getLowStockThreshold();

            filtered.setPredicate(p -> {
                boolean matches = q.isEmpty()
                        || String.valueOf(p.getId()).contains(q)
                        || p.getName().toLowerCase().contains(q);
                boolean lowOk = !lowOnly || p.getQuantity() <= threshold;
                return matches && lowOk;
            });
        };

        searchField.textProperty().addListener((o, oldV, newV) -> apply.run());
        lowOnlyCheck.selectedProperty().addListener((o, oldV, newV) -> apply.run());

        apply.run();
        table.setItems(filtered); // show filtered list
    }

    // Soft-highlight rows that are at/under the low-stock threshold
    private void installLowStockRowHighlight(TableView<Product> tv) {
        tv.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setStyle("");
                } else {
                    setStyle(p.getQuantity() <= Product.getLowStockThreshold()
                            ? "-fx-background-color: #fff3cd;" // light amber
                            : "");
                }
            }
        });
    }

    // Build a basic table bound to the provided list (columns: ID, Name, Qty, Price)
    private TableView<Product> buildTableBoundTo(ObservableList<Product> list) {
        TableView<Product> tv = new TableView<>();
        TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> cd.getValue().idProperty().asObject());
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cd -> cd.getValue().nameProperty());
        nameCol.setPrefWidth(220);
        TableColumn<Product, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(cd -> cd.getValue().quantityProperty().asObject());
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cd -> cd.getValue().priceProperty().asObject());
        tv.getColumns().addAll(idCol, nameCol, qtyCol, priceCol);
        tv.setItems(list);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    // ===================== Reports Page (inline) =====================
    private BorderPane buildReportsPage() {
    BorderPane pane = new BorderPane();
    pane.setPadding(new Insets(15));

    TextArea reportArea = new TextArea();
    reportArea.setEditable(false);
    reportArea.setPromptText("Click 'Load Report' to view low stock items...");

    // زر تحميل التقرير للعرض
    Button loadBtn = new Button("Load Report", new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
    loadBtn.setOnAction(e -> {
        StringBuilder sb = new StringBuilder("=== Low Stock Report (threshold "
                + Product.getLowStockThreshold() + ") ===\n\n");
        Product.all().stream()
                .filter(p -> p.getQuantity() <= Product.getLowStockThreshold())
                .forEach(p -> sb.append(
                        String.format("ID: %d | Name: %s | Qty: %d | Price: %.2f%n",
                                p.getId(), p.getName(), p.getQuantity(), p.getPrice())
                ));
        if (sb.toString().endsWith("===\n\n")) {
            sb.append("No items are low on stock.\n");
        }
        reportArea.setText(sb.toString());
    });

    // زر الحفظ إلى ملف txt (باستخدام ReportIO)
    Button saveBtn = new Button("Save to .txt", new FontAwesomeIconView(FontAwesomeIcon.SAVE));
    saveBtn.setOnAction(e -> {
        try {
            var path = ReportIO.writeLowStockReport(
                    FXCollections.observableArrayList(Product.all()),
                    Product.getLowStockThreshold()
            );
            new Alert(Alert.AlertType.INFORMATION,
                    "Report saved to:\n" + path.toAbsolutePath(),
                    ButtonType.OK).showAndWait();
        } catch (ReportIOException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Could not save report.\n" + ex.getMessage(),
                    ButtonType.OK).showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Unexpected error while saving the report.",
                    ButtonType.OK).showAndWait();
        }
    });

    // شريط الأزرار أسفل التبويب (زرين)
    HBox actions = new HBox(10, loadBtn, saveBtn);
    actions.setAlignment(Pos.CENTER_LEFT);
    actions.setPadding(new Insets(10, 0, 0, 0));

    VBox top = new VBox(10, pageHeader("Reports"));
    pane.setTop(top);
    pane.setCenter(reportArea);
    pane.setBottom(actions);
    return pane;
}


    // ===================== Admin Page (inline) =====================
    private BorderPane buildAdminPage() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(15));
        pane.setTop(pageHeader("Admin Panel"));

        // ------- Left: Form -------
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        TextField idF = new TextField();
        idF.setPromptText("ID (e.g., 1001)");
        TextField nameF = new TextField();
        nameF.setPromptText("Name");
        TextField qtyF = new TextField();
        qtyF.setPromptText("Quantity");
        TextField priceF = new TextField();
        priceF.setPromptText("Price");

        form.addRow(0, new Label("ID:"), idF);
        form.addRow(1, new Label("Name:"), nameF);
        form.addRow(2, new Label("Quantity:"), qtyF);
        form.addRow(3, new Label("Price:"), priceF);

        Button addBtn = new Button("Add", new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addBtn.setOnAction(e -> {
            try {
                Product.add(
                        Integer.parseInt(idF.getText().trim()),
                        nameF.getText().trim(),
                        Integer.parseInt(qtyF.getText().trim()),
                        Double.parseDouble(priceF.getText().trim())
                );
                info("Added.");
                idF.clear();
                nameF.clear();
                qtyF.clear();
                priceF.clear();
                refreshStats();
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        Button updBtn = new Button("Update by ID", new FontAwesomeIconView(FontAwesomeIcon.PENCIL));
        updBtn.setOnAction(e -> {
            try {
                Product.updateById(
                        Integer.parseInt(idF.getText().trim()),
                        nameF.getText().trim(),
                        Integer.parseInt(qtyF.getText().trim()),
                        Double.parseDouble(priceF.getText().trim())
                );
                info("Updated.");
                idF.clear();
                nameF.clear();
                qtyF.clear();
                priceF.clear();
                refreshStats();
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        Button delBtn = new Button("Delete by ID", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        delBtn.setOnAction(e -> {
            try {
                Product.deleteById(Integer.parseInt(idF.getText().trim()));
                info("Deleted.");
                idF.clear();
                nameF.clear();
                qtyF.clear();
                priceF.clear();
                refreshStats();
            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        // Low-stock threshold control
        Spinner<Integer> thresholdSpinner = new Spinner<>(0, 1000, Product.getLowStockThreshold(), 1);
        thresholdSpinner.setEditable(true);
        Button applyThreshold = new Button("Apply Threshold");
        applyThreshold.setOnAction(e -> {
            Product.setLowStockThreshold(thresholdSpinner.getValue());
            refreshStats();
            info("Low-stock threshold set to " + Product.getLowStockThreshold());
        });

        Button resetBtn = new Button("Reset Sample Data", new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        resetBtn.setOnAction(e -> {
            Product.resetSample();
            refreshStats();
            info("Sample data restored.");
        });

        Button clearBtn = new Button("Clear All", new FontAwesomeIconView(FontAwesomeIcon.ERASER));
        clearBtn.setOnAction(e -> {
            if (confirm("Clear all products?")) {
                Product.clearAll();
                refreshStats();
                info("All products cleared.");
            }
        });

        VBox left = new VBox(12,
                form,
                new HBox(10, addBtn, updBtn),
                new HBox(10, delBtn),
                new Separator(),
                new HBox(10, new Label("Low-Stock Threshold:"), thresholdSpinner, applyThreshold),
                new HBox(10, resetBtn, clearBtn)
        );
        left.setAlignment(Pos.TOP_LEFT);

        // ------- Right: Table (shared list) -------
        TableView<Product> adminTable = buildTableBoundTo(Product.all());
        adminTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty()) {
                    Product p = row.getItem();
                    idF.setText(String.valueOf(p.getId()));
                    nameF.setText(p.getName());
                    qtyF.setText(String.valueOf(p.getQuantity()));
                    priceF.setText(String.valueOf(p.getPrice()));
                }
            });
            return row;
        });

        pane.setLeft(left);
        BorderPane.setMargin(left, new Insets(0, 15, 0, 0));
        pane.setCenter(new ScrollPane(adminTable));
        return pane;
    }

    // ===================== Alerts =====================
    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
