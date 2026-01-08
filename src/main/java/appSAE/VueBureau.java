package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Affiche le "bureau" avec les listes et leurs tâches
public class VueBureau extends ScrollPane implements Observateur {
    private final Model model;
    // Liste qu'on est en train de déplacer
    private Liste listeDragEnCours;

    public VueBureau(Model model) {
        this.model = model;
        this.setFitToHeight(true);
        this.setStyle("-fx-background-color: transparent;");
    }

    // Quand le model change, on reconstruit l'écran
    @Override
    public void actualiser(Sujet sujet) {
        HBox hb = new HBox(20);

        Button creerListeBtn = creerBoutton(
                "+ Créer une liste",
                e -> Popup.ouvrirPopupListe(null, model)
        );

        for (Liste ls : model.getListes()) {
            hb.getChildren().add(creerColonne(ls));
        }

        hb.getChildren().add(creerListeBtn);
        this.setContent(hb);
    }

    // Crée l'affichage d'une liste (colonne)
    private VBox creerColonne(Liste ls) {
        VBox colonne = new VBox(5);
        colonne.setPrefWidth(320);

        // Déposer une tâche dans cette liste
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
        // Cliquer sur le titre pour renommer la liste
        titre.setOnMouseClicked(e -> Popup.ouvrirPopupListe(ls, model));

        ImageView deleteIcon = creerIconeSuppression();
        Tooltip.install(deleteIcon, new Tooltip("Supprimer"));
        // Poubelle pour supprimer la liste
        deleteIcon.setOnMouseClicked(e -> {
            e.consume();
            Popup.supprimerListe(ls, model);
        });

        HBox header = new HBox(5, deleteIcon, titre);
        header.setAlignment(Pos.CENTER_LEFT);

        // Déplacement des listes (drag & drop des colonnes)
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
        // Ajoute les cartes de la liste
        for (Tache t : model.getTachesFromListe(ls)) {
            cartesBox.getChildren().add(creerTache(ls, t, 0));
        }

        ScrollPane scrollCartes = new ScrollPane(cartesBox);
        scrollCartes.setFitToWidth(true);
        scrollCartes.setFitToHeight(true);
        scrollCartes.setMinHeight(0);
        scrollCartes.setStyle("-fx-background-color: transparent;");

        Button creerTacheBtn = creerBoutton(
                "+ Créer une tâche",
                e -> Popup.ouvrirPopUpTache(ls, null, null, model)
        );

        colonne.getChildren().addAll(header, scrollCartes, creerTacheBtn);
        return colonne;
    }

