package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.LocalDate;

// Gère la validation de la popup de création/modification de tache
public class ControlerPopTache implements EventHandler<ActionEvent> {

    private final Model model;
    private final Liste liste;
    private final TextField titreField;
    private final TextArea descField;
    private final LocalDate dateDebut;
    private final LocalDate dateFin;
    private final ComboBox<Tache.Priorite> prioBox;
    private final Tache tacheAModifier; // null si on crée une nouvelle tache
    private final Tache parentTache;    // non null si on crée une sous-tache
    private final Tache prerequise;     // tache préalable éventuelle

    public ControlerPopTache(
            Model model,
            Liste liste,
            TextField titreField,
            TextArea descField,
            LocalDate dateDebut,
            LocalDate dateFin,
            ComboBox<Tache.Priorite> prioBox,
            Tache tacheAModifier,
            Tache parentTache,
            Tache prerequise
    ) {
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
        // Si aucune priorité choisie, on met NORMAL
        Tache.Priorite prio = (prioBox.getValue() == null)
                ? Tache.Priorite.NORMAL
                : prioBox.getValue();

        LocalDate deb = dateDebut;

        // Cas 1 : création de tache
        if (tacheAModifier == null) {
            if (parentTache == null) {
                // Création d’une tache "normale" dans une liste
                model.ajouterTache(
                        liste,
                        titreField.getText().trim(),
                        descField.getText(),
                        deb,
                        dateFin,
                        prio,
                        prerequise
                );
            } else {
                // Création d’une sous tache
                CompositeTache parent;

                if (parentTache instanceof FeuilleTache ft) {
                    parent = new CompositeTache(
                            parentTache.getTitre(),
                            parentTache.getDescription(),
                            parentTache.getDebut(),
                            parentTache.getFin(),
                            parentTache.getPriorite()
                    );

                    // On remplace la tache parente par la nouvelle CompositeTache
                    if (parentTache.getParentTache() != null) {
                        parentTache.getParentTache().modifierSousTache(parentTache, parent);
                    } else {
                        liste.modifierTache(parentTache, parent);
                    }
                } else {
                    // Le parent est déjà une CompositeTache
                    parent = (CompositeTache) parentTache;
                }

                // On ajoute la nouvelle sous tache dans la CompositeTache
                model.ajouterSousTache(
                        parent,
                        titreField.getText().trim(),
                        descField.getText(),
                        deb,
                        dateFin,
                        prio,
                        prerequise
                );
            }

        } else {
            // Cas 2 : modification d’une tache existante
            tacheAModifier.setTitre(titreField.getText().trim());
            tacheAModifier.setDescription(descField.getText());
            tacheAModifier.setDebut(deb);
            tacheAModifier.setFin(dateFin);
            tacheAModifier.setPriorite(prio);
            tacheAModifier.setPrerequise(prerequise);

            // On prévient la vue que les données ont changé
            model.notifierObservateur();
        }

        // À la fin, on sauvegarde toujours le modèle dans le fichier courant
        FichierManager.sauvegarder(model, model.getFilepath());
    }
}
