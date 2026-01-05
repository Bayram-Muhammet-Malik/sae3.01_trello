package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Liste implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String titre;
    private List<Tache> taches;

    public Liste(String titre) {
        this.titre = titre;
        this.taches = new ArrayList<>();
    }

    public void changerNom(String newName){
        this.titre = newName;
    }

    public String getTitre(){
        return this.titre;
    }
    public List<Tache> getTaches(){
        return this.taches;
    }

    public void modifierTache(Tache ancienne, Tache nouvelle) {
        int index = taches.indexOf(ancienne);
        if (index >= 0) {
            taches.set(index, nouvelle);
            nouvelle.setParentTache(null);
        }
    }

    public void supprimerTache(Tache tache) {
        this.taches.remove(tache);
    }

    public void ajouterCarte(Tache tache) {
        this.taches.add(tache);
    }
}
