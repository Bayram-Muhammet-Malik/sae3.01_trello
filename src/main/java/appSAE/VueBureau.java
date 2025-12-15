package appSAE;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class VueBureau extends HBox implements Observateur {
    private final Model model;

    public VueBureau(Model model) {
        this.model = model;
        this.setSpacing(5);
    }

    public void actualiser(Sujet sujet) {
        this.getChildren().clear();

        for(Liste ls : model.getListes()){
            this.getChildren().add(creerColonne(ls.getTitre()));
        }

        Button ajouterBtn = new Button("+ Créer une liste");
        ajouterBtn.setOnAction(new ControlerProjet(model));
        ajouterBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e5e7eb; -fx-background-radius: 8px;");
        this.getChildren().add(ajouterBtn);
    }

    private VBox creerColonne(String titre) {
        VBox colonne = new VBox();
        colonne.setSpacing(10);
        colonne.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 12px; -fx-background-radius: 8px;");
        colonne.setPrefWidth(220);
        
        Label titreLabel = new Label(titre);
        titreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        /*
        for (model.get){
        }
         */

        Button ajouterBtn = new Button("+ Ajouter une carte");
        ajouterBtn.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold; -fx-border-color: #2563eb; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6px 12px; ");

        ajouterBtn.setOnAction(e -> {/*Truc*/});

        colonne.getChildren().addAll(titreLabel, ajouterBtn);
        return colonne;
    }

    private VBox creerTache(){
        return null;
    }
}
