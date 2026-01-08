package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    private Model model;
    private Liste liste;
    private FakeObservateur observateur;
    private CompositeTache tache;

    private LocalDate debut;
    private LocalDate fin;

    @BeforeEach
    void setUp() {
        model = new Model();

        observateur = new FakeObservateur();
        model.enregistrerObservateur(observateur);

        liste = new Liste("Liste principale");

        debut = LocalDate.of(2026, 1, 5);
        fin   = LocalDate.of(2026, 1, 5);

        model.ajouterListe(liste);
        model.ajouterTache(liste, "Tâche 1",
                "Description",
                debut,
                fin,
                Tache.Priorite.NORMAL, null);
        liste.modifierTache(model.getTachesFromListe(liste).getFirst(), new CompositeTache("Tâche 1",
                "Description",
                debut,
                fin,
                Tache.Priorite.NORMAL));
        tache = (CompositeTache) model.getTachesFromListe(liste).getFirst();
    }

    // ======================= LISTES =======================

    @Test
    void testAjouterListe() {
        model.ajouterListe(new Liste("Nouvelle liste"));
        assertEquals(2, model.getListes().size());
    }

    @Test
    void testSupprimerListe() {
        model.supprimerListe(liste);
        assertTrue(model.getListes().isEmpty());
    }

    // ======================= TACHES =======================

    @Test
    void testAjouterCompositeTache() {
        model.ajouterTache(liste, "Tâche 2",
                "Desc",
                debut,
                fin,
                Tache.Priorite.URGENT, null);

        assertEquals(2, liste.getTaches().size());
    }

    @Test
    void testSupprimerFeuilleTache() {
        FeuilleTache ft = new FeuilleTache(
                "À supprimer",
                "Test",
                debut,
                fin,
                null
        );

        tache.ajouterTache(ft);
        tache.supprimerTache(ft);

        assertTrue(tache.getTaches().isEmpty());
    }

    // ======================= PRIORITÉ =======================

    @Test
    void testPrioriteParDefaut() {
        FeuilleTache ft = new FeuilleTache(
                "Sans prio",
                "desc",
                debut,
                fin,
                null
        );

        assertEquals(Tache.Priorite.NORMAL, ft.getPriorite());
    }

    @Test
    void testChangerPriorite() {
        tache.setPriorite(Tache.Priorite.URGENT);
        assertEquals(Tache.Priorite.URGENT, tache.getPriorite());
    }

    // ======================= OBSERVATEUR =======================

    @Test
    void testNotificationObservateur() {
        assertTrue(observateur.notifie,
                "L'observateur doit être notifié lors des modifications du modèle");
    }

    @Test
    void testSupprimerObservateur() {
        model.supprimerObservateur(observateur);
        observateur.notifie = false;

        model.ajouterListe(new Liste("Test"));

        assertFalse(observateur.notifie,
                "L'observateur ne doit plus être notifié");
    }

    // ======================= OBSERVATEUR FACTICE =======================

    static class FakeObservateur implements Observateur {
        boolean notifie = false;

        @Override
        public void actualiser(Sujet sujet) {
            notifie = true;
        }
    }
}
