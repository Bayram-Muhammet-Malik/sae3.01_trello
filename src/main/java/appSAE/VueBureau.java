package appSAE;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class VueBureau extends ScrollPane implements Observateur {
    private final Model model;
    private Liste listeDragEnCours;

    public VueBureau(Model model) {
        this.model = model;
        this.setFitToHeight(true);
        this.setStyle("-fx-background-color: transparent;");
    }

    @Override
    public void actualiser(Sujet sujet) {
        HBox hb = new HBox(20);
        for (Liste ls : model.getListes()) hb.getChildren().add(creerColonne(ls));
        this.setContent(hb);
    }

    private VBox creerColonne(Liste ls) {
        VBox colonne = new VBox(5);
        colonne.setPrefWidth(320);

        colonne.setOnDragOver(e -> {
            if (ControlerDrag.tacheEnCours != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        colonne.setOnDragDropped(e -> {
            if (ControlerDrag.tacheEnCours != null) {
                model.deplacerTache(ls, ControlerDrag.tacheEnCours);
                ControlerDrag.tacheEnCours = null;
                e.setDropCompleted(true);
            } else {
                e.setDropCompleted(false);
            }
            e.consume();
        });

        Label titre = new Label(ls.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        titre.setOnMouseClicked(e -> MainWindow.ouvrirPopupListe(ls, model));

        ImageView deleteIcon = creerIconeSuppression();
        deleteIcon.setOnMouseClicked(e -> {
            e.consume();
            supprimerListe(ls);
        });

        HBox header = new HBox(5, deleteIcon, titre);
        header.setAlignment(Pos.CENTER_LEFT);

        // Drag&drop liste
        header.setOnDragDetected(e -> {
            Dragboard db = header.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent cc = new ClipboardContent();
            cc.putString("LISTE");
            db.setContent(cc);
            listeDragEnCours = ls;
            e.consume();
        });

        header.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString() && "LISTE".equals(db.getString()) && listeDragEnCours != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        header.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString() && "LISTE".equals(db.getString()) && listeDragEnCours != null) {
                HBox parent = (HBox) colonne.getParent();
                int nouvelIndex = parent.getChildren().indexOf(colonne);
                model.deplacerListe(listeDragEnCours, nouvelIndex);
                listeDragEnCours = null;
                e.setDropCompleted(true);
            } else {
                e.setDropCompleted(false);
            }
            e.consume();
        });

        header.setOnDragDone(e -> {
            listeDragEnCours = null;
            e.consume();
        });

        VBox cartesBox = new VBox(5);
        for (Tache t : model.getTachesFromListe(ls)) cartesBox.getChildren().add(creerTache(ls, t, 0));
        ScrollPane scrollCartes = new ScrollPane(cartesBox);
        scrollCartes.setFitToWidth(true);
        scrollCartes.setMinHeight(0);
        scrollCartes.setStyle("-fx-background-color: transparent;");

        Button creerTacheBtn = new Button("+ Créer une tâche");
        creerTacheBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e5e7eb; -fx-background-radius: 8px;");
        creerTacheBtn.setOnAction(e -> ouvrirPopUpTache(ls, null, null));

        colonne.getChildren().addAll(header, scrollCartes, creerTacheBtn);
        return colonne;
    }

    private VBox creerTache(Liste liste, Tache tsk, int profondeur) {
        VBox carte = new VBox(6);
        carte.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-background-radius: 10px; -fx-border-color: #e5e7eb; -fx-border-radius: 10px;");

        ControlerDrag cd = new ControlerDrag(model, tsk, liste, carte);

        carte.setOnDragDetected(cd::handleDragDetected);
        carte.setOnDragOver(cd::handleDragOver);
        carte.setOnDragEntered(cd::handleDragEntered);
        carte.setOnDragExited(cd::handleDragExited);
        carte.setOnDragDropped(cd::handleDragDropped);
        carte.setOnDragDone(cd::setOnDragDone);


        HBox ligneHaut = new HBox(8);
        ligneHaut.setAlignment(Pos.CENTER_LEFT);

        Label titre = new Label(tsk.getTitre());
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        Tache.Priorite prio = (tsk.getPriorite() == null) ? Tache.Priorite.NORMAL : tsk.getPriorite();
        Label badge = new Label(prio.getLabel());
        String styleBase = "-fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 999; -fx-font-size: 11px; -fx-font-weight: bold;";
        badge.setStyle(
                switch (prio) {
                    case NORMAL -> "-fx-background-color: #93c47d;" + styleBase;
                    case IMPORTANT -> "-fx-background-color: #ffbb42;" + styleBase;
                    case URGENT -> "-fx-background-color: #ef4444;" + styleBase;
                }
        );

        ImageView deleteIcon = creerIconeSuppression();

        ligneHaut.getChildren().addAll(titre, espace, badge, deleteIcon);

        Label description = new Label(tsk.getDescription() == null ? "" : tsk.getDescription());
        description.setWrapText(true);

        Label dates = new Label("Du " + tsk.getDebut().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.FRENCH)) + " au " + tsk.getFin().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.FRENCH)));

        Button cst = new Button("+ Créer une sous tâche");
        cst.setVisible(false);
        cst.setManaged(false);
        cst.setOnMouseClicked(e -> ouvrirPopUpTache(liste, null, tsk));

        carte.setOnMouseEntered(e -> { cst.setVisible(true); cst.setManaged(true); });
        carte.setOnMouseExited(e -> { cst.setVisible(false); cst.setManaged(false); });

        VBox sousTachesBox = new VBox(6);
        if (tsk instanceof CompositeTache ct) {
            for (Tache sousTache : ct.getTaches()) {
                VBox sousCarte = creerTache(liste, sousTache, profondeur + 1);
                sousCarte.setTranslateX(profondeur * 2);
                sousTachesBox.getChildren().add(sousCarte);
            }
        }

        carte.getChildren().addAll(ligneHaut, description, dates, cst, sousTachesBox);

        carte.setOnMouseClicked(e -> {
            Node source = (Node) e.getTarget();

            if (source == deleteIcon) {
                e.consume();
                supprimerTache(liste, tsk);
                return;
            }

            ouvrirPopUpTache(liste, tsk, null);
        });
        return carte;
    }

    // popup création (tacheAModifier == null) ou modification
    private void ouvrirPopUpTache(Liste liste, Tache tacheAModifier, Tache parentTache) {
        boolean modeModification = (tacheAModifier != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(modeModification ? "Modifier une tâche" : "Créer une tâche");

        ButtonType btnValider = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, btnAnnuler);

        // Titre + desc
        TextField champTitre = new TextField(modeModification ? tacheAModifier.getTitre() : "");
        champTitre.setPromptText("Nom de la tâche");

        TextArea champDescription = new TextArea(modeModification ? (tacheAModifier.getDescription() == null ? "" : tacheAModifier.getDescription()) : "");
        champDescription.setPromptText("Description");
        champDescription.setPrefRowCount(3);
        champDescription.setWrapText(true);

        // Date/Heure début et fin
        DatePicker dateDebut = new DatePicker(LocalDate.now());
        DatePicker dateFin   = new DatePicker(LocalDate.now());
        ComboBox<Integer> hDeb = new ComboBox<>(), mDeb = new ComboBox<>();
        ComboBox<Integer> hFin = new ComboBox<>(), mFin = new ComboBox<>();

        for (int h = 0; h < 24; h++) {
            hDeb.getItems().add(h);
            hFin.getItems().add(h);
        }
        for (int m = 0; m < 60; m += 5) {
            mDeb.getItems().add(m);
            mFin.getItems().add(m);
        }

        if (modeModification) {
            LocalDateTime deb = tacheAModifier.getDebut();
            LocalDateTime fin = tacheAModifier.getFin();

            if (deb != null) {
                dateDebut.setValue(deb.toLocalDate());
                hDeb.setValue(deb.getHour());
                mDeb.setValue(deb.getMinute());
            }

            if (fin != null) {
                dateFin.setValue(fin.toLocalDate());
                hFin.setValue(fin.getHour());
                mFin.setValue(fin.getMinute());
            }
        } else {
            int heure = LocalTime.now().getHour();
            int minute = LocalTime.now().getMinute() - (LocalTime.now().getMinute() % 5);

            hDeb.setValue(heure);
            mDeb.setValue(minute);
            hFin.setValue(heure);
            mFin.setValue(minute);
        }

        // Priorité de la tâche
        ComboBox<Tache.Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().addAll(Tache.Priorite.values());
        champPriorite.setValue(modeModification ? (tacheAModifier.getPriorite() == null ? Tache.Priorite.NORMAL : tacheAModifier.getPriorite()) : Tache.Priorite.NORMAL
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Titre"), champTitre);
        grid.addRow(1, new Label("Description"), champDescription);
        grid.addRow(2, new Label("Début"), new HBox(5, dateDebut, hDeb, new Label(":"), mDeb));
        grid.addRow(3, new Label("Fin"), new HBox(5, dateFin, hFin, new Label(":"), mFin));
        grid.addRow(4, new Label("Priorité"), champPriorite);

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

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == btnValider) new ControlerPopTache(model, liste, champTitre, champDescription, LocalDateTime.of(dateDebut.getValue(), LocalTime.of(hDeb.getValue(), mDeb.getValue())), LocalDateTime.of(dateFin.getValue(), LocalTime.of(hFin.getValue(), mFin.getValue())), champPriorite, tacheAModifier, parentTache).handle(new ActionEvent());
        });
    }

    private void supprimerListe(Liste liste) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Supprimer");

        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        Label texte = new Label("Supprimer la liste : " + liste.getTitre() + " ?\n" + "Toutes les tâches seront supprimées.");
        texte.setWrapText(true);

        VBox content = new VBox(10, texte);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ok) {
                model.supprimerListe(liste);
                FichierManager.sauvegarder(model, model.getFilepath());
            }
        });
    }

    private void supprimerTache(Liste liste, Tache tache) {
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
                if (tache.getParentTache() != null && tache.getParentTache() instanceof CompositeTache parent) {
                    model.supprimerSousTache(parent, tache);
                } else {
                    model.supprimerTache(liste, tache);
                }

                FichierManager.sauvegarder(model, model.getFilepath());
            }
        });
    }

    private ImageView creerIconeSuppression() {
        ImageView icone = new ImageView("file:icons/trash-can-solid-full.png");
        icone.setFitWidth(20);
        icone.setFitHeight(20);
        icone.setPickOnBounds(true);
        return icone;
    }
}
