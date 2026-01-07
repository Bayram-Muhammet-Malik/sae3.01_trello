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
    // tâche préalable
    private final Tache prerequise;

    public ControlerPopTache(Model model, Liste liste, TextField titreField, TextArea descField, LocalDateTime dateDebut, LocalDateTime dateFin, ComboBox<Tache.Priorite> prioBox, Tache tacheAModifier, Tache parentTache, Tache prerequise) {
        this.model = model;
        this.liste = liste;
        this.titreField = titreField;
        this.descField = descField;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prioBox = prioBox;
        this.tacheAModifier = tacheAModifier;
        this.parentTache = parentTache;
        this.prerequise = prerequise;
    }

    @Override
    public void handle(ActionEvent e) {
        Tache.Priorite prio = (prioBox.getValue() == null) ? Tache.Priorite.NORMAL : prioBox.getValue();

        LocalDateTime deb = dateDebut;
        // si une tâche préalable existe et que le début est avant sa fin, on décale
        if (prerequise != null && prerequise.getFin() != null && deb != null && deb.isBefore(prerequise.getFin())) {
            deb = prerequise.getFin().plusMinutes(1);
        }

        if (tacheAModifier == null) {
            if (parentTache == null){
                model.ajouterTache(liste, titreField.getText().trim(), descField.getText(), deb, dateFin, prio, prerequise);
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
                model.ajouterSousTache(parent, titreField.getText().trim(), descField.getText(), deb, dateFin, prio, prerequise);
            }
        } else {
            tacheAModifier.setTitre(titreField.getText().trim());
            tacheAModifier.setDescription(descField.getText());
            tacheAModifier.setDebut(deb);
            tacheAModifier.setFin(dateFin);
            tacheAModifier.setPriorite(prio);
            tacheAModifier.setPrerequise(prerequise); // NOUVEAU
            model.notifierObservateur();
        }

        FichierManager.sauvegarder(model, model.getFilepath());
    }
}
