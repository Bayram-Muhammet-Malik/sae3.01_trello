package appSAE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestVueListeIteration4 {

    private Model model;
    private Liste liste;
    private final DateTimeFormatter format =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /*
     Structure texte simulant l'affichage de VueListe
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

        CompositeTache t1 = new CompositeTache(
                "Tâche aujourd'hui",
                "desc",
                aujourdHui.atTime(10, 0),
                aujourdHui.atTime(11, 0),
                Tache.Priorite.NORMAL
        );

        CompositeTache t2 = new CompositeTache(
                "Tâche demain",
                "desc",
                aujourdHui.plusDays(1).atTime(10, 0),
                aujourdHui.plusDays(1).atTime(11, 0),
                Tache.Priorite.URGENT
        );

        model.ajouterCarte(liste, t1);
        model.ajouterCarte(liste, t2);
    }

    /**
     * Simule la logique interne de VueListe sans JavaFX
     */
    private List<JourTexte> genererAgendaTexte() {
        List<JourTexte> jours = new ArrayList<>();
        LocalDate aujourdHui = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate jour = aujourdHui.plusDays(i);
            String dateJour = format.format(jour);

            JourTexte jt = new JourTexte();
            jt.date = dateJour;

            for (Tache t : liste.getTaches()) {
                String dateTache = format.format(
                        t.getDebut().toLocalDate()
                );

                if (dateJour.equals(dateTache)) {
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

    private void collecterTache(
            Tache t, int niveau, List<String> out) {

        out.add("Niveau " + niveau + " : " + t.getTitre());

        if (t instanceof CompositeTache ct) {
            for (Tache enfant : ct.getTaches()) {
                collecterTache(enfant, niveau + 1, out);
            }
        }
    }

    // ======================= TESTS =======================

    @Test
    void testNombreDeJours() {
        List<JourTexte> jours = genererAgendaTexte();
        assertEquals(7, jours.size(),
                "VueListe doit afficher 7 jours");
    }

    @Test
    void testTacheAujourdHui() {
        List<JourTexte> jours = genererAgendaTexte();

        JourTexte aujourdHui = jours.get(0);

        assertEquals(1, aujourdHui.taches.size());
        assertEquals(
                "Niveau 0 : Tâche aujourd'hui",
                aujourdHui.taches.get(0)
        );
    }

    @Test
    void testTacheDemain() {
        List<JourTexte> jours = genererAgendaTexte();

        JourTexte demain = jours.get(1);

        assertEquals(1, demain.taches.size());
        assertEquals(
                "Niveau 0 : Tâche demain",
                demain.taches.get(0)
        );
    }

    @Test
    void testJourSansTache() {
        List<JourTexte> jours = genererAgendaTexte();

        JourTexte jourSansTache = jours.get(2);

        assertEquals(1, jourSansTache.taches.size());
        assertEquals(
                "aucune tache",
                jourSansTache.taches.get(0)
        );
    }
}
