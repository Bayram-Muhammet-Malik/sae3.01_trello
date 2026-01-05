//package appSAE;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ModelTest {
//
//    private Model model;
//    private Observateur observateur;
//    private Liste liste1, liste2, liste3;
//    private CompositeTache tache; // Utilise la vraie classe CompositeTache
//
//    @BeforeEach
//    void setUp() {
//        model = new Model();
//        observateur = new FakeObservateur();
//        model.enregistrerObservateur(observateur);
//
//        liste1 = new Liste("Liste1");
//        liste2 = new Liste("Liste2");
//        tache = new CompositeTache("T1", "desc", "01/01/2025"); // vraie classe
//        liste1.ajouterCarte(tache);
//
//        model.ajouterListe(liste1);
//        model.ajouterListe(liste2);
//    }
//
//    @Test
//    void testDeplacerTache() {
//        model.deplacerTache(liste2, tache);
//        assertEquals(1, liste2.getTaches().size());
//    }
//
//    @Test
//    void testAjouterListe() {
//        model.ajouterListe(new Liste("Liste2"));
//        assertEquals(2, model.getListes().size());
//    }
//
//    @Test
//    void testSupprimerListe() {
//        model.supprimerListe(liste1);
//        assertEquals(0, model.getListes().size());
//    }
//
//    @Test
//    void testAjouterCarte() {
//        CompositeTache t2 = new CompositeTache("T2", "d", "01"); // vraie classe
//        model.ajouterCarte(liste1, t2);
//        assertEquals(2, liste1.getTaches().size());
//    }
//
//
//    @Test
//    void testSupprimerObservateur() {
//        model.supprimerObservateur(observateur);
//        ((FakeObservateur) observateur).notifie = false;
//        assertFalse(((FakeObservateur) observateur).notifie);
//    }
//
//    // Observateur factice pour tester les notifications
//    static class FakeObservateur implements Observateur {
//        boolean notifie = false;
//
//        @Override
//        public void actualiser(Sujet s) {
//            notifie = true;
//        }
//    }
//}
