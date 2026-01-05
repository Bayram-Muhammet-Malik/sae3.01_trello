package appSAE;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
        if (ControlerDrag.tacheEnCours != null) {
            e.acceptTransferModes(TransferMode.MOVE);
        }
        e.consume();
    }

    public void handleDragDropped(DragEvent e) {
        if (ControlerDrag.tacheEnCours != null) {

            Tache source = ControlerDrag.tacheEnCours;
            Tache cible  = this.tache;

            if (cible != null && source != cible) {
                CompositeTache ctCible;

                if (cible instanceof CompositeTache existing) {
                    ctCible = existing;
                } else {
                    ctCible = new CompositeTache(
                            cible.getTitre(),
                            cible.getDescription(),
                            cible.getDebut(),
                            cible.getFin(),
                            cible.getPriorite()
                    );

                    CompositeTache parent = cible.getParentTache();
                    if (parent != null) {
                        parent.modifierSousTache(cible, ctCible);
                    } else {
                        for (Liste l : model.getListes()) {
                            l.modifierTache(cible, ctCible);
                        }
                    }
                }

                model.deplacerTacheSousComposite(ctCible, source);

            } else {
                model.deplacerTacheDansListe(liste, source);
            }

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
