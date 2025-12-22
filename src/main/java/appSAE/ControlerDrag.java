package appSAE;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;

public class ControlerDrag {

    private final Model model;
    private final Tache tache;


    public ControlerDrag(Model model, Tache tache) {
        this.model = model;
        this.tache = tache;
    }

    public void handleDrag(EtatDrag etat, DragEvent e, Region displayBox) {
        switch (etat) {

            case OVER -> {
                if (e.getGestureSource() != displayBox && e.getDragboard().hasString()) {
                    e.acceptTransferModes(TransferMode.MOVE);
                }
                e.consume();
            }

            case ENTERED -> {
                if (e.getGestureSource() != displayBox && e.getDragboard().hasString()) {
                    displayBox.setStyle("-fx-border-width:2px;-fx-border-color:black;-fx-opacity:.4;");
                }
                e.consume();
            }

            case EXITED -> {
                if (!e.isAccepted()) {
                    displayBox.setStyle("-fx-border-width:2px;-fx-border-color:black;");
                }
                e.consume();
            }

            case DROPPED -> {
                Dragboard db = e.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    displayBox.setStyle("-fx-border-color:black;-fx-opacity:.4;");
                    model.deplacerTache(db.getString(), tache);
                    success = true;
                }

                e.setDropCompleted(success);
                e.consume();
            }
        }
    }
}
