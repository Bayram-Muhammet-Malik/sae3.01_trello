package exoMVC;

import javafx.scene.control.Label;

import java.util.Observer;

public class VueListe extends Label implements Observateur {
    private Modele modele;

    public VueListe(Modele modele) {
        this.modele = modele;
    }

    public void actualiser(Sujet sujet) {
        Modele modele = (Modele)sujet;
        System.out.println("actualiser " + modele);
    }
}
