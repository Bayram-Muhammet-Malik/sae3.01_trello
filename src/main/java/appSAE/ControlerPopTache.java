package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControlerPopTache implements EventHandler<ActionEvent> {

    private final Model model;
    private final TextField titreField;
    private final TextArea descField;
    private final TextField dateField;
    private final ComboBox<Tache.Priorite> prioBox;
    private final Stage stage;

    public ControlerPopTache(Model model,
                             TextField titreField,
                             TextArea descField,
                             TextField dateField,
                             ComboBox<Tache.Priorite> prioBox,
                             Stage stage) {
        this.model = model;
        this.titreField = titreField;
        this.descField = descField;
        this.dateField = dateField;
        this.prioBox = prioBox;
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent e) {

        // on lit les champs
        String titre = titreField.getText();
        String desc = descField.getText();
        String date = dateField.getText();
        Tache.Priorite prio = prioBox.getValue();

        // si aucune priorite choisie : normal
        if (prio == null) prio = Tache.Priorite.NORMAL;

        // on cree la tache
        CompositeTache tache = new CompositeTache(titre, desc, date, prio);

        // on l'ajoute dans la 1ere liste (simple)
        if (!model.getListes().isEmpty()) {
            model.getListes().get(0).ajouterCarte(tache);
        }

        // on met a jour les vues
        model.notifierObservateur();

        // on ferme la popup
        stage.close();
    }
}
