package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelTestIteration4 {

    private Model model;
    private Liste liste;
    private CompositeTache tache;
    private FakeObservateur observateur;

    private LocalDateTime debut;
    private LocalDateTime fin;

    @BeforeEach
    void setUp() {
        model = new Model();

        observateur = new FakeObservateur();
        model.enregistrerObservateur(observateur);

        liste = new Liste("Liste principale");

        debut = LocalDateTime.of(2026, 1, 5, 9, 0);
        fin   = LocalDateTime.of(2026, 1, 5, 17, 0);

        tache = new CompositeTache(
                "Tâche 1",
                "Description",
                debut,
                fin,
                Tache.Priorite.NORMAL
        );

        model.ajouterListe(liste);
        model.ajouterCarte(liste, tache);
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
        CompositeTache t2 = new CompositeTache(
                "Tâche 2",
                "Desc",
                debut,
                fin,
                Tache.Priorite.URGENT
        );

        model.ajouterCarte(liste, t2);

        assertEquals(2, liste.getTaches().size());
    }

    @Test
    void testAjouterFeuilleTacheDansComposite() {
        FeuilleTache ft = new FeuilleTache(
                "Sous-tâche",
                "Détail",
                debut.plusHours(1),
                fin.minusHours(1),
                Tache.Priorite.IMPORTANT
        );

        tache.ajouterTache(ft);

        assertEquals(1, tache.getTaches().size());
        assertEquals("Sous-tâche", tache.getTaches().get(0).getTitre());
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
