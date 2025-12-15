package appSAE;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.ArrayList;

public class VueMenu extends BorderPane implements Observateur {
    private Model model;
    private final ArrayList<Button> navButtons = new ArrayList<>();

    public VueMenu(Model model) {
        this.model = model;
        this.setStyle("-fx-background-color: #2563eb; -fx-padding: 4px;");
        actualiser(model);
    }

    @Override
    public void actualiser(Sujet s) {
        this.getChildren().clear();
        navButtons.clear();
        HBox hbox = new HBox();

        if (model.getFilepath() != null) {
            Label fileText = new Label("Title not found");
            fileText.setTextFill(javafx.scene.paint.Color.WHITE);
            fileText.setStyle("-fx-font-size: 22");
            try {
                fileText.setText(new File(model.getFilepath()).getName());
                this.setLeft(fileText);
            } catch (Exception ex) {
                this.setLeft(fileText);
            }

            Button trelloBtn = new Button();
            trelloBtn.setGraphic(createIcon("file:icons/trello-brands-solid-full.png"));
            trelloBtn.setStyle("-fx-background-color: transparent;");

            Button listBtn = new Button();
            listBtn.setGraphic(createIcon("file:icons/list-check-solid-full.png"));
            listBtn.setStyle("-fx-background-color: transparent;");

            Button ganttBtn = new Button();
            ganttBtn.setGraphic(createIcon("file:icons/chart-gantt-solid-full.png"));
            ganttBtn.setStyle("-fx-background-color: transparent;");

            navButtons.add(trelloBtn);
            navButtons.add(listBtn);
            navButtons.add(ganttBtn);
            trelloBtn.setOnAction(e -> setActiveButton(trelloBtn));
            listBtn.setOnAction(e -> setActiveButton(listBtn));
            ganttBtn.setOnAction(e -> setActiveButton(ganttBtn));

            hbox.getChildren().addAll(trelloBtn, listBtn, ganttBtn);
        }

        Button homeBtn = new Button();
        homeBtn.setGraphic(createIcon("file:icons/house-regular-full.png"));
        homeBtn.setStyle("-fx-background-color: #1d4ed8; -fx-background-radius: 8px;");

        navButtons.add(homeBtn);
        homeBtn.setOnAction(e -> setActiveButton(homeBtn));

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

    private void setActiveButton(Button activeBtn) {
        for (Button btn : navButtons) {
            if (btn == activeBtn) {
                btn.setStyle("-fx-background-color: #1d4ed8; -fx-background-radius: 8px;");
            } else {
                btn.setStyle("-fx-background-color: transparent;");
            }
        }
    }
}