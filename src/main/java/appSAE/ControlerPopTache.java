package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.LocalDateTime;

public class ControlerPopTache implements EventHandler<ActionEvent> {
    private final Model model;
    private final Liste liste;
    private final TextField titreField;
    private final TextArea descField;
    private final LocalDateTime dateDebut;
    private final LocalDateTime dateFin;
    private final ComboBox<Tache.Priorite> prioBox;
    private final boolean modeModification;
    private final Tache tacheAModifier;

    public ControlerPopTache(Model model, Liste liste, TextField titreField, TextArea descField, LocalDateTime dateDebut, LocalDateTime dateFin, ComboBox<Tache.Priorite> prioBox, boolean modeModification, Tache tacheAModifier) {
        this.model = model;
        this.liste = liste;
        this.titreField = titreField;
        this.descField = descField;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prioBox = prioBox;
        this.modeModification = modeModification;
        this.tacheAModifier = tacheAModifier;
    }

    @Override
    public void handle(ActionEvent e) {
        Tache.Priorite prio = (prioBox.getValue() == null) ? Tache.Priorite.NORMAL : prioBox.getValue();

        if (!modeModification) {
            model.ajouterTache(liste, titreField.getText().trim(), descField.getText(), dateDebut, dateFin, prio);
        } else if (tacheAModifier != null) {
            tacheAModifier.titre = titreField.getText().trim();
            tacheAModifier.description = descField.getText();
            tacheAModifier.debut = dateDebut;
            tacheAModifier.fin = dateFin;
            tacheAModifier.setPriorite(prio);
            model.notifierObservateur();
        }

        FichierManager.sauvegarder(model, model.getFilepath());
    }
}
