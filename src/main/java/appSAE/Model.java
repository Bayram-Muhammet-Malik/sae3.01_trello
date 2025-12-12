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
    public void modifierNom(String nomtache){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(nomtache)) {
                    t.titre = nomtache; // titre est protected dans Tache
                    notifierObservateur();
                    return; // On modifie la première tâche trouvée
                }
            }
        }
    }

    // Méthode pour modifier une tâche par son nom, description et statut
    public void modifierTout(String nomtache, String description, String date){
        for (Liste liste : listes) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getTitre().equals(nomtache)) {
                    t.description = description;
                    t.date = date;
                    notifierObservateur();
                    return;
                }
            }
        }
    }
    public List<Liste> getListes() {
        return this.listes;
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