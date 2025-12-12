package appSAE;

import java.io.Serializable;

public abstract class Tache implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String titre;
    protected String description;
    protected String date;
    protected boolean estFait;

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
}