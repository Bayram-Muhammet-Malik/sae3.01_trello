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
    private final Tache tacheAModifier;
    private final Tache parentTache;

    public ControlerPopTache(Model model, Liste liste, TextField titreField, TextArea descField, LocalDateTime dateDebut, LocalDateTime dateFin, ComboBox<Tache.Priorite> prioBox, Tache tacheAModifier, Tache parentTache) {
        this.model = model;
        this.liste = liste;
        this.titreField = titreField;
        this.descField = descField;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prioBox = prioBox;
        this.tacheAModifier = tacheAModifier;
        this.parentTache = parentTache;
    }

    @Override
    public void handle(ActionEvent e) {
        Tache.Priorite prio = (prioBox.getValue() == null) ? Tache.Priorite.NORMAL : prioBox.getValue();

        if (tacheAModifier == null) {
            if (parentTache == null){
                model.ajouterTache(liste, titreField.getText().trim(), descField.getText(), dateDebut, dateFin, prio);
            } else {
                CompositeTache parent;
                if (parentTache instanceof FeuilleTache ft) {
                    parent = new CompositeTache(parentTache.getTitre(), parentTache.getDescription(), parentTache.getDebut(), parentTache.getFin(), parentTache.getPriorite());
                    if (parentTache.getParentTache() != null) {
                        parentTache.getParentTache().modifierSousTache(parentTache, parent);
                    } else {
                        liste.modifierTache(parentTache, parent);
                    }
                } else {
                    parent = (CompositeTache) parentTache;
                }
                model.ajouterSousTache(parent, titreField.getText().trim(), descField.getText(), dateDebut, dateFin, prio);
            }
        } else {
            tacheAModifier.setTitre(titreField.getText().trim());
            tacheAModifier.setDescription(descField.getText());
            tacheAModifier.setDebut(dateDebut);
            tacheAModifier.setFin(dateFin);
            tacheAModifier.setPriorite(prio);
            model.notifierObservateur();
        }

        FichierManager.sauvegarder(model, model.getFilepath());
    }
}
