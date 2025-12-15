package appSAE;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class VueMenu extends BorderPane implements Observateur {
    private Model model;
    private MainWindow mainWindow;
    private final ArrayList<Button> navButtons = new ArrayList<>();

    public VueMenu(Model model, MainWindow mainWindow) {
        this.model = model;
        this.mainWindow = mainWindow;
        this.setStyle("-fx-background-color: #2563eb; -fx-padding: 4px;");
        actualiser(model);
    }

    @Override
    public void actualiser(Sujet s) {
        this.getChildren().clear();
        navButtons.clear();
        HBox hbox = new HBox(5);

        ControlerMenu controller = new ControlerMenu(model, mainWindow);

        if (model.getFilepath() != null) {
            Label fileText = new Label("Unknown");
            fileText.setTextFill(javafx.scene.paint.Color.WHITE);
            fileText.setStyle("-fx-font-size: 21px; -fx-font-weight: bold;");
            try {
                fileText.setText(new File(model.getFilepath()).getName());
                this.setLeft(fileText);
            } catch (Exception ex) {
                this.setLeft(fileText);
            }

            Button creerListeBtn = new Button("+ Créer une liste");
            creerListeBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e5e7eb; -fx-background-radius: 8px;");
            creerListeBtn.setOnAction(e -> this.ouvrirPopupCreationTache());

            Button trelloBtn = new Button();
            trelloBtn.setId("BUREAU");
            trelloBtn.setGraphic(createIcon("file:icons/trello-brands-solid-full.png"));
            trelloBtn.setStyle("-fx-background-color: transparent;");
            trelloBtn.setOnAction(controller);

            Button listBtn = new Button();
            listBtn.setId("LISTE");
            listBtn.setGraphic(createIcon("file:icons/list-check-solid-full.png"));
            listBtn.setStyle("-fx-background-color: transparent;");
            listBtn.setOnAction(controller);

            Button ganttBtn = new Button();
            ganttBtn.setId("GANTT");
            ganttBtn.setGraphic(createIcon("file:icons/chart-gantt-solid-full.png"));
            ganttBtn.setStyle("-fx-background-color: transparent;");
            ganttBtn.setOnAction(controller);

            navButtons.add(trelloBtn);
            navButtons.add(listBtn);
            navButtons.add(ganttBtn);
            hbox.getChildren().addAll(creerListeBtn, trelloBtn, listBtn, ganttBtn);
        }

        Button homeBtn = new Button();
        homeBtn.setId("HOME");
        homeBtn.setGraphic(createIcon("file:icons/house-regular-full.png"));
        homeBtn.setStyle("-fx-background-color: #1d4ed8; -fx-background-radius: 8px;");
        homeBtn.setOnAction(controller);

        navButtons.add(homeBtn);
        hbox.getChildren().add(homeBtn);

        // Placement à droite
        this.setRight(hbox);
    }

    private ImageView createIcon(String path) {
        ImageView icon = new ImageView(path);
        icon.setFitWidth(30);
        icon.setFitHeight(30);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);
        return icon;
    }

    public void setActiveButton(String type) {
        for (Button btn : navButtons) {
            String id = btn.getId();
            if (id != null && id.equals(type)) {
                btn.setStyle("-fx-background-color: #1d4ed8; -fx-background-radius: 8px;");
            } else {
                btn.setStyle("-fx-background-color: transparent;");
            }
        }
    }

    private void ouvrirPopupCreationTache() {
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
            // A déplacer dans ControllerMenu
            /*
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
             */

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
}