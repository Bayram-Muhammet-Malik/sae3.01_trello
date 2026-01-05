module appSAE {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.base;
    requires javafx.graphics;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;


    opens appSAE to javafx.fxml;
    exports appSAE;

}