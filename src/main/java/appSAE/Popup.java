package appSAE;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class Popup {
    /**
     * Méthode qui crée une fenètre Popup de Création ou Modification de Tâches et Sous-Tâches
     * @param liste Liste où la tâche est contenue (null si c'est une SousTache)
     * @param tacheAModifier Tache a modifier (null si c'est une création)
     * @param parentTache Tache Parent (null si ce n'est pas une SousTache)
     * @param model Model
     */
    public static void ouvrirPopUpTache(Liste liste, Tache tacheAModifier, Tache parentTache, Model model) {
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

        // Dates début / fin (sans heure)
        DatePicker dateDebut;
        DatePicker dateFin;

        if (modeModification) {
            LocalDate deb = tacheAModifier.getDebut();
            LocalDate fin = tacheAModifier.getFin();

            dateDebut = new DatePicker(deb != null ? deb : LocalDate.now());
            dateFin   = new DatePicker(fin != null ? fin : LocalDate.now().plusDays(1));

        } else if (parentTache != null) {
            LocalDate deb = parentTache.getDebut();
            LocalDate fin = parentTache.getFin();

            dateDebut = new DatePicker(deb != null ? deb : LocalDate.now());
            dateFin   = new DatePicker(fin != null ? fin : LocalDate.now());
        } else {
            LocalDate today = LocalDate.now();
            dateDebut = new DatePicker(today);
            dateFin   = new DatePicker(today.plusDays(1));
        }

        // Priorité de la tâche
        ComboBox<Tache.Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().addAll(Tache.Priorite.values());
        champPriorite.setValue(modeModification ? (tacheAModifier.getPriorite() == null ? Tache.Priorite.NORMAL : tacheAModifier.getPriorite()) : Tache.Priorite.NORMAL);

        // choix de la tâche préalable
        ComboBox<Tache> comboPrerequise = new ComboBox<>();
        comboPrerequise.setPromptText("Aucune");

        for (Liste l : model.getListes()) {
            for (Tache t : model.getTachesFromListe(l)) {
                if (t != tacheAModifier) {
                    comboPrerequise.getItems().add(t);
                }
            }
        }

        comboPrerequise.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitre());
            }
        });
        comboPrerequise.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tache item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Aucune" : item.getTitre());
            }
        });

        if (modeModification && tacheAModifier.getPrerequise() != null) comboPrerequise.setValue(tacheAModifier.getPrerequise());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

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

        Button bValider = (Button) pane.lookupButton(btnValider);
        bValider.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14;");

        Button bAnnuler = (Button) pane.lookupButton(btnAnnuler);
        bAnnuler.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 8 14;");

        Runnable verifierDates = () -> {
            boolean invalide = datesInvalides(dateDebut, dateFin, parentTache);
            boolean titreVide = champTitre.getText() == null || champTitre.getText().isBlank();

            bValider.setDisable(invalide || titreVide);

            if (invalide) {
                labErreurDate.setText("Date invalide");
            } else {
                labErreurDate.setText("");
            }
        };
        champTitre.textProperty().addListener((o, ov, nv) -> verifierDates.run());
        dateDebut.valueProperty().addListener((o, ov, nv) -> verifierDates.run());
        dateFin.valueProperty().addListener((o, ov, nv) -> verifierDates.run());
        verifierDates.run();

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == btnValider) {
                LocalDate deb = dateDebut.getValue();
                LocalDate fin = dateFin.getValue();

                new ControlerPopTache(model, liste, champTitre, champDescription, deb, fin, champPriorite, tacheAModifier, parentTache, comboPrerequise.getValue()).handle(new ActionEvent());
            }
        });
    }

    /**
     * Méthode qui permet de vérifier si les dates début/fin sont invalides ou pas
     * @param dateDebut
     * @param dateFin
     * @param parentTache
     * @return true si les dates sont invalides sinon false
     */
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

    /**
     * Méthode qui créer un popup de suppression (de liste ou tache)
     */
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
            if (btn == ok) action.run();
        });
    }

    /**
     * Méthode qui permet de supprimer une Liste (au traver de popupSuppression)
     * @param liste
     * @param model
     */
    public static void supprimerListe(Liste liste, Model model) {
        popupSuppression("Supprimer la liste : " + liste.getTitre() + " ?\nToutes les tâches seront supprimées.", () -> {
            model.supprimerListe(liste);
            FichierManager.sauvegarder(model, model.getFilepath());
        });
    }

    /**
     * Méthode qui permet de supprimer une Tache (au traver de popupSuppression)
     * @param liste Liste où ce trouve la tâche
     * @param tache Tache à supprimer
     * @param model
     */
    public static void supprimerTache(Liste liste, Tache tache, Model model) {
        popupSuppression("Supprimer la tâche : " + tache.getTitre() + " ?", () -> {
            if (tache.getParentTache() instanceof CompositeTache parent) {
                model.supprimerSousTache(parent, tache);
            } else {
                model.supprimerTache(liste, tache);
            }
            FichierManager.sauvegarder(model, model.getFilepath());
        });
    }

    /**
     * Méthode qui permet de créer un popup pour créer ou modifier une liste
     * @param listeAModifier
     * @param model
     */
    public static void ouvrirPopupListe(Liste listeAModifier, Model model) {
        String titreInitial = (listeAModifier == null) ? "" : listeAModifier.getTitre();
        String titreFenetre = (listeAModifier == null) ? "Créer une liste" : "Modifier la liste";

        TextInputDialog dialog = new TextInputDialog(titreInitial);
        dialog.setTitle(titreFenetre);
        dialog.setHeaderText(null);
        dialog.setContentText("Titre :");

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setOnAction(new ControlerPopListe(model, dialog.getEditor(), listeAModifier));

        dialog.showAndWait();
    }
}
