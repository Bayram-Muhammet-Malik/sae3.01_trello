package appSAE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache{
    private final List<Tache> sousTaches = new ArrayList<>();

    /**
     * Constructeur CompositeTache (appel le constructeur de Tache auquel il hérite)
     * @param titre Titre de la tâche
     * @param description Description de la tâche
     * @param debut Date début
     * @param fin Date de fin
     * @param prio Tag Normal/Important/Urgent
     */
    public CompositeTache(String titre, String description, LocalDate debut, LocalDate fin, Priorite prio) {
        super(titre, description, debut, fin, prio);
    }

    /**
     * Méthode qui permet d'ajouter une sous tâche à une tâche
     * @param tache tache à ajouter
     */
    public void ajouterTache(Tache tache) {
        sousTaches.add(tache);
        tache.setParentTache(this);
    }

    /**
     * Méthode qui permet de remplacer une sous-tâche (sert à passé d'une FeuilleTache à CompositeTache par exemple)
     * @param ancienne (tâche a remplacer)
     * @param nouvelle (tâche qui remplace)
     */
    public void modifierSousTache(Tache ancienne, Tache nouvelle) {
        int index = sousTaches.indexOf(ancienne);
        if (index >= 0) {
            sousTaches.set(index, nouvelle);
            nouvelle.setParentTache(this);
        }
    }

    /**
     * Méthode qui permet de supprimer une sous-tâche
     * @param tache
     */
    public void supprimerTache(Tache tache) {
        sousTaches.remove(tache);
    }

    /**
     * Méthode qui permet d'obtenir les fils de la tâche
     * @return
     */
    public List<Tache> getTaches() {
        return sousTaches;
    }

    /**
     * Méthode qui permet de changé l'état fait/non fait de la tâche est ses enfants/parent en fonctions des cas
     * @param fait true si la tâche est faite sinon false
     */
    @Override
    public void setEstFait(boolean fait) {
        boolean ancien = estFait();
        super.setEstFait(fait);

        if (ancien == fait) return;

        if (getParentTache() != null) {
            for (Tache t : sousTaches) if (t.estFait() != fait) t.setEstFait(fait);
            return;
        }

        if (fait) for (Tache t : sousTaches) if (!t.estFait()) t.setEstFait(true);
    }
}
