package appSAE;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class VueBureau extends HBox implements Observateur {
    private final Model model;

    public VueBureau(Model model) {
        this.model = model;
        this.setSpacing(5);
    }

    @Override
    public void actualiser(Sujet sujet) {
        this.getChildren().clear();

        for (Liste ls : model.getListes()) {
            this.getChildren().add(creerColonne(ls));
        }
    }

    private VBox creerColonne(Liste ls) {
        VBox colonne = new VBox();
        colonne.setSpacing(5);
        colonne.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 12px; -fx-background-radius: 8px;");
        colonne.setPrefWidth(325);

        Label titre = new Label(ls.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Button creerTacheBtn = new Button("+ Créer une tâche");
        creerTacheBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e5e7eb; -fx-background-radius: 8px;");
        creerTacheBtn.setOnAction(e -> ouvrirPopUpTache(ls));

        colonne.getChildren().add(titre);

        for (Tache t : model.getTachesFromListe(ls)) {
            colonne.getChildren().add(creerTache(t));
        }

        colonne.getChildren().add(creerTacheBtn);
        return colonne;
    }

    private VBox creerTache(Tache tsk) {
        VBox carte = new VBox(6);
        carte.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-background-radius: 10px; -fx-border-color: #e5e7eb; -fx-border-radius: 10px;");

        HBox ligneHaut = new HBox(8);
        ligneHaut.setAlignment(Pos.CENTER_LEFT);

        Label titre = new Label(tsk.getTitre());
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        Tache.Priorite prio = (tsk.getPriorite() == null) ? Tache.Priorite.NORMAL : tsk.getPriorite();
        Label badge = new Label(
                switch (prio) {
                    case NORMAL -> "Normal";
                    case SECONDAIRE -> "Important";
                    case URGENT -> "Urgent";
                }
        );
        String styleBase = "-fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 999; -fx-font-size: 11px; -fx-font-weight: bold;";
        badge.setStyle(
                switch (prio) {
                    case NORMAL -> "-fx-background-color: #93c47d;" + styleBase;
                    case SECONDAIRE -> "-fx-background-color: #ffbb42;" + styleBase;
                    case URGENT -> "-fx-background-color: #ef4444;" + styleBase;
                }
        );

        ligneHaut.getChildren().addAll(titre, espace, badge);

        Label description = new Label(tsk.getDescription() == null ? "" : tsk.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");

        carte.getChildren().addAll(ligneHaut, description);

        return carte;
    }

    private void ouvrirPopUpTache(Liste liste) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Créer une tâche");

        ButtonType btnCreer = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCreer, btnAnnuler);

        TextField champTitre = new TextField();
        champTitre.setPromptText("Nom de la tâche");

        TextArea champDescription = new TextArea();
        champDescription.setPromptText("Description");
        champDescription.setPrefRowCount(3);
        champDescription.setWrapText(true);

        DatePicker champDate = new DatePicker(LocalDate.now());
        ComboBox<Tache.Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().addAll(Tache.Priorite.values());
        champPriorite.setValue(Tache.Priorite.NORMAL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getColumnConstraints().addAll(new ColumnConstraints(90), new ColumnConstraints() {{ setHgrow(Priority.ALWAYS); }});

        grid.addRow(0, new Label("Titre"), champTitre);
        grid.addRow(1, new Label("Description"), champDescription);
        grid.addRow(2, new Label("Date"), champDate);
        grid.addRow(3, new Label("Priorité"), champPriorite);

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(grid);

        Button bCreer = (Button) pane.lookupButton(btnCreer);
        bCreer.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14;");
        bCreer.setDisable(true);
        champTitre.textProperty().addListener((obs, oldV, newV) -> bCreer.setDisable(newV == null || newV.isBlank()));

        ((Button) pane.lookupButton(btnAnnuler)).setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 8 14;");

        bCreer.setOnAction(new ControlerPopTache(model, liste, champTitre, champDescription, champDate, champPriorite, (Stage) dialog.getDialogPane().getScene().getWindow()));

        dialog.showAndWait();
    }
}
