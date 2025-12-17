package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ControlerPopTache implements EventHandler<ActionEvent> {
    /**
     * Le modele est attribut du controleur
     */
    private Model model;
    private ArrayList<Control> JFXobj;
    private Liste liste;
    private Stage stage;

    /**
     * Constructeur
     * @param m l'objet Model
     */
    public ControlerPopTache(Model m, ArrayList<Control> JFXobj, Stage stage) {
        this.model = m;
        this.JFXobj = JFXobj;
        this.stage=stage;
    }

    public void handle(ActionEvent e) {
        //model.ajouterCarte(liste, new CompositeTache(JFXobj.get(1).get));
        stage.close();
    }
}
