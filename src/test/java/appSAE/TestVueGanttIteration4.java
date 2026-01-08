package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TestVueGanttIteration4 {

    private Model model;
    private Liste liste;

    @BeforeEach
    void setUp() {
        model = new Model();
        liste = new Liste("Planning");

        model.ajouterListe(liste);

        model.ajouterTache(
                liste,
                "Analyse",
                "Analyse du sujet",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1),
                Tache.Priorite.NORMAL
        );

        model.ajouterTache(
                liste,
                "Développement",
                "Coder la solution",
                LocalDate.of(2025, 1, 2),
                LocalDate.of(2025, 1, 2),
                Tache.Priorite.IMPORTANT
        );
    }

    @Test
    void testListeExiste() {
        assertEquals(1, model.getListes().size());
        assertEquals("Planning", model.getListes().get(0).getTitre());
    }

    @Test
    void testTachesDansListe() {
        assertEquals(2, model.getTachesFromListe(liste).size());
    }

    @Test
    void testDatesDesTaches() {
        Tache tache = model.getTachesFromListe(liste).get(0);

        assertNotNull(tache.getDebut());
        assertNotNull(tache.getFin());
        assertTrue(tache.getDebut().isBefore(tache.getFin()));
    }

    @Test
    void testPrioriteTache() {
        Tache tache = model.getTachesFromListe(liste).get(1);
        assertEquals(Tache.Priorite.IMPORTANT, tache.getPriorite());
    }
}
