package appSAE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Tâche qui peut contenir d'autres tâches (sous-tâches)
public class CompositeTache extends Tache {
    private final List<Tache> sousTaches = new ArrayList<>();
    public CompositeTache(String titre, String description, LocalDate debut, LocalDate fin, Priorite prio) {
        super(titre, description, debut, fin, prio);
    }

    // Ajoute une sous-tâche et lui indique que son parent est cette tâche
    public void ajouterTache(Tache tache) {
        sousTaches.add(tache);
        tache.setParentTache(this);
    }

    // Remplace une ancienne sous-tâche par une nouvelle (par exemple après modification)
    public void modifierSousTache(Tache ancienne, Tache nouvelle) {
        int index = sousTaches.indexOf(ancienne);
        if (index >= 0) {
            sousTaches.set(index, nouvelle);
            nouvelle.setParentTache(this);
        }
    }

    public void supprimerTache(Tache tache) {
        sousTaches.remove(tache);
    }

    public List<Tache> getTaches() {
        return sousTaches;
    }

    @Override
    public void setEstFait(boolean fait) {
        // On garde l’ancien état pour savoir si ça change vraiment
        boolean ancien = estFait();
        super.setEstFait(fait);

        // Si rien n’a changé, on ne fait rien de plus
        if (ancien == fait) return;

        // Si cette tâche a elle-même un parent
        if (getParentTache() != null) {
            // On force toutes les sous-tâches à avoir le même état (fait / pas fait)
            for (Tache t : sousTaches) {
                if (t.estFait() != fait) {
                    t.setEstFait(fait);
                }
            }
            return;
        }

        // Si c’est une tâche "racine" et qu’on la coche, on coche toutes les sous-tâches
        if (fait) {
            for (Tache t : sousTaches) {
                if (!t.estFait()) {
                    t.setEstFait(true);
                }
            }
        }
    }
}
