package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ControlerProjet implements EventHandler<ActionEvent> {
    /**
     * Le modele est attribut du controleur
     */
    private Model model;

    /**
     * Constructeur
     * @param m l'objet Model
     */
    public ControlerProjet(Model m) {
        this.model=m;
    }

    public void handle(ActionEvent e) {
        Button b = (Button) e.getSource();

        switch (b.getText()) {
            case "+ Créer une liste":
                //model.ajouterTache();
                break;
        }
    }
}
