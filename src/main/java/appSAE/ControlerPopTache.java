package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class ControlerPopTache implements EventHandler<ActionEvent> {
    private final Model model;
    private final Liste liste;
    private final TextField titreField;
    private final TextArea descField;
    private final DatePicker datePicker;
    private final ComboBox<Tache.Priorite> prioBox;
    private final boolean modeModification;
    private final CompositeTache tacheAModifier;

    public ControlerPopTache(Model model, Liste liste, TextField titreField, TextArea descField, DatePicker datePicker, ComboBox<Tache.Priorite> prioBox, boolean modeModification, CompositeTache tacheAModifier) {
        this.model = model;
        this.liste = liste;
        this.titreField = titreField;
        this.descField = descField;
        this.datePicker = datePicker;
        this.prioBox = prioBox;
        this.modeModification = modeModification;
        this.tacheAModifier = tacheAModifier;
    }

    @Override
    public void handle(ActionEvent e) {
        String dateStr = datePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Tache.Priorite prio = (prioBox.getValue() == null) ? Tache.Priorite.NORMAL : prioBox.getValue();

        if (!modeModification) {
            model.ajouterTache(liste, titreField.getText().trim(), descField.getText(), dateStr, prio);
        } else if (tacheAModifier != null) {
            tacheAModifier.titre = titreField.getText().trim();
            tacheAModifier.description = descField.getText();
            tacheAModifier.date = dateStr;
            tacheAModifier.setPriorite(prio);
            model.notifierObservateur();
        }

        FichierManager.sauvegarder(model, model.getFilepath());
    }
}
