module appSAE {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;

    opens appSAE to javafx.fxml;
    exports appSAE;
}