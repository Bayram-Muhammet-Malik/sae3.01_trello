package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

// Représente une tâche de base (titre, dates, priorité, état...), commune à toutes les tâches
public abstract class Tache implements Serializable {
    @Serial
    protected static final long serialVersionUID = 1L;

    // Niveau d'urgence / importance de la tâche
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

    // Tâche qui doit être faite avant celle-ci (optionnel)
    private Tache prerequise;
    // Tâche parente si celle-ci est une sous-tâche
    private CompositeTache parentTache;

    public Tache(String titre, String description, LocalDate debut, LocalDate fin, Priorite priorite) {
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

    // Change l'état de la tâche (faite / pas faite) et met à jour la tâche parente si besoin
    public void setEstFait(boolean fait) {
        boolean ancien = this.estFait;
        this.estFait = fait;
        if (ancien == fait) return;  // si rien n'a changé, on ne fait rien
        mettreAJourParent();
    }

    // Met à jour l'état de la tâche parente en fonction des sous-tâches
    protected void mettreAJourParent() {
        CompositeTache parent = getParentTache();
        if (parent == null) return;

        // Si cette tâche repasse à "non fait", on décoche le parent
        if (!this.estFait) {
            if (parent.estFait()) parent.setEstFait(false);
            return;
        }

        // Si toutes les sous-tâches sont faites, on coche le parent
        boolean tousFaits = parent.getTaches().stream().allMatch(Tache::estFait);
        if (tousFaits && !parent.estFait()) parent.setEstFait(true);
    }

    public CompositeTache getParentTache() {
        return parentTache;
    }

    public void setParentTache(CompositeTache parentTache) {
        this.parentTache = parentTache;
    }

    // Tâche qui doit être terminée avant celle-ci (dépendance)
    public Tache getPrerequise() {
        return prerequise;
    }

    public void setPrerequise(Tache prerequise) {
        this.prerequise = prerequise;
    }
}
