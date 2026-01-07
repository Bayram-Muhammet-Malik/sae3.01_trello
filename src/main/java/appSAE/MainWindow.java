package appSAE;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainWindow extends Application {
    private BorderPane homePage = new BorderPane();
    private VueMenu menu;
    private VueBureau vb;
    private VueListe vl;
    private VueGantt vg;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4");

        Model model = new Model();
        menu = new VueMenu(model, this);
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

        vb = new VueBureau(model);
        model.enregistrerObservateur(vb);
        vl = new VueListe(model);
        model.enregistrerObservateur(vl);
        vg = new VueGantt(model);
        model.enregistrerObservateur(vg);

        centreRoot.getChildren().addAll(homePage, vb, vl, vg);
        this.switchView("HOME");
        root.setCenter(centreRoot);
        BorderPane.setMargin(centreRoot, new Insets(10));

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
        vl.setVisible(false);
        vl.setManaged(false);
        vg.setVisible(false);
        vg.setManaged(false);

        switch (type) {
            case "HOME" -> { homePage.setVisible(true); homePage.setManaged(true); }
            case "BUREAU" -> { vb.setVisible(true); vb.setManaged(true); }
            case "LISTE" -> { vl.setVisible(true); vl.setManaged(true); }
            case "GANTT" -> { vg.setVisible(true); vg.setManaged(true); }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}