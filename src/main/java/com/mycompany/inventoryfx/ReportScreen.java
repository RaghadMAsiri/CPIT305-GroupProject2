package com.mycompany.inventoryfx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.nio.file.Path;
import java.time.LocalDate;


public class ReportScreen {
    
    private Spinner<Integer> thresholdSpinner;
    private TableView<Product> tableRef;

    private TextArea txtArea; // text area to show the report
    
    public ReportScreen() { }

    public ReportScreen(TableView<Product> tableRef) {
    this.tableRef = tableRef;
    }

    // Show the Report window
    public void show() {
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle("Low Stock Report");

    BorderPane layout = new BorderPane();
    layout.setStyle("-fx-background-color: #FFFFFF;");

    // ===== Title =====
    Label title = new Label("Low Stock Report");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-padding: 15;");
    title.setMaxWidth(Double.MAX_VALUE);
    title.setAlignment(Pos.CENTER);
    layout.setTop(title);

    // ===== Report Area =====
    txtArea = new TextArea();
    txtArea.setEditable(false);
    txtArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 13px;");
    BorderPane.setMargin(txtArea, new Insets(0, 15, 15, 15));
    layout.setCenter(txtArea);

    // ===== Controls (Threshold + Load + Save) =====
    Spinner<Integer> thresholdSpinner = new Spinner<>(1, 9999, Product.getLowStockThreshold());
    thresholdSpinner.setEditable(true);
    thresholdSpinner.setPrefWidth(100);

    Button btnLoad = new Button("Load Report");
    btnLoad.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REFRESH));

    Button btnSave = new Button("Save to .txt");
    btnSave.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));

    // Styles (نفس ستايل زرّك السابق)
    String defaultStyle = "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
    String hoverStyle   = "-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
    btnLoad.setStyle(defaultStyle);
    btnSave.setStyle(defaultStyle);
    btnLoad.setOnMouseEntered(e -> btnLoad.setStyle(hoverStyle));
    btnLoad.setOnMouseExited(e -> btnLoad.setStyle(defaultStyle));
    btnSave.setOnMouseEntered(e -> btnSave.setStyle(hoverStyle));
    btnSave.setOnMouseExited(e -> btnSave.setStyle(defaultStyle));

    // Actions
    btnLoad.setOnAction(e -> loadReport(thresholdSpinner.getValue()));
    btnSave.setOnAction(e -> saveReport(thresholdSpinner.getValue()));

    HBox controls = new HBox(10,
            new Label("Threshold:"), thresholdSpinner,
            btnLoad, btnSave
    );
    controls.setAlignment(Pos.CENTER_LEFT);
    controls.setPadding(new Insets(0, 15, 15, 15));

    layout.setBottom(controls);

    // ===== Show Window =====
    Scene scene = new Scene(layout, 550, 420);
    stage.setScene(scene);
    stage.showAndWait();
}


   
   // Build and display the report based on Product data
private void loadReport(int threshold) {
    txtArea.clear();

    StringBuilder sb = new StringBuilder("=== Low Stock Report (threshold "
            + threshold + ") ===\n\n");

    Product.all().stream()
            .filter(p -> p.getQuantity() <= threshold)
            .forEach(p -> sb.append(String.format(
                    "ID: %d | Name: %s | Qty: %d | Price: %.2f%n",
                    p.getId(), p.getName(), p.getQuantity(), p.getPrice()
            )));

    if (sb.toString().endsWith("===\n\n")) {
        sb.append("No items are low on stock.\n");
    }

    txtArea.setText(sb.toString());
}
private void saveReport(int threshold) {
    try {
        java.nio.file.Path path = ReportIO.writeLowStockReport(
                javafx.collections.FXCollections.observableArrayList(Product.all()),
                threshold
        );
        Alert ok = new Alert(Alert.AlertType.INFORMATION,
                "Report saved to:\n" + path.toAbsolutePath(), ButtonType.OK);
        ok.setHeaderText(null);
        ok.setTitle("Saved");
        ok.showAndWait();
    } catch (ReportIOException ex) {
        Alert err = new Alert(Alert.AlertType.ERROR,
                "Could not save report.\n" + ex.getMessage(), ButtonType.OK);
        err.setHeaderText(null);
        err.setTitle("Save Failed");
        err.showAndWait();
    } catch (Exception ex) {
        ex.printStackTrace();
        Alert err = new Alert(Alert.AlertType.ERROR,
                "Unexpected error while saving the report.", ButtonType.OK);
        err.setHeaderText(null);
        err.setTitle("Unexpected");
        err.showAndWait();
    }
}


}
