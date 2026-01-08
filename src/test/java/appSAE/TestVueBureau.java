package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TestVueBureau {
    private Model model;
    private Liste aFaire;
    private Liste enCours;

    @BeforeEach
    void setUp() {
        model = new Model();

        // Création des listes
        aFaire = new Liste("À faire");
        enCours = new Liste("En cours");

        model.ajouterListe(aFaire);
        model.ajouterListe(enCours);

        model.ajouterTache(aFaire, "Tâche 1",
                "Description 1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1),
                Tache.Priorite.NORMAL, null);
        model.ajouterTache(enCours, "Tâche 2",
                "Description 2",
                LocalDate.of(2025, 1, 2),
                LocalDate.of(2025, 1, 2),
                Tache.Priorite.URGENT, null);
    }

    @Test
    void testListesAjouteesAuModel() {
        assertEquals(2, model.getListes().size(),
                "Le modèle doit contenir deux listes");

        assertTrue(model.getListes().contains(aFaire));
        assertTrue(model.getListes().contains(enCours));
    }

    @Test
    void testTachesDansChaqueListe() {
        assertEquals(1, model.getTachesFromListe(aFaire).size(),
                "La liste À faire doit contenir une tâche");

        Tache tacheAFaire = model.getTachesFromListe(aFaire).get(0);
        assertEquals("Tâche 1", tacheAFaire.getTitre());
        assertEquals(Tache.Priorite.NORMAL, tacheAFaire.getPriorite());

        assertEquals(1, model.getTachesFromListe(enCours).size(),
                "La liste En cours doit contenir une tâche");

        Tache tacheEnCours = model.getTachesFromListe(enCours).get(0);
        assertEquals(Tache.Priorite.URGENT, tacheEnCours.getPriorite());
    }

    @Test
    void testAjouterCarteDepuisVueBureau() {
        CompositeTache nouvelleTache = new CompositeTache(
                "Tâche 3",
                "Desc 3",
                LocalDate.of(2025, 1, 3),
                LocalDate.of(2025, 1, 3),
                Tache.Priorite.IMPORTANT
        );

        model.ajouterTache(aFaire, "Tâche 3",
                "Desc 3",
                LocalDate.of(2025, 1, 3),
                LocalDate.of(2025, 1, 3),
                Tache.Priorite.IMPORTANT, null);

        assertEquals(2, model.getTachesFromListe(aFaire).size(),
                "La liste À faire doit contenir deux tâches");

        assertEquals("Tâche 3",
                model.getTachesFromListe(aFaire).get(1).getTitre());
    }

    @Test
    void testSupprimerTacheDepuisVueBureau() {
        Tache tache = model.getTachesFromListe(aFaire).get(0);

        model.supprimerTache(aFaire, tache);

        assertTrue(model.getTachesFromListe(aFaire).isEmpty(),
                "La liste À faire doit être vide après suppression");
    }
}
