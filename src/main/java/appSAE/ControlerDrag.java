package appSAE;

import javafx.scene.input.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ControlerDrag {

    private Model model;
    private Tache tache;
    private Liste liste;
    private VBox carte;
    public static Tache tacheEnCours;


    public ControlerDrag(Model model, Tache tache, Liste listeCible, VBox carte) {
        this.model = model;
        this.tache = tache;
        this.liste = listeCible;
        this.carte = carte;
    }

    public void handleDragOver(DragEvent e) {
        if (tache != null) {
            e.acceptTransferModes(TransferMode.MOVE);
        }
        e.consume();
    }

    public void handleDragEntered(DragEvent e) {
        if (e.getGestureSource() != carte && e.getDragboard().hasString()) {
            carte.setStyle("");
        }
        e.consume();
    }

    public void handleDragExited(DragEvent e) {
        if (!e.isAccepted()) {
            carte.setStyle("-fx-border-width:2px;-fx-border-color:black;");
        }
        e.consume();
    }

    public void handleDragDropped(DragEvent e) {
        if (tache != null){
            model.deplacerTache(liste, tache);

            e.setDropCompleted(true);
        } else {
            e.setDropCompleted(false);
        }
        e.consume();
    }

    public void handleDragDetected(MouseEvent e) {
        tacheEnCours = tache;
        Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        content.putString("TACHE");
        db.setContent(content);

        e.consume();
    }

    public void setOnDragDone(DragEvent e) {
        ControlerDrag.tacheEnCours = null;
        e.consume();
    }
}
