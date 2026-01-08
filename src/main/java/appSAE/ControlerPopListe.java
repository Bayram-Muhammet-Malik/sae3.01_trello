package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class ControlerPopListe implements EventHandler<ActionEvent> {
    /**
     * Le modele est attribut du controleur
     */
    private Model model;
    private TextField titreField;
    private final Liste listeAModifier;

    /**
     * Constructeur
     * @param model
     * @param titreField TextField du nouveau titre
     * @param listeAModifier Liste qui aura sont titre modifié
     */
    public ControlerPopListe(Model model, TextField titreField, Liste listeAModifier) {
        this.model = model;
        this.titreField = titreField;
        this.listeAModifier = listeAModifier;
    }

    /**
     * Handler qui effectue la création ou modification d'une Liste
     * @param e ActionEvent
     */
    @Override
    public void handle(ActionEvent e) {
        String t = titreField.getText() == null ? "" : titreField.getText().trim();
        if (t.isBlank()) return;

        if (listeAModifier == null) {
            model.ajouterListe(new Liste(t));
        } else {
            listeAModifier.changerNom(t);
            model.notifierObservateur();
        }

        FichierManager.sauvegarder(model, model.getFilepath());
    }
}
