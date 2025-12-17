package appSAE;

import java.io.Serializable;

public abstract class Tache implements Serializable {
    protected String titre;
    protected String description;
    protected String date;
    protected boolean estFait;

    public enum Priorite {
        URGENT,
        NORMAL,
        SECONDAIRE
    }

    protected Priorite priorite = Priorite.NORMAL;

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
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