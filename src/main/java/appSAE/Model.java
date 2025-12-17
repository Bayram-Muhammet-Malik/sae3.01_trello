package appSAE;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Model implements Sujet, Serializable {
    private static final long serialVersionUID = 1L;
    private transient List<Observateur> obs;
    private List<Liste> listes;
    private String filepath;

    public Model() {
        obs = new ArrayList<Observateur>();
        listes = new ArrayList<Liste>();
    }

    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException {

        ois.defaultReadObject();
        obs = new ArrayList<>();
    }

    public void ajouterListe(Liste liste){
        this.listes.add(liste);
        notifierObservateur();
    }

    public void supprimerListe(Liste liste){
        this.listes.remove(liste);
        notifierObservateur();
    }

    // Méthode pour modifier le chemin fichier actuel
    public void modifierPath(String path){
        if (this.filepath != path){
            this.filepath = path;
            notifierObservateur();
        }
    }

    // Méthode pour modifier le titre d'une tâche
    public void modifierNom(String nomtache){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(nomtache)) {
                    t.titre = nomtache;
                    notifierObservateur();
                }
            }
        }
    }

    // Méthode pour modifier la date d'une tâche
    public void modifierDate(String titre, String date){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(titre)) {
                    t.date = date;
                    notifierObservateur();
                }
            }
        }
    }

    // Méthode pour modifier la description d'une tâche
    public void modifierDescription(String titre, String description){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(titre)) {
                    t.description = description;
                    notifierObservateur();
                }
            }
        }
    }

    // Méthode pour modifier tous les elements d'une tache
    public void modifierTout(String nomtache, String description, String date){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(nomtache)) {
                    t.titre = nomtache;
                    t.description = description;
                    t.date = date;
                    notifierObservateur();
                }
            }
        }
    }

    public List<Liste> getListes() {
        return listes;
    }

    public String getFilepath() {
        return filepath;
    }

    public List<CompositeTache> getTachesFromListe(Liste liste){
        return liste.getTaches();
    }

    public void ajouterCarte(Liste liste, Tache nouvelleTache) {
        liste.ajouterCarte(nouvelleTache);
        notifierObservateur();
    }

    public void supprimerTache(Liste liste, Tache Tache) {
        liste.supprimerTache(Tache);
        notifierObservateur();
    }

    @Override
    public void enregistrerObservateur(Observateur o) {
        this.obs.add(o);
    }

    @Override
    public void notifierObservateur() {
        for (int i = 0; i < this.obs.size(); i++) {
            Observateur observer = this.obs.get(i);
            observer.actualiser(this);
        }
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        int i = this.obs.indexOf(o);
        if (i >= 0) {
            this.obs.remove(i);
        }
    }
}