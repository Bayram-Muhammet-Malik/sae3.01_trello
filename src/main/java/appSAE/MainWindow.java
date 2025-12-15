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

public class MainWindow extends Application {
    private BorderPane homePage = new BorderPane();
    private VueBureau vb;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Model model = new Model();
        VueMenu menu = new VueMenu(model);
        model.enregistrerObservateur(menu);

        root.setTop(menu);

        // Centre root
        StackPane centreRoot = new StackPane();

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
        createBtn.setId("createBtn");
        createBtn.setOnAction(new ControlerHomeBtn(model, this));
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
        openBtn.setId("openBtn");
        openBtn.setOnAction(new ControlerHomeBtn(model, this));
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

        vb = new VueBureau(model);
        model.enregistrerObservateur(vb);
        vb.setVisible(false);
        vb.setManaged(false);

        centreRoot.getChildren().add(vb);

        root.setCenter(centreRoot);

        Scene scene = new Scene(root);

        stage.setTitle("HiTask");
        stage.setMinHeight(324);
        stage.setMinWidth(576);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public void switchView(String type) {
        homePage.setVisible(false);
        homePage.setManaged(false);
        vb.setVisible(false);
        vb.setManaged(false);

        switch (type) {
            case "HOME" -> { homePage.setVisible(true); homePage.setManaged(true); }
            case "BUREAU" -> { vb.setVisible(true); vb.setManaged(true); }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
