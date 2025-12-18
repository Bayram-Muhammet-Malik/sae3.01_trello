package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestVueListe {

    private Model model;
    private Liste liste;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /*
     Structure texte simulant l'affichage de VueListe
     (jour -> tâches -> sous-tâches)
     */
    private static class JourTexte {
        String date;
        List<String> taches = new ArrayList<>();
    }

    @BeforeEach
    void setUp() {
        model = new Model();
        liste = new Liste("Agenda");
        model.ajouterListe(liste);

        LocalDate aujourdHui = LocalDate.now();

        // Tâche du jour
        CompositeTache t1 = new CompositeTache(
                "Tâche jour",
                "desc",
                format.format(aujourdHui),
                Tache.Priorite.NORMAL
        );


        // Tâche demain
        CompositeTache t2 = new CompositeTache(
                "Tâche demain",
                "desc",
                format.format(aujourdHui.plusDays(1)),
                Tache.Priorite.URGENT
        );

        model.ajouterCarte(liste, t1);
        model.ajouterCarte(liste, t2);
    }

    /**
     * Simule la logique interne de VueListe (sans JavaFX)
     */
    private List<JourTexte> genererAgendaTexte() {
        List<JourTexte> jours = new ArrayList<>();
        LocalDate aujourdHui = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate jour = aujourdHui.plusDays(i);
            String dateJour = format.format(jour);

            JourTexte jt = new JourTexte();
            jt.date = dateJour;

            for (CompositeTache t : liste.getTaches()) {
                if (dateJour.equals(t.getDate())) {
                    collecterTache(t, 0, jt.taches);
                }
            }

            if (jt.taches.isEmpty()) {
                jt.taches.add("aucune tache");
            }

            jours.add(jt);
        }

        return jours;
    }

    private void collecterTache(Tache t, int niveau, List<String> out) {
        out.add("Niveau " + niveau + " : " + t.getTitre());

        if (t instanceof CompositeTache ct) {
            for (Tache enfant : ct.getTaches()) {
                collecterTache(enfant, niveau + 1, out);
            }
        }
    }

    // ========================= TESTS =========================

    @Test
    void testNombreDeJours() {
        List<JourTexte> jours = genererAgendaTexte();
        assertEquals(7, jours.size(), "VueListe doit afficher 7 jours");
    }



    @Test
    void testTacheDemain() {
        List<JourTexte> jours = genererAgendaTexte();

        JourTexte demain = jours.get(1);

        assertEquals(1, demain.taches.size());
        assertEquals("Niveau 0 : Tâche demain", demain.taches.get(0));
    }

    @Test
    void testJourSansTache() {
        List<JourTexte> jours = genererAgendaTexte();

        JourTexte jourSansTache = jours.get(2);

        assertEquals(1, jourSansTache.taches.size());
        assertEquals("aucune tache", jourSansTache.taches.get(0));
    }


}
