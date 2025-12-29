package appSAE;

public class FeuilleTache extends Tache {

    public FeuilleTache(String titre, String description, String date) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.estFait = false;
    }
}
