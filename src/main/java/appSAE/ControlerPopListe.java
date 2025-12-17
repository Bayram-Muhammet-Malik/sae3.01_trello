package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControlerPopListe implements EventHandler<ActionEvent> {
    /**
     * Le modele est attribut du controleur
     */
    private Model model;
    private TextField titreField;
    private Stage stage;

    /**
     * Constructeur
     * @param m l'objet Model
     */
    public ControlerPopListe(Model m, TextField titreField, Stage stage) {
        this.model=m;
        this.titreField=titreField;
        this.stage=stage;
    }

    public void handle(ActionEvent e) {
        model.ajouterListe(new Liste(titreField.getText()));
        FichierManager.sauvegarder(model, model.getFilepath());
        stage.close();
    }
}
