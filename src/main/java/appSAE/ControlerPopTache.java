package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class ControlerPopTache implements EventHandler<ActionEvent> {
    private final Model model;
    private final Liste liste;
    private final TextField titreField;
    private final TextArea descField;
    private final LocalDate dateDebut;
    private final LocalDate dateFin;
    private final ComboBox<Tache.Priorite> prioBox;
    private final Tache tacheAModifier;
    private final Tache parentTache;
    private final Tache prerequise;

    /**
     * Constructeur
     * @param model Model
     * @param liste Liste auquel appartient la tâche
     * @param titreField Champ du titre
     * @param descField Champ de la description
     * @param dateDebut Date de début de la tâche
     * @param dateFin Date de fin de la tâche
     * @param prioBox Etiquette de priorité (Normal, Important, Urgent)
     * @param tacheAModifier Tache a modifier (null si c'est une création)
     * @param parentTache Tache parente (null si y en a pas)
     * @param prerequise Dépendance
     */
    public ControlerPopTache(Model model, Liste liste, TextField titreField, TextArea descField, LocalDate dateDebut, LocalDate dateFin, ComboBox<Tache.Priorite> prioBox, Tache tacheAModifier, Tache parentTache, Tache prerequise) {
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

    /**
     * Handler qui effectue une action de création/modification de composite tâche/sous-tâches
     * @param e ActionEvent
     */
    @Override
    public void handle(ActionEvent e) {
        Tache.Priorite prio = (prioBox.getValue() == null) ? Tache.Priorite.NORMAL : prioBox.getValue();

        LocalDate deb = dateDebut;
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
            tacheAModifier.setPrerequise(prerequise);
            model.notifierObservateur();
        }

        FichierManager.sauvegarder(model, model.getFilepath());
    }
}
