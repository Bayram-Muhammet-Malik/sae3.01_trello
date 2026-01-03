package appSAE;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Tache implements Serializable {
    @Serial
    protected static final long serialVersionUID = 1L;
    protected String titre, description;
    protected LocalDateTime debut, fin;
    protected boolean estFait;

    public enum Priorite {
        URGENT, IMPORTANT, NORMAL;

        public String getLabel() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    protected Priorite priorite = Priorite.NORMAL;

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
    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }
}