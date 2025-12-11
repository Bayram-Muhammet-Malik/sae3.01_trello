package appSAE;

public abstract class Tache {
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