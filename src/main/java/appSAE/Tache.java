package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Tache implements Serializable {
    @Serial
    protected static final long serialVersionUID = 1L;
    private String titre, description;
    private LocalDateTime debut, fin;
    private boolean estFait;

    public enum Priorite {
        URGENT, IMPORTANT, NORMAL;

        public String getLabel() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    private Priorite priorite = Priorite.NORMAL;
    private CompositeTache parentTache = null;

    /**
     * Contructeur
     */
    public Tache(String titre, String description, LocalDateTime debut, LocalDateTime fin, Priorite prio) {
        this.titre = titre;
        this.description = description;
        this.estFait = false;
        this.debut = debut;
        this.fin = fin;
        this.priorite = (prio == null) ? Priorite.NORMAL : prio;
    }

    // GETTERS
    public String getTitre() {
        return titre;
    }
    public String getDescription() {
        return description;
    }

    public LocalDateTime getDebut() {
        return debut;
    }
    public LocalDateTime getFin() {
        return fin;
    }

    public boolean estFait() {
        return estFait;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public CompositeTache getParentTache() {
        return parentTache;
    }

    public void setParentTache(CompositeTache parentTache) {
        this.parentTache = parentTache;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }
    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public void setEstFait(boolean fait) {
        boolean ancien = this.estFait;
        this.estFait = fait;
        if (ancien == fait) return;
        mettreAJourParent();
    }

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

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }
}