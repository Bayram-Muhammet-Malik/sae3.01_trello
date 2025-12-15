package appSAE;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainBureau extends Application {

    @Override
    public void start(Stage primaryStage) {

        // ----- modele -----
        Model model = new Model();

        // ===== colonne 1 : a faire =====
        Liste aFaire = new Liste("a faire");

        CompositeTache t1 = new CompositeTache(
                "titre de la tache 1",
                "description courte de la tache 1.",
                "15-12-2025",
                EtatTache.A_FAIRE,
                Tache.Priorite.NORMAL
        );

        CompositeTache t2 = new CompositeTache(
                "titre de la tache a",
                "description courte de la tache a.",
                "16-12-2025",
                EtatTache.EN_COURS,
                Tache.Priorite.URGENT
        );

        CompositeTache t3 = new CompositeTache(
                "titre de la tache x",
                "description courte de la tache x.",
                "17-12-2025",
                EtatTache.EN_REVUE,
                Tache.Priorite.SECONDAIRE
        );

        CompositeTache t4 = new CompositeTache(
                "titre de la tache z",
                "description courte de la tache z.",
                "18-12-2025",
                EtatTache.TERMINE,
                Tache.Priorite.NORMAL
        );

        aFaire.ajouterTache(t1);
        aFaire.ajouterTache(t2);
        aFaire.ajouterTache(t3);
        aFaire.ajouterTache(t4);

        // ===== colonne 2 : en cours =====
        Liste enCours = new Liste("en cours");

        CompositeTache tA = new CompositeTache(
                "titre de la tache a",
                "description courte de la tache a.",
                "16-12-2025",
                EtatTache.EN_COURS
        ); // priorite normal par defaut

        CompositeTache tB = new CompositeTache(
                "titre de la tache b",
                "description courte de la tache b.",
                "16-12-2025",
                EtatTache.EN_COURS,
                Tache.Priorite.SECONDAIRE
        );

        enCours.ajouterTache(tA);
        enCours.ajouterTache(tB);

        // ===== colonne 3 : en revue =====
        Liste enRevue = new Liste("en revue");

        CompositeTache tX = new CompositeTache(
                "titre de la tache x",
                "description courte de la tache x.",
                "17-12-2025",
                EtatTache.EN_REVUE
        );

        enRevue.ajouterTache(tX);

        // ===== colonne 4 : termine =====
        Liste termine = new Liste("termine");

        CompositeTache tZ = new CompositeTache(
                "titre de la tache z",
                "description courte de la tache z.",
                "18-12-2025",
                EtatTache.TERMINE,
                Tache.Priorite.URGENT
        );

        termine.ajouterTache(tZ);

        // ajout des listes au modele (ordre = colonnes)
        model.ajouterListe(aFaire);
        model.ajouterListe(enCours);
        model.ajouterListe(enRevue);
        model.ajouterListe(termine);

        // ----- vue bureau -----
        VueBureau vueBureau = new VueBureau(model);
        model.enregistrerObservateur(vueBureau);

        Scene scene = new Scene(vueBureau, 1200, 650);
        primaryStage.setTitle("affichage bureau - tableau : projet sae");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
