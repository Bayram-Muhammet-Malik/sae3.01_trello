package appSAE;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MainWindow extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Model model = new Model();
        /*
        VueMenu menu = new VueMenu
        model.enregistrerObservateur(menu);
        root.setTop(menu);
        */

        // Centre root
        StackPane centreRoot = new StackPane();


        BorderPane homePage = new BorderPane();
        Text welcomeText = new Text("Bienvenue dans HiTask");
        welcomeText.setStyle("-fx-font-size: 25");
        StackPane topPane = new StackPane(welcomeText);
        topPane.setAlignment(Pos.CENTER);
        homePage.setTop(topPane);

        HBox btnHP = new HBox();

        VBox createBtnBox = new VBox();
        createBtnBox.getChildren().add(new ImageView("file:icons/plus-solid-full.png") {{
            setFitWidth(32);
            setFitHeight(32);
            setPreserveRatio(true);
        }});
        createBtnBox.getChildren().add(new Label("Créer") {{
            setTextFill(javafx.scene.paint.Color.WHITE);
        }});
        Button createBtn = new Button();
        createBtn.setGraphic(createBtnBox);
        createBtn.setPrefSize(200, 130);
        createBtn.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 8;");
        createBtnBox.setAlignment(Pos.CENTER);

        VBox openBtnBox = new VBox();
        openBtnBox.getChildren().add(new ImageView("file:icons/folder-open-regular-full.png") {{
            setFitWidth(32);
            setFitHeight(32);
            setPreserveRatio(true);
        }});
        openBtnBox.getChildren().add(new Label("Ouvrir"));
        Button openBtn = new Button();
        openBtn.setGraphic(openBtnBox);
        openBtn.setPrefSize(200, 130);
        openBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #cccccc; -fx-border-radius: 8;");
        openBtnBox.setAlignment(Pos.CENTER);

        btnHP.getChildren().addAll(createBtn, openBtn);
        btnHP.setAlignment(Pos.CENTER);
        btnHP.setSpacing(40);
        btnHP.setStyle("-fx-padding: 40;");
        homePage.setCenter(btnHP);

        centreRoot.getChildren().add(homePage);
        root.setCenter(centreRoot);

        /*
        VueBureau bureau = new VueBureau
        model.enregistrerObservateur(bureau);
        */

        Scene scene = new Scene(root);

        stage.setTitle("HiTask");
        stage.setMinHeight(324);
        stage.setMinWidth(576);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
