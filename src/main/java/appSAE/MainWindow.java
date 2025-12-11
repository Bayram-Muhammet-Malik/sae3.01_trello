package appSAE;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainWindow extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("HiTask");
        stage.setMinHeight(324);
        stage.setMinWidth(576);
        stage.setMaximized(true);
        stage.show();
    }
}
