package appSAE;

import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache{
    private final List<Tache> content = new ArrayList<>();

    // constructeur complet
    public CompositeTache(String titre, String description, String date, Priorite prio) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.estFait = false;
        this.priorite = (prio == null) ? Priorite.NORMAL : prio;
    }

    public CompositeTache(String titre, String description, String date) {
        this(titre, description, date, null);
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
