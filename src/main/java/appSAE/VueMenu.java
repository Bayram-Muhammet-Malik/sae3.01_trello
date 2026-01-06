package appSAE;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.File;

public class VueMenu extends BorderPane implements Observateur {
    private Model model;
    private MainWindow mainWindow;

    public VueMenu(Model model, MainWindow mainWindow) {
        this.model = model;
        this.mainWindow = mainWindow;
        this.setStyle("-fx-background-color: #2563eb; -fx-padding: 4px;");
        actualiser(model);
    }

    @Override
    public void actualiser(Sujet s) {
        this.getChildren().clear();
        HBox hbox = new HBox(5);

        ControlerMenu controller = new ControlerMenu(model, mainWindow);
        String lastVue = model.getLastVue();

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
            creerListeBtn.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #1d4ed8; -fx-background-radius: 8px;");
            creerListeBtn.setOnAction(e -> MainWindow.ouvrirPopupListe(null, model));

            Button trelloBtn = createNavButton("BUREAU", "file:icons/trello-brands-solid-full.png", lastVue, controller);
            Button listBtn = createNavButton("LISTE", "file:icons/list-check-solid-full.png", lastVue, controller);
            Button ganttBtn = createNavButton("GANTT", "file:icons/chart-gantt-solid-full.png", lastVue, controller);
            hbox.getChildren().addAll(creerListeBtn, trelloBtn, listBtn, ganttBtn);
        }

        Button homeBtn = createNavButton("HOME", "file:icons/house-regular-full.png", lastVue, controller);
        hbox.getChildren().add(homeBtn);

        // Placement à droite
        this.setRight(hbox);
    }

    private Button createNavButton(String id, String iconPath, String lastVue, ControlerMenu controller) {
        ImageView icon = new ImageView(iconPath);
        icon.setFitWidth(30);
        icon.setFitHeight(30);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);

        Button btn = new Button(null, icon);
        btn.setId(id);
        btn.setStyle(id.equals(lastVue) ? "-fx-background-color: #1d4ed8; -fx-background-radius: 8px;" : "-fx-background-color: transparent;");
        btn.setOnAction(controller);
        return btn;
    }
}