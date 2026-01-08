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

    /**
     * Constucteur d'un Liste
     * @param titre
     */
    public Liste(String titre) {
        this.titre = titre;
        this.taches = new ArrayList<>();
    }

    /**
     * Méthode qui permet de changer le nom de la liste
     * @param newName
     */
    public void changerNom(String newName){
        this.titre = newName;
    }

    // GETTERS
    public String getTitre(){
        return this.titre;
    }
    public List<Tache> getTaches(){
        return this.taches;
    }

    /**
     * Méthode qui permet de remplacer une tâche de la liste (exemple passer d'une FeuilleTache à CompositeTache)
     * @param ancienne
     * @param nouvelle
     */
    public void modifierTache(Tache ancienne, Tache nouvelle) {
        int index = taches.indexOf(ancienne);
        if (index >= 0) {
            taches.set(index, nouvelle);
            nouvelle.setParentTache(null);
        }
    }

    /**
     * Méthode qui permet de supprimer une Tâche de la liste
     * @param tache
     */
    public void supprimerTache(Tache tache) {
        this.taches.remove(tache);
    }

    /**
     * Méthode qui permet d'ajouter une Tâche à la liste
     * @param tache
     */
    public void ajouterCarte(Tache tache) {
        this.taches.add(tache);
    }
}
