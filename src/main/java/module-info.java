module com.mycompany.cpit.project {
    // للسماح باستخدام مكونات جافا إف إكس
    requires javafx.controls;
    requires javafx.fxml; // مهم إذا كنت ستستخدم ملفات تصميم FXML

    requires de.jensd.fx.glyphs.fontawesome;
    // السطر الأهم: يسمح لجافا إف إكس بالوصول لملفاتك لتشغيل التطبيق
    opens com.mycompany.inventoryfx to javafx.fxml, javafx.graphics;
}