package appSAE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache{
    private final List<Tache> content = new ArrayList<>();

    public CompositeTache(String titre, String description, LocalDateTime debut, LocalDateTime fin, Priorite prio) {
        super(titre, description, debut, fin, prio);
    }

    public void ajouterTache(Tache tache) {
        content.add(tache);
    }

    public void supprimerTache(Tache tache) {
        content.remove(tache);
    }

    public List<Tache> getTaches() {
        return content;
    }
}
