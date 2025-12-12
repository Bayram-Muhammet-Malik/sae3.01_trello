package appSAE;

import javafx.scene.control.Label;

public class VueBureau extends Label implements Observateur{
    private Model modele;

    public VueBureau(Model modele) { this.modele = modele; }

    public void actualiser(Sujet s) {
        Model modele = (Model)s;
        System.out.println("actualiser " + modele);
    }
}
