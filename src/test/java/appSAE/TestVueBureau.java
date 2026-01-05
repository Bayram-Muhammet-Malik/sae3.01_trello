package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TestVueBureau {
    private Model model;
    private Liste aFaire;
    private Liste enCours;

    @BeforeEach
    void setUp() {
        model = new Model();

        // Création de listes
        aFaire = new Liste("À faire");
        enCours = new Liste("En cours");

        model.ajouterListe(aFaire);
        model.ajouterListe(enCours);

        // Ajout de tâches
        model.ajouterTache(aFaire, "Tâche 1", "Description 1", LocalDateTime.of(2025, 1, 1, 10, 0), LocalDateTime.of(2025, 1, 1, 11, 0), Tache.Priorite.NORMAL);
        model.ajouterTache(enCours, "Tâche 2", "Description 2", LocalDateTime.of(2025, 1, 2, 10, 0), LocalDateTime.of(2025, 1, 2, 11, 0), Tache.Priorite.URGENT);
    }

    @Test
    void testListesAjoutees() {
        assertEquals(2, model.getListes().size(), "Le model doit contenir 2 listes");
        assertTrue(model.getListes().contains(aFaire));
        assertTrue(model.getListes().contains(enCours));
    }

    @Test
    void testTachesDansListes() {
        assertEquals(1, model.getTachesFromListe(aFaire).size(), "À faire doit contenir 1 tâche");
        assertEquals("Tâche 1", model.getTachesFromListe(aFaire).get(0).getTitre());

        assertEquals(1, model.getTachesFromListe(enCours).size(), "En cours doit contenir 1 tâche");
        assertEquals(Tache.Priorite.URGENT, model.getTachesFromListe(enCours).get(0).getPriorite());
    }

    @Test
    void testAjouterCarte() {
        CompositeTache nouvelleTache = new CompositeTache("Tâche 3", "Desc 3", LocalDateTime.of(2025, 1, 3, 10, 0), LocalDateTime.of(2025, 1, 3, 11, 0), Tache.Priorite.IMPORTANT);
        model.ajouterCarte(aFaire, nouvelleTache);

        assertEquals(2, model.getTachesFromListe(aFaire).size(), "À faire doit maintenant contenir 2 tâches");
        assertEquals("Tâche 3", model.getTachesFromListe(aFaire).get(1).getTitre());
    }

    /*
    @Test
    void testSupprimerTache() {
        CompositeTache tache = model.getTachesFromListe(aFaire).get(0);
        model.supprimerTache(aFaire, tache);

        assertEquals(0, model.getTachesFromListe(aFaire).size(), "À faire doit être vide après suppression");
    }


     */
}
