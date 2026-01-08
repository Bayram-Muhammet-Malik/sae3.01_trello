package appSAE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache{
    private final List<Tache> sousTaches = new ArrayList<>();

    public CompositeTache(String titre, String description, LocalDate debut, LocalDate fin, Priorite prio) {
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
