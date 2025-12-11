package appSAE;

import javafx.scene.control.Label;

import java.util.Observer;

public class VueListe extends Label implements Observateur {
    private Model modele;

    public VueListe(Model modele) {
        this.modele = modele;
    }

    public void actualiser(Sujet sujet) {
        Model modele = (Model)sujet;
        System.out.println("actualiser " + modele);
    }
}
