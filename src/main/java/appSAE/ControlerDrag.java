package appSAE;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

// Gère le drag and drop d'une tâche
public class ControlerDrag {

    private Model model;
    private Tache tache;   // tâche liée à cette carte
    private Liste liste;   // liste cible quand on déplace la tâche
    private VBox carte;
    public static Tache tacheEnCours; // tâche actuellement en train d'être déplacée

    public ControlerDrag(Model model, Tache tache, Liste listeCible, VBox carte) {
        this.model = model;
        this.tache = tache;
        this.liste = listeCible;
        this.carte = carte;
    }

    // Appelé quand on survole une zone possible de dépôt avec une tâche en drag
    public void handleDragOver(DragEvent e) {
        if (ControlerDrag.tacheEnCours != null) {
            // On indique qu'on accepte le déplacement
            e.acceptTransferModes(TransferMode.MOVE);
        }
        e.consume();
    }

    // Appelé quand on lâche la souris (drop) sur une zone
    public void handleDragDropped(DragEvent e) {
        if (ControlerDrag.tacheEnCours != null) {

            Tache source = ControlerDrag.tacheEnCours; // tâche qu'on déplace
            Tache cible  = this.tache; // tâche sur laquelle on lâche

            if (cible != null && source != cible) {
                CompositeTache ctCible;

                // Si la cible est déjà une tâche composite, on la réutilise
                if (cible instanceof CompositeTache existing) {
                    ctCible = existing;
                } else {
                    // Sinon, on transforme la tâche cible en CompositeTache
                    ctCible = new CompositeTache(
                            cible.getTitre(),
                            cible.getDescription(),
                            cible.getDebut(),
                            cible.getFin(),
                            cible.getPriorite()
                    );

                    // On remplace la tâche cible par la nouvelle CompositeTache
                    CompositeTache parent = cible.getParentTache();
                    if (parent != null) {
                        // Cas où la cible était déjà une sous-tâche
                        parent.modifierSousTache(cible, ctCible);
                    } else {
                        // Cas où la cible est une tâche de liste "classique"
                        for (Liste l : model.getListes()) {
                            l.modifierTache(cible, ctCible);
                        }
                    }
                }

                // On déplace la tâche source à l'intérieur de la composite cible
                model.deplacerTacheSousComposite(ctCible, source);

            } else {
                // Si on ne drop pas sur une autre tâche, on déplace juste dans une liste
                model.deplacerTacheDansListe(liste, source);
            }

            e.setDropCompleted(true);
        } else {
            e.setDropCompleted(false);
        }
        e.consume();
    }

    // Appelé quand on commence le drag sur la carte
    public void handleDragDetected(MouseEvent e) {
        // On indique globalement quelle tâche est en cours de déplacement
        tacheEnCours = tache;
        Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        content.putString("TACHE"); // contenu symbolique, on n’utilise que l’état statique
        db.setContent(content);

        e.consume();
    }

    // Appelé quand le drag est terminé (quel que soit le résultat)
    public void setOnDragDone(DragEvent e) {
        // On réinitialise la tâche en cours de drag
        ControlerDrag.tacheEnCours = null;
        e.consume();
    }
}
