package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public abstract class Tache implements Serializable {
    @Serial
    protected static final long serialVersionUID = 1L;

    public enum Priorite {
        NORMAL("Normal"),
        IMPORTANT("Important"),
        URGENT("Urgent");

        private final String label;

        Priorite(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private String titre;
    private String description;
    private LocalDate debut;
    private LocalDate fin;
    private Priorite priorite;
    private boolean estFait;

    private Tache prerequise;
    private CompositeTache parentTache;

    /**
     * Constructeur
     * @param titre
     * @param description
     * @param debut Date de début
     * @param fin Date de fin
     * @param priorite Etiquette (Normal, Important, Urgent)
     */
    public Tache(String titre, String description, LocalDate debut, LocalDate fin, Priorite priorite) {
        this.titre = titre;
        this.description = description;
        this.debut = debut;
        this.fin = fin;
        this.priorite = (priorite == null) ? Priorite.NORMAL : priorite;
        this.estFait = false;
    }

    // GETTES et SETTERS
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDebut() {
        return debut;
    }

    public void setDebut(LocalDate debut) {
        this.debut = debut;
    }

    public LocalDate getFin() {
        return fin;
    }

    public void setFin(LocalDate fin) {
        this.fin = fin;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public boolean estFait() {
        return estFait;
    }

    public void setEstFait(boolean fait) {
        boolean ancien = this.estFait;
        this.estFait = fait;
        if (ancien == fait) return;
        mettreAJourParent();
    }

    /**
     * Méthode qui permet de mettre à jour le status fait des partents de la Tache (au travers de setEstFait(boolean fait))
     */
    protected void mettreAJourParent() {
        CompositeTache parent = getParentTache();
        if (parent == null) return;

        if (!this.estFait) {
            if (parent.estFait()) parent.setEstFait(false);
            return;
        }

        boolean tousFaits = parent.getTaches().stream().allMatch(Tache::estFait);
        if (tousFaits && !parent.estFait()) parent.setEstFait(true);
    }

    public CompositeTache getParentTache() {
        return parentTache;
    }

    public void setParentTache(CompositeTache parentTache) {
        this.parentTache = parentTache;
    }

    public Tache getPrerequise() {
        return prerequise;
    }

    public void setPrerequise(Tache prerequise) {
        this.prerequise = prerequise;
    }
}