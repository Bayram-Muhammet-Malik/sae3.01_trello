package appSAE;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class VueBureau extends HBox implements Observateur {
    private final Model model;

    public VueBureau(Model model) {
        this.model = model;
        this.setSpacing(5);
    }

    @Override
    public void actualiser(Sujet sujet) {
        this.getChildren().clear();

        for (Liste ls : model.getListes()) {
            this.getChildren().add(creerColonne(ls));
        }
    }


    private VBox creerColonne(Liste ls) {
        VBox colonne = new VBox();
        colonne.setSpacing(10);
        colonne.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 12px; -fx-background-radius: 8px;");
        colonne.setPrefWidth(220);
        
        Label titre = new Label(ls.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");


        for (Tache taches : model.getTachesFromListe(ls)){
            colonne.getChildren().add(creerTache(taches));
        }

        Button creerTacheBtn = new Button("+ Créer une liste");
        creerTacheBtn.setOnAction(new ControlerProjet(model));
        creerTacheBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e5e7eb; -fx-background-radius: 8px;");

        creerTacheBtn.setOnAction(e -> {/*Truc*/});

        colonne.getChildren().addAll(titre, creerTacheBtn);
        return colonne;
    }

    private VBox creerTache(Tache tsk) {
        VBox tache = new VBox();
        tache.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-background-radius: 8px;");

        Label titre = new Label(tsk.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        tache.getChildren().add(titre);

        return tache;
    }
}
