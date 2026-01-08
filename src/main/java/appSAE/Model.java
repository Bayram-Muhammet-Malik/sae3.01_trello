package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Model implements Sujet, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private transient List<Observateur> obs;
    private List<Liste> listes;
    private transient String filepath;
    private String lastVue = "HOME";

    /**
     * Constructeur
     */
    public Model() {
        obs = new ArrayList<>();
        listes = new ArrayList<>();
    }

    /**
     * Méthode qui initialise le Model (pour le chargement de fichier)
     * @param model Model qui aura ses données de copie dans ce Model
     * @param path Chemin du fichier
     */
    public void setModel(Model model, String path) {
        this.listes = (model != null ? model.getListes() : new ArrayList<>());
        this.lastVue = (model != null ? model.getLastVue() : "BUREAU");
        this.filepath = path;
        notifierObservateur();
    }

    /**
     * Méthode qui ajoute une Liste à la liste de Liste du Model
     * @param liste
     */
    public void ajouterListe(Liste liste){
        this.listes.add(liste);
        notifierObservateur();
    }

    /**
     * Méthode qui supprime une Liste à la liste de Liste du Model
     * @param liste
     */
    public void supprimerListe(Liste liste){
        this.listes.remove(liste);
        notifierObservateur();
    }

    /**
     * Méthode qui modifie l'attribut lastvue (qui permet de reprendre directement sur la dernière vue au chargement du fichier)
     * @param vue
     */
    public void modifierLastVue(String vue){
        if (!this.lastVue.equals(vue)){
            this.lastVue = vue;
            notifierObservateur();
        }
    }

    /**
     * Méthode qui permet de créer une Tache dans une liste
     * @param liste
     * @param titre
     * @param desc
     * @param debut
     * @param fin
     * @param prio
     * @param prerequise
     */
    public void ajouterTache(Liste liste, String titre, String desc, LocalDate debut, LocalDate fin, Tache.Priorite prio, Tache prerequise) {
        if (prio == null) prio = Tache.Priorite.NORMAL;
        FeuilleTache tache = new FeuilleTache(titre, desc, debut, fin, prio);
        tache.setPrerequise(prerequise);
        liste.ajouterCarte(tache);
        notifierObservateur();
    }

    /**
     * Méthode qui permet de créer une Sous-Tache dans une tâche
     * @param parent
     * @param titre
     * @param desc
     * @param debut
     * @param fin
     * @param prio
     * @param prerequise
     */
    public void ajouterSousTache(CompositeTache parent, String titre, String desc, LocalDate debut, LocalDate fin, Tache.Priorite prio, Tache prerequise) {
        if (prio == null) prio = Tache.Priorite.NORMAL;
        FeuilleTache tache = new FeuilleTache(titre, desc, debut, fin, prio);
        tache.setPrerequise(prerequise);
        parent.ajouterTache(tache);
        notifierObservateur();
    }

    // GETTERS
    public List<Liste> getListes() {
        return listes;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getLastVue(){ return lastVue; }

    public List<Tache> getTachesFromListe(Liste liste){
        return liste.getTaches();
    }

    /**
     * Méthode qui permet de supprimer une Tache d'une liste
     * @param liste
     * @param tache
     */
    public void supprimerTache(Liste liste, Tache tache) {
        liste.supprimerTache(tache);
        notifierObservateur();
    }

    /**
     * Méthode qui permet de supprimer une Sous Tache d'une tâche
     * @param tache
     * @param tache
     */
    public void supprimerSousTache(CompositeTache parent, Tache tache) {
        parent.supprimerTache(tache);
        notifierObservateur();
    }

    /**
     * Méthode qui permet de marquer une Tache comme fait ou non
     * @param tache
     * @param fait true si Tache faite sinon false
     */
    public void setTacheFait(Tache tache, boolean fait) {
        tache.setEstFait(fait);
        FichierManager.sauvegarder(this, filepath);
        notifierObservateur();
    }

    /**
     * Méthode qui permet de déplacer une liste
     * @param liste
     * @param nouvelIndex
     */
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

    /**
     * Méthode qui permet de déplacer une Tache d'une liste à une autre
     * @param listeCible
     * @param tache
     */
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

    /**
     * Méthode qui permet de déplacer une Tache dans une autre Tache
     * @param nouveauParent
     * @param tache
     */
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

    /**
     * Méthode qui permet d'enregistrer un observateur
     * @param o Observateur
     */
    @Override
    public void enregistrerObservateur(Observateur o) {
        if (obs == null) obs = new ArrayList<>();
        this.obs.add(o);
    }

    /**
     * Méthode qui permet d'actualiser les observateurs
     */
    @Override
    public void notifierObservateur() {
        if (obs == null) return;
        for (int i = 0; i < this.obs.size(); i++) {
            Observateur observer = this.obs.get(i);
            observer.actualiser(this);
        }
    }

    /**
     * Méthode qui permet de supprimer un observateurs
     */
    @Override
    public void supprimerObservateur(Observateur o) {
        if (obs == null) return;
        int i = this.obs.indexOf(o);
        if (i >= 0) {
            this.obs.remove(i);
        }
    }
}
