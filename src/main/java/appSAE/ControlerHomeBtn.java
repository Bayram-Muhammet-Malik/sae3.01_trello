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
    private MainWindow mainWindow;

    /**
     * Constructeur
     * @param m l'objet Model
     */
    public ControlerHomeBtn(Model m, MainWindow window) {
        this.model=m;
        this.mainWindow=window;
    }

    @Override
    public void handle(ActionEvent e) {
        Button b = (Button) e.getSource();
        Stage stage = (Stage) b.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier HiTask", "*.htsk"));
        File file = null;

        try {
            switch (b.getId()) {
                case "createBtn":
                    file = chooser.showSaveDialog(stage);
                    if (file != null) file.createNewFile();
                    break;
                case "openBtn":
                    file = chooser.showOpenDialog(stage);
                    break;
            }

            if (file != null) {
                FichierManager.charger(model, file.getAbsolutePath());
                mainWindow.switchView(model.getLastVue());
                model.modifierLastVue(model.getLastVue());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
