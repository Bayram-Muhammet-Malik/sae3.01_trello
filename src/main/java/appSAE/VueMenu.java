package appSAE;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.ArrayList;

public class VueMenu extends BorderPane implements Observateur {

    private final Model model;
    private final MainWindow mainWindow; // <-- pour changer de vue
    private final ArrayList<Button> navButtons = new ArrayList<>();

    // <-- constructeur modifié : on passe MainWindow
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

        HBox hbox = new HBox(10);

        // si un fichier est ouvert, on affiche les 3 boutons (bureau / liste / gantt)
        ControlerMenu controller = new ControlerMenu(model, mainWindow);

        // si un fichier est ouvert
        if (model.getFilepath() != null) {

            Label fileText = new Label("Title not found");
            fileText.setTextFill(javafx.scene.paint.Color.WHITE);
            fileText.setStyle("-fx-font-size: 22");

            fileText.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");
            try {
                fileText.setText(new File(model.getFilepath()).getName());
                this.setLeft(fileText);
            } catch (Exception ex) {
                this.setLeft(fileText);
            }

            Button trelloBtn = new Button();
            trelloBtn.setId("BUREAU");
            trelloBtn.setGraphic(createIcon("file:icons/trello-brands-solid-full.png"));
            trelloBtn.setStyle("-fx-background-color: transparent;");
            trelloBtn.setOnAction(e -> {
                setActiveButton(trelloBtn);
                mainWindow.switchView("BUREAU");
            });

            Button listBtn = new Button();
            listBtn.setId("LISTE");
            listBtn.setGraphic(createIcon("file:icons/list-check-solid-full.png"));
            listBtn.setStyle("-fx-background-color: transparent;");
            listBtn.setOnAction(e -> {
                setActiveButton(listBtn);
                mainWindow.switchView("LISTE");
            });

            Button ganttBtn = new Button();
            ganttBtn.setId("GANTT");
            ganttBtn.setGraphic(createIcon("file:icons/chart-gantt-solid-full.png"));
            ganttBtn.setStyle("-fx-background-color: transparent;");
            ganttBtn.setOnAction(e -> {
                setActiveButton(ganttBtn);
                mainWindow.switchView("GANTT");
            });

            navButtons.add(trelloBtn);
            navButtons.add(listBtn);
            navButtons.add(ganttBtn);

            hbox.getChildren().addAll(trelloBtn, listBtn, ganttBtn);

            // ===== bouton "+ créer une liste" (repris tel quel) =====
            Button creerListeBtn = new Button("+ Créer une liste");
            creerListeBtn.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: #e5e7eb;" +
                            "-fx-background-radius: 8px;"
            );
            creerListeBtn.setOnAction(e -> mainWindow.ouvrirPopupCreationTache());

            hbox.getChildren().add(creerListeBtn);
        }

        // bouton home toujours présent
        Button homeBtn = new Button();
        homeBtn.setId("HOME");
        homeBtn.setGraphic(createIcon("file:icons/house-regular-full.png"));
        homeBtn.setStyle("-fx-background-color: #1d4ed8; -fx-background-radius: 8px;");
        homeBtn.setOnAction(e -> {
            setActiveButton(homeBtn);
            mainWindow.switchView("HOME");
        });

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

    public void setActiveButton(Button boutonActif) {
        for (Button btn : navButtons) {
            if (btn == boutonActif) {
                btn.setStyle("-fx-background-color: #1d4ed8; -fx-background-radius: 8px;");
            } else {
                btn.setStyle("-fx-background-color: transparent;");
            }
        }
    }
}