package appSAE;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainBureau extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ----- Modèle -----
        Model model = new Model();

        // ===== Colonne 1 : À faire =====
        Liste aFaire = new Liste("À faire");
        CompositeTache t1 = new CompositeTache(
                "Titre de la tâche 1",
                "Description courte de la tâche 1.",
                "15-12-2025",
                false,
                EtatTache.A_FAIRE
        );

        CompositeTache t2 = new CompositeTache(
                "Titre de la tâche A",
                "Description courte de la tâche A.",
                "16-12-2025",
                false,
                EtatTache.EN_COURS
        );

        CompositeTache t3 = new CompositeTache(
                "Titre de la tâche X",
                "Description courte de la tâche X.",
                "17-12-2025",
                false,
                EtatTache.EN_REVUE
        );

        CompositeTache t4 = new CompositeTache(
                "Titre de la tâche Z",
                "Description courte de la tâche Z.",
                "18-12-2025",
                false,
                EtatTache.TERMINE
        );


        aFaire.ajouterTache(t1);
        aFaire.ajouterTache(t2);
        aFaire.ajouterTache(t3);
        aFaire.ajouterTache(t4);

        // ===== Colonne 2 : En cours =====
        Liste enCours = new Liste("En cours");
        CompositeTache tA = new CompositeTache(
                "Titre de la tâche A",
                "Description courte de la tâche A.",
                "16-12-2025"
        );
        CompositeTache tB = new CompositeTache(
                "Titre de la tâche B",
                "Description courte de la tâche B.",
                "16-12-2025"
        );
        enCours.ajouterTache(tA);
        enCours.ajouterTache(tB);

        // ===== Colonne 3 : En revue =====
        Liste enRevue = new Liste("En revue");
        CompositeTache tX = new CompositeTache(
                "Titre de la tâche X",
                "Description courte de la tâche X.",
                "17-12-2025"
        );
        enRevue.ajouterTache(tX);

        // ===== Colonne 4 : Terminé =====
        Liste termine = new Liste("Terminé");
        CompositeTache tZ = new CompositeTache(
                "Titre de la tâche Z",
                "Description courte de la tâche Z.",
                "18-12-2025"
        );
        termine.ajouterTache(tZ);

        // Ajout des listes au modèle (ordre = colonnes de gauche à droite)
        model.ajouterListe(aFaire);
        model.ajouterListe(enCours);
        model.ajouterListe(enRevue);
        model.ajouterListe(termine);

        // ----- Vue Bureau (maquette Kanban) -----
        VueBureau vueBureau = new VueBureau(model);
        model.enregistrerObservateur(vueBureau);

        Scene scene = new Scene(vueBureau, 1200, 650);
        primaryStage.setTitle("Affichage bureau - Tableau : Projet SAE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);   // lance l'affichage bureau
    }
}
