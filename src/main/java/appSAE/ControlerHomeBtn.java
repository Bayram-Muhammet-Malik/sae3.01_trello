package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ControlerHomeBtn implements EventHandler<ActionEvent> {
    /**
     * Le modele est attribut du controleur
     */
    private Model model;

    /**
     * Constructeur
     * @param m l'objet Model
     */
    public ControlerHomeBtn(Model m) {
        this.model=m;
    }

    public void handle(ActionEvent e) {
        Button b = (Button) e.getSource();
        Stage stage = (Stage) b.getScene().getWindow();

        switch (b.getId()) {
            case "createBtn":
                FileChooser saveChooser = new FileChooser();
                saveChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texte", "*.txt"));
                File fileToSave = saveChooser.showSaveDialog(stage);
                if (fileToSave != null) {
                    try {
                        //non utile if (fileToSave.createNewFile());
                        // model.modifier()
                        // FichierManager charger fichier
                        System.out.println("Fichier créé : " + fileToSave.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            case "openBtn":
                FileChooser openChooser = new FileChooser();
                openChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texte", "*.txt"));
                File fileToOpen = openChooser.showOpenDialog(stage);
                if (fileToOpen != null) {
                    // FichierManager charger fichier
                    System.out.println("Fichier ouvert : " + fileToOpen.getAbsolutePath());
                    // model.charger(fileToOpen);
                }
                break;
        }
    }
}
