package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Model implements Sujet, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private transient List<Observateur> obs;
    private List<Liste> listes;
    private transient String filepath;
    private String lastVue = "HOME";

    public Model() {
        obs = new ArrayList<>();
        listes = new ArrayList<>();
    }

    public void setModel(Model model, String path) {
        this.listes = (model != null ? model.getListes() : new ArrayList<>());
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
        if (!this.lastVue.equals(vue)){
            this.lastVue = vue;
            notifierObservateur();
        }
    }

    public void ajouterTache(Liste liste, String titre, String desc, LocalDate debut, LocalDate fin, Tache.Priorite prio) {
        ajouterTache(liste, titre, desc, debut, fin, prio, null);
    }

    // avec tâche préalable
    public void ajouterTache(Liste liste, String titre, String desc, LocalDate debut, LocalDate fin, Tache.Priorite prio, Tache prerequise) {
        if (prio == null) prio = Tache.Priorite.NORMAL;
        FeuilleTache tache = new FeuilleTache(titre, desc, debut, fin, prio);
        tache.setPrerequise(prerequise);
        liste.ajouterCarte(tache);
        notifierObservateur();
    }

    public void ajouterSousTache(CompositeTache parent, String titre, String desc, LocalDate debut, LocalDate fin, Tache.Priorite prio) {
        ajouterSousTache(parent, titre, desc, debut, fin, prio, null);
    }

    // sous-tâche avec dépendance
    public void ajouterSousTache(CompositeTache parent, String titre, String desc, LocalDate debut, LocalDate fin, Tache.Priorite prio, Tache prerequise) {
        if (prio == null) prio = Tache.Priorite.NORMAL;
        FeuilleTache tache = new FeuilleTache(titre, desc, debut, fin, prio);
        tache.setPrerequise(prerequise);
        parent.ajouterTache(tache);
        notifierObservateur();
    }

    public List<Liste> getListes() {
        return listes;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getLastVue(){ return lastVue; }

    // Retourne les tâches d'une liste
    public List<Tache> getTachesFromListe(Liste liste){
        return liste.getTaches();
    }

    // Ajoute une tâche déjà créée dans une liste
    public void ajouterCarte(Liste liste, Tache nouvelleTache) {
        liste.ajouterCarte(nouvelleTache);
        notifierObservateur();
    }

    // Supprime une tâche d'une liste

    public void supprimerTache(Liste liste, Tache tache) {
        liste.supprimerTache(tache);
        notifierObservateur();
    }

    // Supprime une sous-tâche d'une tâche composite
    public void supprimerSousTache(CompositeTache parent, Tache tache) {
        parent.supprimerTache(tache);
        notifierObservateur();
    }

    // Coche / décoche une tâche
    public void setTacheFait(Tache tache, boolean fait) {
        tache.setEstFait(fait);
        notifierObservateur();
    }

    // Change l’ordre des listes (drag and drop des colonnes)
    public void deplacerListe(Liste liste, int nouvelIndex) {
        if (liste == null) return;
        int ancienIndex = listes.indexOf(liste);
        if (ancienIndex == -1 || nouvelIndex < 0 || nouvelIndex >= listes.size())
            return;
        if (ancienIndex == nouvelIndex) return;
        listes.remove(ancienIndex);
        listes.add(nouvelIndex, liste);
        FichierManager.sauvegarder(this, filepath);
        notifierObservateur();
    }

    // Déplace une tâche vers une autre liste
    public void deplacerTacheDansListe(Liste listeCible, Tache tache) {
        if (listeCible == null || tache == null) return;

        CompositeTache parent = tache.getParentTache();
        if (parent != null) {
            parent.getTaches().remove(tache);
            tache.setParentTache(null);
        } else {
            for (Liste l : listes) {
                if (l.getTaches().remove(tache)) {
                    break;
                }
            }
        }

        listeCible.ajouterCarte(tache);
        notifierObservateur();
        FichierManager.sauvegarder(this, filepath);
    }

    // Met une tâche sous une tâche composite
    public void deplacerTacheSousComposite(CompositeTache nouveauParent, Tache tache) {
        if (nouveauParent == null || tache == null) return;

        CompositeTache ancienParent = tache.getParentTache();
        if (ancienParent != null) {
            ancienParent.getTaches().remove(tache);
        } else {
            for (Liste l : listes) {
                if (l.getTaches().remove(tache)) break;
            }
        }

        nouveauParent.ajouterTache(tache);
        notifierObservateur();
        FichierManager.sauvegarder(this, filepath);
    }

    // Observateurs
    @Override
    public void enregistrerObservateur(Observateur o) {
        if (obs == null) obs = new ArrayList<>();
        this.obs.add(o);
    }

    @Override
    public void notifierObservateur() {
        if (obs == null) return;
        for (int i = 0; i < this.obs.size(); i++) {
            Observateur observer = this.obs.get(i);
            observer.actualiser(this);
        }
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        if (obs == null) return;
        int i = this.obs.indexOf(o);
        if (i >= 0) {
            this.obs.remove(i);
        }
    }
}
