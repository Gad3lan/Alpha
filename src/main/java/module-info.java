module com.alpha {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens com.alpha to javafx.fxml;
    exports com.alpha;
}