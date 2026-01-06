package appSAE;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

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
                model.deplacerTacheDansListe(ls, ControlerDrag.tacheEnCours);
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
        Tooltip.install(deleteIcon, new Tooltip("Supprimer"));
        deleteIcon.setOnMouseClicked(e -> {
            e.consume();
            Popup.supprimerListe(ls, model);
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
        creerTacheBtn.setOnAction(e -> Popup.ouvrirPopUpTache(ls, null, null, model));

        colonne.getChildren().addAll(header, scrollCartes, creerTacheBtn);
        return colonne;
    }

    private VBox creerTache(Liste liste, Tache tsk, int profondeur) {
        VBox carte = new VBox(6);
        VBox content = new VBox(6);

        String couleurFond;
        if (profondeur % 2 == 0){
            couleurFond = "#ffffff";
        } else {
            couleurFond = "#f3f4f6";
        }
        carte.setStyle("-fx-background-color: " + couleurFond + "; -fx-padding: 12px; -fx-background-radius: 10px; -fx-border-color: #e5e7eb; -fx-border-radius: 10px;");

        ControlerDrag cd = new ControlerDrag(model, tsk, liste, carte);
        carte.setOnDragDetected(cd::handleDragDetected);
        carte.setOnDragOver(cd::handleDragOver);
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
        Tooltip.install(deleteIcon, new Tooltip("Supprimer"));
        ligneHaut.getChildren().addAll(titre, espace, badge, deleteIcon);

        Label description = new Label(tsk.getDescription() == null ? "" : tsk.getDescription());
        description.setWrapText(true);

        Label dates = new Label("Du " + tsk.getDebut().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.FRENCH)) + " au " + tsk.getFin().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.FRENCH)));

        Button cst = new Button("+ Créer une sous tâche");
        cst.setVisible(false);
        cst.setManaged(false);
        cst.setOnMouseClicked(e -> Popup.ouvrirPopUpTache(liste, null, tsk, model));

        content.hoverProperty().addListener((obs, oldVal, isHovering) -> {
            if (ControlerDrag.tacheEnCours != null) return;
            cst.setVisible(isHovering);
            cst.setManaged(isHovering);
        });

        VBox sousTachesBox = new VBox(6);
        if (tsk instanceof CompositeTache ct) {
            for (Tache sousTache : ct.getTaches()) {
                VBox sousCarte = creerTache(liste, sousTache, profondeur + 1);
                sousCarte.setTranslateX(profondeur * 2);
                sousTachesBox.getChildren().add(sousCarte);
            }
        }

        content.getChildren().addAll(ligneHaut, description, dates, cst);

        content.setOnMouseClicked(e -> {
            Node source = (Node) e.getTarget();
            e.consume();

            if (source == deleteIcon) {
                Popup.supprimerTache(liste, tsk, model);
                return;
            }

            Popup.ouvrirPopUpTache(liste, tsk, null, model);
        });
        carte.getChildren().addAll(content, sousTachesBox);
        return carte;
    }

    private ImageView creerIconeSuppression() {
        ImageView icone = new ImageView("file:icons/trash-can-solid-full.png");
        icone.setFitWidth(20);
        icone.setFitHeight(20);
        icone.setPickOnBounds(true);
        return icone;
    }
}
