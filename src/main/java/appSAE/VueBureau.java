package appSAE;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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
        creerTacheBtn.setOnAction(e -> ouvrirPopUpTache(ls, null)); // null = mode création

        colonne.getChildren().add(titre);

        for (Tache t : model.getTachesFromListe(ls)) {
            colonne.getChildren().add(creerTache(ls, t));
        }

        colonne.getChildren().add(creerTacheBtn);
        return colonne;
    }

    private VBox creerTache(Liste liste, Tache tsk) {
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

        // clic sur une tâche => popup en mode modification
        carte.setOnMouseClicked(e -> {
            if (tsk instanceof CompositeTache ct) {
                ouvrirPopUpTache(liste, ct);
            }
        });

        return carte;
    }

    // popup unique : création (tacheAModifier == null) ou modification (sinon)
    private void ouvrirPopUpTache(Liste liste, CompositeTache tacheAModifier) {

        boolean modeModification = (tacheAModifier != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(modeModification ? "modifier une tâche" : "créer une tâche");

        ButtonType btnValider = new ButtonType(
                modeModification ? "enregistrer" : "créer",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType btnAnnuler = new ButtonType("annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, btnAnnuler);

        TextField champTitre = new TextField(modeModification ? tacheAModifier.getTitre() : "");
        champTitre.setPromptText("nom de la tâche");

        TextArea champDescription = new TextArea(
                modeModification ? (tacheAModifier.getDescription() == null ? "" : tacheAModifier.getDescription()) : ""
        );
        champDescription.setPromptText("description");
        champDescription.setPrefRowCount(3);
        champDescription.setWrapText(true);

        DatePicker champDate = new DatePicker(LocalDate.now());
        if (modeModification) {
            try {
                champDate.setValue(LocalDate.parse(
                        tacheAModifier.getDate(),
                        java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
                ));
            } catch (Exception ignored) {}
        }

        ComboBox<Tache.Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().addAll(Tache.Priorite.values());
        champPriorite.setValue(
                modeModification
                        ? (tacheAModifier.getPriorite() == null ? Tache.Priorite.NORMAL : tacheAModifier.getPriorite())
                        : Tache.Priorite.NORMAL
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getColumnConstraints().addAll(
                new ColumnConstraints(90),
                new ColumnConstraints() {{ setHgrow(Priority.ALWAYS); }}
        );

        grid.addRow(0, new Label("titre"), champTitre);
        grid.addRow(1, new Label("description"), champDescription);
        grid.addRow(2, new Label("date"), champDate);
        grid.addRow(3, new Label("priorité"), champPriorite);

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(grid);

        Button bValider = (Button) pane.lookupButton(btnValider);
        bValider.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14;");

        Button bAnnuler = (Button) pane.lookupButton(btnAnnuler);
        bAnnuler.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 8 14;");

        bValider.setDisable(champTitre.getText() == null || champTitre.getText().isBlank());
        champTitre.textProperty().addListener((obs, oldV, newV) ->
                bValider.setDisable(newV == null || newV.isBlank())
        );

        dialog.showAndWait().ifPresent(result -> {
            if (result != btnValider) return;

            String dateStr = champDate.getValue().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            Tache.Priorite prio = (champPriorite.getValue() == null) ? Tache.Priorite.NORMAL : champPriorite.getValue();

            if (!modeModification) {
                CompositeTache nouvelle = new CompositeTache(
                        champTitre.getText().trim(),
                        champDescription.getText(),
                        dateStr,
                        prio
                );
                liste.ajouterCarte(nouvelle);
            } else {
                tacheAModifier.titre = champTitre.getText().trim();
                tacheAModifier.description = champDescription.getText();
                tacheAModifier.date = dateStr;
                tacheAModifier.setPriorite(prio);
            }

            model.notifierObservateur();
            FichierManager.sauvegarder(model, model.getFilepath());
        });
    }
}
