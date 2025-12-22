package appSAE;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
        VBox colonne = new VBox(6);
        colonne.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 12px; -fx-background-radius: 8px;");
        colonne.setPrefWidth(325);

        Label titre = new Label(ls.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        titre.setOnMouseClicked(e -> MainWindow.ouvrirPopupListe(ls, model));

        ImageView deleteIcon = creerIconeSuppression();
        deleteIcon.setOnMouseClicked(e -> {
            e.consume();
            supprimerListe(ls);
        });

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox header = new HBox(6, deleteIcon, titre, espace);
        header.setAlignment(Pos.CENTER_LEFT);

        colonne.getChildren().add(header);

        for (Tache t : model.getTachesFromListe(ls)) {
            colonne.getChildren().add(creerTache(ls, t));
        }

        Button creerTacheBtn = new Button("+ Créer une tâche");
        creerTacheBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e5e7eb; -fx-background-radius: 8px;");
        creerTacheBtn.setOnAction(e -> ouvrirPopUpTache(ls, null));

        colonne.getChildren().add(creerTacheBtn);
        return colonne;
    }


    private VBox creerTache(Liste liste, Tache tsk) {
        VBox carte = new VBox(6);
        carte.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-background-radius: 10px; -fx-border-color: #e5e7eb; -fx-border-radius: 10px;");

        ControlerDrag cd = new ControlerDrag(model, tsk);

        carte.setOnDragOver(e -> cd.handleDrag(EtatDrag.OVER, e, carte));
        carte.setOnDragEntered(e -> cd.handleDrag(EtatDrag.ENTERED, e, carte));
        carte.setOnDragExited(e -> cd.handleDrag(EtatDrag.EXITED, e, carte));
        carte.setOnDragDropped(e -> cd.handleDrag(EtatDrag.DROPPED, e, carte));

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

        ImageView deleteIcon = creerIconeSuppression();
        deleteIcon.setOnMouseClicked(e -> {
            e.consume();
            if (tsk instanceof CompositeTache ct) {
                supprimerTache(liste, ct);
            }
        });

        ligneHaut.getChildren().addAll(titre, espace, badge, deleteIcon);

        Label description = new Label(tsk.getDescription() == null ? "" : tsk.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;");

        carte.getChildren().addAll(ligneHaut, description);

        carte.setOnMouseClicked(e -> {
            if (tsk instanceof CompositeTache ct) {
                ouvrirPopUpTache(liste, ct);
            }
        });

        return carte;
    }


    // popup  création (tacheAModifier == null) ou modification
    private void ouvrirPopUpTache(Liste liste, CompositeTache tacheAModifier) {
        boolean modeModification = (tacheAModifier != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(modeModification ? "modifier une tâche" : "créer une tâche");

        ButtonType btnValider = new ButtonType(modeModification ? "enregistrer" : "créer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, btnAnnuler);

        TextField champTitre = new TextField(modeModification ? tacheAModifier.getTitre() : "");
        champTitre.setPromptText("nom de la tâche");

        TextArea champDescription = new TextArea(modeModification ? (tacheAModifier.getDescription() == null ? "" : tacheAModifier.getDescription()) : "");
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
        champPriorite.setValue(modeModification ? (tacheAModifier.getPriorite() == null ? Tache.Priorite.NORMAL : tacheAModifier.getPriorite()) : Tache.Priorite.NORMAL
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

        dialog.setResultConverter(button -> {
            if (button == btnValider) new ControlerPopTache(model, liste, champTitre, champDescription, champDate, champPriorite, modeModification, tacheAModifier).handle(new ActionEvent());
            return button;
        });

        dialog.showAndWait();
    }

    private void supprimerListe(Liste liste) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Supprimer");

        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        Label texte = new Label(
                "Supprimer la liste : " + liste.getTitre() + " ?\n" +
                        "Toutes les tâches seront supprimées."
        );
        texte.setWrapText(true);

        VBox content = new VBox(10, texte);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ok) {
                model.supprimerListe(liste);
                FichierManager.sauvegarder(model, model.getFilepath());

                Dialog<ButtonType> info = new Dialog<>();
                info.setTitle("Information");
                info.getDialogPane().getButtonTypes().add(ButtonType.OK);
                info.getDialogPane().setContent(new Label("Suppression effectuée."));
                info.showAndWait();
            }
        });
    }


    private void supprimerTache(Liste liste, CompositeTache tache) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Supprimer");

        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        Label texte = new Label("Supprimer la tâche : " + tache.getTitre() + " ?");
        texte.setWrapText(true);

        VBox content = new VBox(10, texte);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ok) {
                model.supprimerTache(liste, tache);
                FichierManager.sauvegarder(model, model.getFilepath());

                Dialog<ButtonType> info = new Dialog<>();
                info.setTitle("Information");
                info.getDialogPane().getButtonTypes().add(ButtonType.OK);
                info.getDialogPane().setContent(new Label("Suppression effectuée."));
                info.showAndWait();
            }
        });
    }



    private ImageView creerIconeSuppression() {
        ImageView icone = new ImageView("file:icons/delete.png");
        icone.setFitWidth(16);
        icone.setFitHeight(16);
        icone.setPreserveRatio(true);
        icone.setPickOnBounds(true);
        icone.setStyle("-fx-cursor: hand;");
        return icone;
    }


}
