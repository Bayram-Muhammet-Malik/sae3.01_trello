package appSAE;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Popup {
    // Popup de création et modification de tâche et de sous-tâche
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

        // Date/Heure début et fin
        DatePicker dateDebut = new DatePicker(LocalDate.now());
        DatePicker dateFin = new DatePicker(LocalDate.now());
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

        if (modeModification && tacheAModifier.getPrerequise() != null) {
            comboPrerequise.setValue(tacheAModifier.getPrerequise());
        }
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Titre"), champTitre);
        grid.addRow(1, new Label("Description"), champDescription);
        grid.addRow(2, new Label("Début"), new HBox(5, dateDebut, hDeb, new Label(":"), mDeb));
        grid.addRow(3, new Label("Fin"), new HBox(5, dateFin, hFin, new Label(":"), mFin));
        grid.addRow(4, new Label("Priorité"), champPriorite);
        grid.addRow(5, new Label("Tâche préalable"), comboPrerequise);

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(grid);

        Button bValider = (Button) pane.lookupButton(btnValider);
        bValider.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14;");

        Button bAnnuler = (Button) pane.lookupButton(btnAnnuler);
        bAnnuler.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 8 14;");

        Runnable verifierDates = () -> {
            boolean invalide = datesInvalides(dateDebut, hDeb, mDeb, dateFin, hFin, mFin, parentTache);
            boolean titreVide = champTitre.getText() == null || champTitre.getText().isBlank();
            bValider.setDisable(invalide || titreVide);
        };
        List<ObservableValue<?>> champs = List.of(dateDebut.valueProperty(), dateFin.valueProperty(), hDeb.valueProperty(), mDeb.valueProperty(), hFin.valueProperty(), mFin.valueProperty(), champTitre.textProperty());
        champs.forEach(prop -> prop.addListener((o, ov, nv) -> verifierDates.run()));
        verifierDates.run();

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == btnValider)
                new ControlerPopTache(model, liste, champTitre, champDescription, LocalDateTime.of(dateDebut.getValue(), LocalTime.of(hDeb.getValue(), mDeb.getValue())), LocalDateTime.of(dateFin.getValue(), LocalTime.of(hFin.getValue(), mFin.getValue())), champPriorite, tacheAModifier, parentTache, comboPrerequise.getValue()).handle(new ActionEvent());
        });
    }

    private static boolean datesInvalides(DatePicker dateDebut, ComboBox<Integer> hDeb, ComboBox<Integer> mDeb, DatePicker dateFin, ComboBox<Integer> hFin, ComboBox<Integer> mFin, Tache parentTache) {
        LocalDateTime deb = LocalDateTime.of(dateDebut.getValue(), LocalTime.of(hDeb.getValue(), mDeb.getValue()));
        LocalDateTime fin = LocalDateTime.of(dateFin.getValue(), LocalTime.of(hFin.getValue(), mFin.getValue()));

        if (deb.isAfter(fin)) return true;

        if (parentTache != null) {
            LocalDateTime pDeb = parentTache.getDebut();
            LocalDateTime pFin = parentTache.getFin();

            if (pDeb != null && deb.isBefore(pDeb)) return true;
            if (pFin != null && fin.isAfter(pFin)) return true;
        }

        return false;
    }

    //Popup de suppression d'une liste et tâche
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

    public static void supprimerListe(Liste liste, Model model) {
        popupSuppression("Supprimer la liste : " + liste.getTitre() + " ?\nToutes les tâches seront supprimées.", () -> {
            model.supprimerListe(liste);
            FichierManager.sauvegarder(model, model.getFilepath());
        });
    }

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
