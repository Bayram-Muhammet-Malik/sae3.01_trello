module com.example.saetrello {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.saetrello to javafx.fxml;
    exports com.example.saetrello;
}