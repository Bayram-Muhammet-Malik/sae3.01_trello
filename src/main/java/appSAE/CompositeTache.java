package appSAE;

import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache{
    private final List<Tache> content = new ArrayList<>();
    private EtatTache etat;

    // constructeur complet : etat + priorite
    public CompositeTache(String titre, String description, String date, EtatTache etat, Priorite prio) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.estFait = false;

        // valeurs par defaut si null
        this.etat = (etat == null) ? EtatTache.A_FAIRE : etat;
        this.priorite = (prio == null) ? Priorite.NORMAL : prio;
    }

    // raccourci : priorite donnee, etat par defaut = a faire
    public CompositeTache(String titre, String description, String date, Priorite prio) {
        this(titre, description, date, EtatTache.A_FAIRE, prio);
    }

    // raccourci : etat donne, priorite par defaut = normal
    public CompositeTache(String titre, String description, String date, EtatTache etat) {
        this(titre, description, date, etat, Priorite.NORMAL);
    }

    //raccourci : BYPASS la prio
    public CompositeTache(String titre, String description, String date) {
        this(titre, description, date, EtatTache.A_FAIRE, null);
    }

    public EtatTache getEtat() {
        return etat;
    }

    public void setEtat(EtatTache etat) {
        if (etat == null) return;
        this.etat = etat;
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
