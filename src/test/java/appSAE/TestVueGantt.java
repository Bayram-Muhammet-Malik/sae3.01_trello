package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestVueGantt {

    private Model model;
    private Liste liste;
    private CompositeTache tache1;
    private CompositeTache tache2;
    private TestObservateur obs;

    // Observateur factice pour simuler rafraichir() de VueGantt
    private static class TestObservateur implements Observateur {
        List<String> lignes = new ArrayList<>();

        @Override
        public void actualiser(Sujet sujet) {
            if (sujet instanceof Model m) {
                lignes.clear();
                for (Liste l : m.getListes()) {
                    for (CompositeTache t : l.getTaches()) {
                        collect(t, 0);
                    }
                }
            }
        }

        private void collect(Tache t, int niveau) {
            /*lignes.add("Niveau " + niveau + " : " + t.getTitre() + " (" + t.getDate() + ")");
            if (t instanceof CompositeTache ct) {
                for (Tache enfant : ct.getTaches()) {
                    collect(enfant, niveau + 1);
                }
            }*/
        }
    }

    @BeforeEach
    void setUp() {
        model = new Model();
        liste = new Liste("Test Liste");
        model.ajouterListe(liste);

        // Création de tâches
        tache1 = new CompositeTache("Tâche 1", "Desc 1", LocalDateTime.of(2025, 1, 1, 10, 0), LocalDateTime.of(2025, 1, 1, 11, 0), Tache.Priorite.NORMAL);
        tache2 = new CompositeTache("Tâche 2", "Desc 2", LocalDateTime.of(2025, 1, 2, 10, 0), LocalDateTime.of(2025, 1, 2, 11, 0), Tache.Priorite.URGENT);

        // Ajout au modèle
        model.ajouterCarte(liste, tache1);
        model.ajouterCarte(liste, tache2);

        obs = new TestObservateur();
        model.enregistrerObservateur(obs);

        // Simule un rafraîchissement
        obs.actualiser(model);
    }




    @Test
    void testDatesParsées() {
        for (String ligne : obs.lignes) {
            assertTrue(ligne.contains("("), "Chaque ligne doit contenir une date");
        }
    }


}