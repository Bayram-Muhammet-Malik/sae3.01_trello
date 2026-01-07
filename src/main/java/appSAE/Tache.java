package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    private LocalDateTime debut;
    private LocalDateTime fin;
    private Priorite priorite;
    private boolean estFait;

    private Tache prerequise;
    private CompositeTache parentTache;

    public Tache(String titre, String description, LocalDateTime debut, LocalDateTime fin, Priorite priorite) {
        this.titre = titre;
        this.description = description;
        this.debut = debut;
        this.fin = fin;
        this.priorite = priorite;
        this.estFait = false;
    }

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

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
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

    // ancien comportement + nom compatible avec ton code existant
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

    public CompositeTache getParentTache() {
        return parentTache;
    }

    public void setParentTache(CompositeTache parentTache) {
        this.parentTache = parentTache;
    }

    // gestion de la tâche préalable
    public Tache getPrerequise() {
        return prerequise;
    }

    public void setPrerequise(Tache prerequise) {
        this.prerequise = prerequise;
    }
}
