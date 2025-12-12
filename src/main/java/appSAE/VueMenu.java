package appSAE;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.File;

public class VueMenu extends BorderPane implements Observateur {
    private Model model;

    public VueMenu(Model model) {
        this.model = model;
        this.setStyle("-fx-background-color: #2563eb; -fx-padding: 7px;");
        actualiser(model);
    }

    @Override
    public void actualiser(Sujet s) {
        this.getChildren().clear();
        Label fileText = new Label("Title not found");
        fileText.setTextFill(javafx.scene.paint.Color.WHITE);
        fileText.setStyle("-fx-font-size: 22");
        try {
            fileText.setText(new File(model.getFilepath()).getName());
            this.setLeft(fileText);
        } catch (Exception ex) {
            this.setLeft(fileText);
        }
        HBox hbox = new HBox();

        // Création des boutons avec icônes
        Button trelloBtn = createIconButton("file:icons/trello-brands-solid-full.png", false);
        Button listBtn = createIconButton("file:icons/list-check-solid-full.png", false);
        Button ganttBtn = createIconButton("file:icons/chart-gantt-solid-full.png", false);
        Button homeBtn = createIconButton("file:icons/house-regular-full.png", true);

        hbox.getChildren().addAll(trelloBtn, listBtn, ganttBtn, homeBtn);

        // Placement à droite
        this.setRight(hbox);
    }

    private Button createIconButton(String path, boolean estActive) {
        ImageView icon = new ImageView(path);
        icon.setFitWidth(30);
        icon.setFitHeight(30);
        icon.setPreserveRatio(true);

        Button btn = new Button();
        btn.setGraphic(icon);
        btn.setStyle(estActive ? "-fx-background-color: #1d4ed8; -fx-background-radius: 8px;" : "-fx-background-color: transparent;");

        return btn;
    }
}