package appSAE;

import java.util.ArrayList;
import java.util.List;

public class Model implements Sujet{
    private List<Observateur> obs;
    private List<Liste> listes;
    private String filepath;

    public Model() {
        obs = new ArrayList<Observateur>();
        listes = new ArrayList<Liste>();
    }

    public void ajouterListe(Liste liste){
        this.listes.add(liste);
    }

    // Méthode pour modifier le chemin fichier actuel
    public void modifierPath(String path){
        if (filepath != path) {
            filepath = path;
            this.notifierObservateur();
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

    // Méthode pour ajouter une tâche
    public void ajouterTache(String nomtache, String description, String date){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(nomtache)) {
                    Tache nouvelleTache = new CompositeTache(nomtache, description, date, t.getPriorite());
                    t.ajouterTache(nouvelleTache);
                    notifierObservateur();
                    return;
                }
            }
        }
    }

    // Méthode pour supprimer une tâche
    public void supprimerTache(String nomtache, String description, String date){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(nomtache)) {
                    Tache tacheASupprimer = null;
                    /*for (Tache sousTache : t.getSousTaches()) {
                        if (sousTache.getTitre().equals(nomtache) &&
                            sousTache.getDescription().equals(description) &&
                            sousTache.getDate().equals(date)) {
                            tacheASupprimer = sousTache;
                            break;
                        }
                    }*/
                    if (tacheASupprimer != null) {
                        t.supprimerTache(tacheASupprimer);
                        notifierObservateur();
                    }
                    return;
                }
            }
        }
    }

    public void ajouterCarte(String nomtache, String description, String date){
        //TODO
        // à faire quand on auras les tags
    }

    public List<Liste> getListes() {
        return listes;
    }

    public String getFilepath() {
        return filepath;
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