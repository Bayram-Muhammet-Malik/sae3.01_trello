package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private Model model;
    private Observateur observateur;
    private Liste liste;
    private CompositeTache tache; // Utilise la vraie classe CompositeTache

    @BeforeEach
    void setUp() {
        model = new Model();
        observateur = new FakeObservateur();
        model.enregistrerObservateur(observateur);

        liste = new Liste("Liste1");
        tache = new CompositeTache("T1", "desc", "01/01/2025"); // vraie classe
        liste.ajouterCarte(tache);
        model.ajouterListe(liste);
    }

    @Test
    void testAjouterListe() {
        model.ajouterListe(new Liste("Liste2"));
        assertEquals(2, model.getListes().size());
    }

    @Test
    void testSupprimerListe() {
        model.supprimerListe(liste);
        assertEquals(0, model.getListes().size());
    }

    @Test
    void testModifierPath() {
        model.modifierPath("data.txt");
        assertEquals("data.txt", model.getFilepath());
        assertTrue(((FakeObservateur) observateur).notifie);
    }

    @Test
    void testModifierDate() {
        model.modifierDate("T1", "02/02/2025");
        assertEquals("02/02/2025", tache.getDate());
    }

    @Test
    void testModifierDescription() {
        model.modifierDescription("T1", "nouvelle description");
        assertEquals("nouvelle description", tache.getDescription());
    }

    @Test
    void testModifierTout() {
        model.modifierTout("T1", "desc2", "03/03/2025");
        assertEquals("desc2", tache.getDescription());
        assertEquals("03/03/2025", tache.getDate());
        assertEquals("T1", tache.getTitre());
    }

    @Test
    void testAjouterCarte() {
        CompositeTache t2 = new CompositeTache("T2", "d", "01"); // vraie classe
        model.ajouterCarte(liste, t2);
        assertEquals(2, liste.getTaches().size());
    }


    @Test
    void testSupprimerObservateur() {
        model.supprimerObservateur(observateur);
        ((FakeObservateur) observateur).notifie = false;
        model.modifierPath("test.txt");
        assertFalse(((FakeObservateur) observateur).notifie);
    }

    // Observateur factice pour tester les notifications
    static class FakeObservateur implements Observateur {
        boolean notifie = false;

        @Override
        public void actualiser(Sujet s) {
            notifie = true;
        }
    }
}
