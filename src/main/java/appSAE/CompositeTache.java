package appSAE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompositeTache extends Tache implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Tache> content = new ArrayList<>();
    private EtatTache etat;

    public CompositeTache(String titre, String description, String date) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.estFait = false;
    }

    public CompositeTache(String titreDeLaTâcheX, String s, String s1, boolean b, EtatTache etatTache) {
        super();
        this.etat = etat;
    }

    public EtatTache getEtat() { return etat; }
    public void setEtat(EtatTache etat) { this.etat = etat; }

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
