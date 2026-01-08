package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

// Gère les boutons de la page d'accueil (nouveau fichier / ouvrir fichier)
public class ControlerHomeBtn implements EventHandler<ActionEvent> {

    private Model model;
    private MainWindow mainWindow;

    public ControlerHomeBtn(Model m, MainWindow window) {
        this.model = m;
        this.mainWindow = window;
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
                    // Créer un nouveau fichier .htsk
                    file = chooser.showSaveDialog(stage);
                    if (file != null) {
                        // Nouveau modèle avec 3 listes de base
                        Model nvf = new Model();
                        nvf.ajouterListe(new Liste("A faire"));
                        nvf.ajouterListe(new Liste("En cours"));
                        nvf.ajouterListe(new Liste("Terminé"));
                        nvf.modifierLastVue("BUREAU");
                        FichierManager.sauvegarder(nvf, file.getAbsolutePath());
                    }
                    break;

                case "openBtn":
                    // Ouvrir un fichier .htsk existant
                    file = chooser.showOpenDialog(stage);
                    break;
            }

            if (file != null) {
                // Charge le fichier dans le model principal
                FichierManager.charger(model, file.getAbsolutePath());
                // Affiche la dernière vue utilisée (bureau, liste, gantt)
                mainWindow.switchView(model.getLastVue());
                // Réenregistre cette vue comme dernière vue
                model.modifierLastVue(model.getLastVue());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
