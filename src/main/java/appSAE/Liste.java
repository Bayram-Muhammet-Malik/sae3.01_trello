package appSAE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Liste implements Serializable {
    private static final long serialVersionUID = 1L;
    private String titre;
    private List<CompositeTache> taches;

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
    public List<CompositeTache> getTaches(){
        return this.taches;
    }

    public void supprimerTache(Tache tache) {
        this.taches.remove(tache);
    }


    public void ajouterCarte(Tache tache) {
        this.taches.add((CompositeTache) tache);
    }
}
