package appSAE;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainWindow extends Application {

    private final BorderPane homePage = new BorderPane();

    // vues
    private VueBureau vb;
    private VueListe vl;
    private VueMenu menu;

    // modele
    private Model model;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        // ----- modele -----
        model = new Model();

        // ----- menu -----
        menu = new VueMenu(model, this);
        model.enregistrerObservateur(menu);

        root.setTop(menu);

        // ----- centre -----
        StackPane centreRoot = new StackPane();

        // ===== HOME PAGE =====
        Text welcomeText = new Text("Bienvenue dans HiTask");
        welcomeText.setStyle("-fx-font-size: 25");
        StackPane topPane = new StackPane(welcomeText);
        topPane.setAlignment(Pos.CENTER);
        homePage.setTop(topPane);

        HBox btnHP = new HBox(40);
        btnHP.setAlignment(Pos.CENTER);
        btnHP.setStyle("-fx-padding: 40;");

        // bouton creer
        VBox createBtnBox = new VBox(6);
        createBtnBox.setAlignment(Pos.CENTER);
        createBtnBox.getChildren().addAll(
                new ImageView("file:icons/plus-solid-full.png") {{
                    setFitWidth(32);
                    setFitHeight(32);
                    setPreserveRatio(true);
                }},
                new Label("Créer") {{
                    setTextFill(javafx.scene.paint.Color.WHITE);
                }}
        );

        Button createBtn = new Button();
        createBtn.setId("createBtn");
        createBtn.setGraphic(createBtnBox);
        createBtn.setPrefSize(200, 130);
        createBtn.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 8;");
        createBtn.setOnAction(new ControlerHomeBtn(model, this));

        // bouton ouvrir
        VBox openBtnBox = new VBox(6);
        openBtnBox.setAlignment(Pos.CENTER);
        openBtnBox.getChildren().addAll(
                new ImageView("file:icons/folder-open-regular-full.png") {{
                    setFitWidth(32);
                    setFitHeight(32);
                    setPreserveRatio(true);
                }},
                new Label("Ouvrir")
        );

        Button openBtn = new Button();
        openBtn.setId("openBtn");
        openBtn.setGraphic(openBtnBox);
        openBtn.setPrefSize(200, 130);
        openBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #cccccc; -fx-border-radius: 8;");
        openBtn.setOnAction(new ControlerHomeBtn(model, this));

        btnHP.getChildren().addAll(createBtn, openBtn);
        homePage.setCenter(btnHP);

        // ===== VUES =====
        vb = new VueBureau(model);
        model.enregistrerObservateur(vb);
        vb.setVisible(false);
        vb.setManaged(false);

        vl = new VueListe(model);
        model.enregistrerObservateur(vl);
        vl.setVisible(false);
        vl.setManaged(false);

        // empilement des vues
        centreRoot.getChildren().addAll(homePage, vb, vl);
        root.setCenter(centreRoot);
        BorderPane.setMargin(centreRoot, new Insets(10));

        // ----- scene -----
        Scene scene = new Scene(root);
        stage.setTitle("HiTask");
        stage.setMinHeight(324);
        stage.setMinWidth(576);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    // ===== CHANGEMENT DE VUE =====
    public void switchView(String type) {

        homePage.setVisible(false);
        homePage.setManaged(false);

        vb.setVisible(false);
        vb.setManaged(false);

        vl.setVisible(false);
        vl.setManaged(false);

        switch (type) {
            case "HOME" -> {
                homePage.setVisible(true);
                homePage.setManaged(true);
            }
            case "BUREAU" -> {
                vb.setVisible(true);
                vb.setManaged(true);
            }
            case "LISTE" -> {
                vl.setVisible(true);
                vl.setManaged(true);
            }
        }
        
    }

    // ===== POPUP CREATION DE TACHE =====
    public void ouvrirPopupCreationTache() {

        Stage popup = new Stage();
        popup.setTitle("Créer une tâche");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        TextField titreField = new TextField();
        titreField.setPromptText("Titre de la tâche");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);

        TextField dateField = new TextField();
        dateField.setPromptText("Date (jj-mm-aaaa)");

        ComboBox<Tache.Priorite> prioBox = new ComboBox<>();
        prioBox.getItems().addAll(Tache.Priorite.values());
        prioBox.setValue(Tache.Priorite.NORMAL);

        Button valider = new Button("Créer");
        valider.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8px;"
        );

        valider.setOnAction(e -> {

            String titre = titreField.getText();
            String desc = descField.getText();
            String date = dateField.getText();
            Tache.Priorite prio = prioBox.getValue();

            if (titre.isEmpty() || date.isEmpty()) return;

            if (!model.getListes().isEmpty()) {
                CompositeTache t = new CompositeTache(titre, desc, date, prio);
                model.getListes().get(0).ajouterCarte(t);
                model.notifierObservateur();
            }

            popup.close();
        });

        root.getChildren().addAll(
                new Label("Titre"),
                titreField,
                new Label("Description"),
                descField,
                new Label("Date"),
                dateField,
                new Label("Priorité"),
                prioBox,
                valider
        );

        popup.setScene(new Scene(root, 320, 420));
        popup.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
