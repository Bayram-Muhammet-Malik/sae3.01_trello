package appSAE;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class Popup {

    // Ouvre une fenêtre pour créer ou modifier une tâche (ou une sous-tâche)
    public static void ouvrirPopUpTache(Liste liste, Tache tacheAModifier, Tache parentTache, Model model) {
        // modeModification = true si on modifie une tâche, false si on en crée une nouvelle
        boolean modeModification = (tacheAModifier != null);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(modeModification ? "Modifier une tâche" : "Créer une tâche");

        ButtonType btnValider = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, btnAnnuler);

        // Champs pour le titre et la description
        TextField champTitre = new TextField(modeModification ? tacheAModifier.getTitre() : "");
        champTitre.setPromptText("Nom de la tâche");

        // Si on modifie et que la description est nulle, on met une chaîne vide
        TextArea champDescription = new TextArea(modeModification ? (tacheAModifier.getDescription() == null ? "" : tacheAModifier.getDescription()) : "");
        champDescription.setPromptText("Description");
        champDescription.setPrefRowCount(3);
        champDescription.setWrapText(true);

        // Sélection des dates de début et de fin
        DatePicker dateDebut;
        DatePicker dateFin;

        if (modeModification) {
            // On remplit avec les dates de la tâche qu’on modifie
            LocalDate deb = tacheAModifier.getDebut();
            LocalDate fin = tacheAModifier.getFin();

            dateDebut = new DatePicker(deb != null ? deb : LocalDate.now());
            dateFin   = new DatePicker(fin != null ? fin : LocalDate.now().plusDays(1));

        } else if (parentTache != null) {
            // Pour une sous-tâche, on se base sur les dates de la tâche parente
            LocalDate deb = parentTache.getDebut();
            LocalDate fin = parentTache.getFin();

            dateDebut = new DatePicker(deb != null ? deb : LocalDate.now());
            dateFin   = new DatePicker(fin != null ? fin : LocalDate.now());
        } else {
            // Pour une nouvelle tâche simple, on met aujourd’hui et demain
            LocalDate today = LocalDate.now();
            dateDebut = new DatePicker(today);
            dateFin   = new DatePicker(today.plusDays(1));
        }

        // Listeners (ici tu pourrais ajouter des règles si tu veux réagir dès qu’une date change)
        dateDebut.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate == null) return;
            LocalDate fin = dateFin.getValue();
        });

        dateFin.valueProperty().addListener((obs, oldFin, newFin) -> {
            if (newFin == null) return;
            LocalDate deb = dateDebut.getValue();
            if (deb == null) return;
        });

        // Choix de la priorité (par défaut : NORMAL)
        ComboBox<Tache.Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().addAll(Tache.Priorite.values());
        champPriorite.setValue(
                modeModification
                        ? (tacheAModifier.getPriorite() == null ? Tache.Priorite.NORMAL : tacheAModifier.getPriorite())
                        : Tache.Priorite.NORMAL
        );

        // ComboBox pour choisir une tâche préalable (pré-requise)
        ComboBox<Tache> comboPrerequise = new ComboBox<>();
        comboPrerequise.setPromptText("Aucune");

        // Ajoute toutes les tâches (racines + sous-tâches), sauf celle qu'on modifie
        for (Liste l : model.getListes()) {
            for (Tache t : model.getTachesFromListe(l)) {
                ajouterTacheEtSousTachesDansCombo(comboPrerequise, t, tacheAModifier);
            }
        }


        // Affiche le titre des tâches dans la liste
        comboPrerequise.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitre());
            }
        });

        // Affiche le texte sur le bouton du ComboBox
        comboPrerequise.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Aucune" : item.getTitre());
            }
        });

        // Si la tâche a déjà une tâche préalable, on la sélectionne
        if (modeModification && tacheAModifier.getPrerequise() != null) {
            comboPrerequise.setValue(tacheAModifier.getPrerequise());
        }

        // Mise en page avec une grille
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Label pour afficher un message d’erreur sur les dates
        Label labErreurDate = new Label();
        labErreurDate.setStyle("-fx-text-fill: red;");

        grid.addRow(0, new Label("Titre"), champTitre);
        grid.addRow(1, new Label("Description"), champDescription);
        grid.addRow(2, new Label("Début"), dateDebut);
        grid.addRow(3, new Label("Fin"), dateFin);
        grid.addRow(4, new Label("Priorité"), champPriorite);
        grid.addRow(5, new Label("Tâche préalable"), comboPrerequise);
        grid.add(labErreurDate, 1, 6);

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(grid);

        // Style du bouton Enregistrer
        Button bValider = (Button) pane.lookupButton(btnValider);
        bValider.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14;");

        // Style du bouton Annuler
        Button bAnnuler = (Button) pane.lookupButton(btnAnnuler);
        bAnnuler.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 8 14;");

        // Vérifie que les dates sont correctes et que le titre n’est pas vide
        Runnable verifierDates = () -> {
            boolean invalide = datesInvalides(dateDebut, dateFin, parentTache);
            boolean titreVide = champTitre.getText() == null || champTitre.getText().isBlank();

            // On bloque le bouton si quelque chose ne va pas
            bValider.setDisable(invalide || titreVide);

            if (invalide) {
                labErreurDate.setText("Date invalide");
            } else {
                labErreurDate.setText("");
            }
        };

        // On relance la vérification dès que l’utilisateur change une valeur
        champTitre.textProperty().addListener((o, ov, nv) -> verifierDates.run());
        dateDebut.valueProperty().addListener((o, ov, nv) -> verifierDates.run());
        dateFin.valueProperty().addListener((o, ov, nv) -> verifierDates.run());
        verifierDates.run();

        // Quand on ferme la fenêtre, si l’utilisateur a cliqué sur Enregistrer
        dialog.showAndWait().ifPresent(btn -> {
            if (btn == btnValider) {
                LocalDate deb = dateDebut.getValue();
                LocalDate fin = dateFin.getValue();

                // On laisse le contrôleur gérer la création ou la modification de la tâche
                new ControlerPopTache(
                        model,
                        liste,
                        champTitre,
                        champDescription,
                        deb,
                        fin,
                        champPriorite,
                        tacheAModifier,
                        parentTache,
                        comboPrerequise.getValue()
                ).handle(new ActionEvent());
            }
        });
    }

    // Retourne true si les dates ne sont pas correctes
    // Règles :
    // - les deux dates doivent être remplies
    // - la fin doit être après le début
    // - si c’est une sous-tâche, elle doit rester dans les dates de la tâche parente
    private static boolean datesInvalides(DatePicker dateDebut, DatePicker dateFin, Tache parentTache) {
        LocalDate dDeb = dateDebut.getValue();
        LocalDate dFin = dateFin.getValue();

        if (dDeb == null || dFin == null) return true;
        if (!dFin.isAfter(dDeb)) return true;

        if (parentTache != null) {
            LocalDate pDeb = parentTache.getDebut();
            LocalDate pFin = parentTache.getFin();

            if (pDeb != null && dDeb.isBefore(pDeb)) return true;
            return pFin != null && dFin.isAfter(pFin);
        }

        return false;
    }

    // Affiche une petite fenêtre de confirmation avant de lancer une action (suppression par exemple)
    public static void popupSuppression(String message, Runnable action) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Supprimer");

        ButtonType ok = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        Label texte = new Label(message);
        texte.setWrapText(true);

        dialog.getDialogPane().setContent(new VBox(10, texte));
        dialog.showAndWait().ifPresent(btn -> {
            // Si l’utilisateur clique sur Supprimer, on exécute l’action
            if (btn == ok) action.run();
        });
    }

    // Demande confirmation puis supprime une liste et toutes ses tâches, puis sauvegarde
    public static void supprimerListe(Liste liste, Model model) {
        popupSuppression("Supprimer la liste : " + liste.getTitre() + " ?\nToutes les tâches seront supprimées.", () -> {
            model.supprimerListe(liste);
            FichierManager.sauvegarder(model, model.getFilepath());
        });
    }

    // Demande confirmation puis supprime une tâche ou une sous-tâche, puis sauvegarde
    public static void supprimerTache(Liste liste, Tache tache, Model model) {
        popupSuppression("Supprimer la tâche : " + tache.getTitre() + " ?", () -> {
            if (tache.getParentTache() instanceof CompositeTache parent) {
                // Cas d’une sous-tâche
                model.supprimerSousTache(parent, tache);
            } else {
                // Cas d’une tâche normale dans la liste
                model.supprimerTache(liste, tache);
            }
            FichierManager.sauvegarder(model, model.getFilepath());
        });
    }

    // Ouvre une petite fenêtre pour créer ou renommer une liste
    public static void ouvrirPopupListe(Liste listeAModifier, Model model) {
        String titreInitial = (listeAModifier == null) ? "" : listeAModifier.getTitre();
        String titreFenetre = (listeAModifier == null) ? "Créer une liste" : "Modifier la liste";

        TextInputDialog dialog = new TextInputDialog(titreInitial);
        dialog.setTitle(titreFenetre);
        dialog.setHeaderText(null);
        dialog.setContentText("Titre :");

        // Quand on clique sur OK, le contrôleur s’occupe de créer ou modifier la liste
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setOnAction(new ControlerPopListe(model, dialog.getEditor(), listeAModifier));

        dialog.showAndWait();
    }
    // Méthode utilitaire remplissant les sous tâches dans les dépendances
    private static void ajouterTacheEtSousTachesDansCombo(ComboBox<Tache> combo,
                                                          Tache t,
                                                          Tache tacheAModifier) {
        if (t != tacheAModifier) {
            combo.getItems().add(t);
        }
        if (t instanceof CompositeTache ct) {
            for (Tache sous : ct.getTaches()) {
                ajouterTacheEtSousTachesDansCombo(combo, sous, tacheAModifier);
            }
        }
    }

}
