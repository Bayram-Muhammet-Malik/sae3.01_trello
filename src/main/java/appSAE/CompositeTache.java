package appSAE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Tache> content = new ArrayList<>();

    public CompositeTache(String titre, String description, String date, Priorite prio) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.estFait = false;
        // Priorité avec verif si = null
        this.priorite = (prio == null) ? Priorite.NORMAL : prio;
        ;
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
