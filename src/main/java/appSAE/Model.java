package appSAE;

import java.util.ArrayList;
import java.util.List;

public class Model implements Sujet{
    private List<Observateur> obs;
    private List<Liste> listes;

    public Model() {
        obs = new ArrayList<Observateur>();
        listes = new ArrayList<Liste>();
    }


    public void ajouterListe(Liste liste){
        this.listes.add(liste);
    }


    // Méthode pour modifier une tâche par son nom
    public void modifier(String nomtache){
        // TODO
    }

    // Méthode pour modifier une tâche par son nom, description et statut
    public void modifier(String nomtache, String description, String status){
        // TODO
    }

    public void enregistrerObs(Observateur o) {
        this.obs.add(o);
    }

    public void supprimerObs(Observateur o) {
        int i = this.obs.indexOf(o);
        if (i >= 0) {
            this.obs.remove(i);
        }
    }

    public void notifierObs() {
        for (int i = 0; i < this.obs.size(); i++) {
            Observateur observer = this.obs.get(i);
            observer.actualiser(this);
        }
    }

    @Override
    public void enregistrerObservateur(Observateur o) {

    }

    @Override
    public void notifierObservateur() {

    }

    @Override
    public void supprimerObservateur(Observateur o) {

    }
}