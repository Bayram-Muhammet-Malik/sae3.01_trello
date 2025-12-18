package appSAE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Model implements Sujet, Serializable {
    private static final long serialVersionUID = 1L;
    private transient List<Observateur> obs;
    private List<Liste> listes;
    private transient String filepath;
    private String lastVue = "HOME";

    public Model() {
        obs = new ArrayList<Observateur>();
        listes = new ArrayList<Liste>();
    }

    public void setModel(Model model, String path) {
        this.listes = (model != null ? model.getListes() : new ArrayList<Liste>());
        this.lastVue = (model != null ? model.getLastVue() : "BUREAU");
        this.filepath = path;
        notifierObservateur();
    }

    public void ajouterListe(Liste liste){
        this.listes.add(liste);
        notifierObservateur();
    }

    public void supprimerListe(Liste liste){
        this.listes.remove(liste);
        notifierObservateur();
    }

    public void modifierLastVue(String vue){
        if (this.lastVue != vue){
            this.lastVue = vue;
            notifierObservateur();
        }
    }

    public void ajouterTache(Liste liste, String titre, String desc, String date, Tache.Priorite prio) {
        if (prio == null) prio = Tache.Priorite.NORMAL;
        CompositeTache tache = new CompositeTache(titre, desc, date, prio);
        liste.ajouterCarte(tache);
        notifierObservateur();
    }

    public List<Liste> getListes() {
        return listes;
    }
    public String getFilepath() {
        return filepath;
    }
    public String getLastVue(){ return lastVue; }

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

    public void deplacerTache(String dashboard, Tache tache) {
        Liste listeSource = null;
        Liste listeCible = null;

        for (Liste l : listes) {
            if (l.getTaches().contains(tache)) {
                listeSource = l;
            }
            if (l.getTitre().equals(dashboard)) {
                listeCible = l;
            }
        }

        if (listeSource == listeCible) {
            return;

        }
        listeSource.supprimerTache(tache);
        listeCible.ajouterCarte(tache);

        notifierObservateur();
    }

}