    // Crée une carte pour une tâche (avec la priorité, dates, sous-tâches, etc.)
    private VBox creerTache(Liste liste, Tache tsk, int profondeur) {
        VBox carte = new VBox(6);
        carte.setStyle(
                "-fx-background-color: " +
                        (profondeur % 2 == 0 ? "#ffffff" : "#f3f4f6") +
                        "; -fx-padding: 10px; -fx-background-radius: 10px; " +
                        "-fx-border-color: #e5e7eb; -fx-border-radius: 10px;"
        );

        // Drag & drop d’une tâche
        ControlerDrag cd = new ControlerDrag(model, tsk, liste, carte);
        carte.setOnDragDetected(cd::handleDragDetected);
        carte.setOnDragOver(cd::handleDragOver);
        carte.setOnDragDropped(cd::handleDragDropped);
        carte.setOnDragDone(cd::setOnDragDone);

        VBox content = new VBox();

        HBox ligneHaut = new HBox(8);
        ligneHaut.setAlignment(Pos.CENTER_LEFT);

        // Case à cocher pour marquer la tâche comme faite
        CheckBox fait = new CheckBox();
        fait.setSelected(tsk.estFait());
        fait.selectedProperty().addListener((obs, oldVal, newVal) -> {
            model.setTacheFait(tsk, newVal);
        });

        Label titre = new Label(tsk.getTitre());
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        // Affiche la priorité sous forme de badge
        Tache.Priorite prio = (tsk.getPriorite() == null)
                ? Tache.Priorite.NORMAL
                : tsk.getPriorite();

        Label badge = new Label(prio.getLabel());
        String styleBase =
                "-fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 999; " +
                        "-fx-font-size: 11px; -fx-font-weight: bold;";
        badge.setStyle(
                switch (prio) {
                    case NORMAL -> "-fx-background-color: #93c47d;" + styleBase;
                    case IMPORTANT -> "-fx-background-color: #ffbb42;" + styleBase;
                    case URGENT -> "-fx-background-color: #ef4444;" + styleBase;
                }
        );

        ImageView deleteIcon = creerIconeSuppression();
        Tooltip.install(deleteIcon, new Tooltip("Supprimer"));

        ligneHaut.getChildren().addAll(fait, titre, espace, badge, deleteIcon);

        Label description = new Label(tsk.getDescription() == null ? "" : tsk.getDescription());
        description.setWrapText(true);

        Label dates = new Label(
                "Du " +
                        tsk.getDebut().format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH)) +
                        " au " +
                        tsk.getFin().format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH))
        );

        // Label "dépend de" (affiché seulement si la tâche a une dépendance)
        Label dependDeLabel = new Label();
        if (tsk.getPrerequise() != null) {
            dependDeLabel.setText("Dépend de : " + tsk.getPrerequise().getTitre());
            dependDeLabel.setVisible(true);
            dependDeLabel.setManaged(true);
        } else {
            dependDeLabel.setVisible(false);
            dependDeLabel.setManaged(false);
        }

        // Bouton "Créer une sous-tâche" (visible au survol)
        Button cst = creerBoutton(
                "+ Créer une sous tâche",
                e -> Popup.ouvrirPopUpTache(liste, null, tsk, model)
        );
        cst.setVisible(false);
        cst.setManaged(false);

        content.hoverProperty().addListener((obs, oldVal, isHovering) -> {
            if (ControlerDrag.tacheEnCours != null) return;
            cst.setVisible(isHovering);
            cst.setManaged(isHovering);
        });

        VBox sousTachesBox = new VBox(6);
        // Si c’est une tâche qui contient d'autres tâches, on affiche les sous-tâches
        if (tsk instanceof CompositeTache ct) {
            for (Tache sousTache : ct.getTaches()) {
                VBox sousCarte = creerTache(liste, sousTache, profondeur + 1);
                sousCarte.setTranslateX(profondeur * 2);
                sousTachesBox.getChildren().add(sousCarte);
            }
        }

        // dates sur une ligne, puis la dépendance juste en dessous
        content.getChildren().addAll(ligneHaut, description, dates, dependDeLabel, cst);

        // Clic sur la carte :
        // - si on clique sur la poubelle -> supprimer
        // - sinon -> ouvrir la fenêtre d’édition
        content.setOnMouseClicked(e -> {
            Node source = (Node) e.getTarget();
            e.consume();

            if (source == deleteIcon) {
                Popup.supprimerTache(liste, tsk, model);
                return;
            }

            Popup.ouvrirPopUpTache(liste, tsk, tsk.getParentTache(), model);
        });

        carte.getChildren().addAll(content, sousTachesBox);
        return carte;
    }

    // Renvoie une icône de poubelle
    private ImageView creerIconeSuppression() {
        ImageView icone = new ImageView("file:icons/trash-can-solid-full.png");
        icone.setFitWidth(20);
        icone.setFitHeight(20);
        icone.setPickOnBounds(true);
        return icone;
    }

    // Crée un bouton avec le style de l’appli
    private Button creerBoutton(String text, EventHandler<ActionEvent> action){
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-color: #e5e7eb; -fx-background-radius: 8px;"
        );
        btn.setOnAction(action);
        return btn;
    }
}
