module appSAE {
    requires javafx.controls;
    requires javafx.fxml;


    opens appSAE to javafx.fxml;
    exports appSAE;
}