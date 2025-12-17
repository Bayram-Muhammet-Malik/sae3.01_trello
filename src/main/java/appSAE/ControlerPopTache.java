package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class ControlerPopTache implements EventHandler<ActionEvent> {
    private final Model model;
    private final TextField titreField;
    private final Liste liste;
    private final TextArea descField;
    private final DatePicker datePicker;
    private final ComboBox<Tache.Priorite> prioBox;
    private final Stage stage;

    public ControlerPopTache(Model model, Liste liste, TextField titreField, TextArea descField, DatePicker datePicker, ComboBox<Tache.Priorite> prioBox, Stage stage) {
        this.model = model;
        this.liste = liste;
        this.titreField = titreField;
        this.descField = descField;
        this.datePicker = datePicker;
        this.prioBox = prioBox;
        this.stage = stage;
    }

    @Override
    public void handle(ActionEvent e) {
        model.ajouterTache(liste, titreField.getText(), descField.getText(), datePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), prioBox.getValue());
        FichierManager.sauvegarder(model, model.getFilepath());
        stage.close();
    }
}
