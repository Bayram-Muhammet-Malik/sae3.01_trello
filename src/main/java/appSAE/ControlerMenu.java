package appSAE;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ControlerMenu implements EventHandler<ActionEvent> {
    /**
     * Le modele est attribut du controleur
     */
    private Model model;
    private MainWindow mainWindow;

    /**
     * Constructeur
     * @param m l'objet Model
     */
    public ControlerMenu(Model m, MainWindow window) {
        this.model=m;
        this.mainWindow=window;
    }

    /**
     * Handler qui permet de changer la Vue afficher
     * @param e ActionEvent
     */
    public void handle(ActionEvent e) {
        Button b = (Button) e.getSource();
        mainWindow.switchView(b.getId());
        model.modifierLastVue(b.getId());
    }
}
