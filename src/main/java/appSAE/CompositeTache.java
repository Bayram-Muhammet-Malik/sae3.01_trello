package appSAE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache{
    private final List<Tache> sousTaches = new ArrayList<>();

    public CompositeTache(String titre, String description, LocalDateTime debut, LocalDateTime fin, Priorite prio) {
        super(titre, description, debut, fin, prio);
    }

    public void ajouterTache(Tache tache) {
        sousTaches.add(tache);
        tache.setParentTache(this);
    }

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
}
