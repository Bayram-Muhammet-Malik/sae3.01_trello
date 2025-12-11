module appSAE {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens appSAE to javafx.fxml;
    exports appSAE;
}