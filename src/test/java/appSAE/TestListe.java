package appSAE;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TestListe {

    @Test
    void constructeurCreeListeAvecBonTitreEtListeVide() {
        Liste liste = new Liste("À faire");

        assertEquals("À faire", liste.getTitre(),
                "Le titre doit être correctement initialisé");
        assertNotNull(liste.getTaches(),
                "La liste de tâches ne doit pas être null");
        assertTrue(liste.getTaches().isEmpty(),
                "Une nouvelle liste doit être vide");
    }

    @Test
    void changerNomModifieLeTitre() {
        Liste liste = new Liste("Ancien titre");

        liste.changerNom("Nouveau titre");

        assertEquals("Nouveau titre", liste.getTitre(),
                "changerNom doit modifier le titre");
    }

    @Test
    void ajouterCarteAjouteUneCompositeTache() {
        Liste liste = new Liste("Liste");

        CompositeTache tache = new CompositeTache(
                "Tâche 1",
                "Description",
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 5),
                Tache.Priorite.NORMAL
        );

        liste.ajouterCarte(tache);

        assertEquals(1, liste.getTaches().size(),
                "La tâche doit être ajoutée à la liste");
        assertSame(tache, liste.getTaches().get(0),
                "La tâche ajoutée doit être celle passée en paramètre");
    }

    @Test
    void supprimerTacheRetireLaTache() {
        Liste liste = new Liste("Liste");

        CompositeTache t1 = new CompositeTache(
                "Tâche 1",
                "Desc",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1),
                Tache.Priorite.NORMAL
        );

        CompositeTache t2 = new CompositeTache(
                "Tâche 2",
                "Desc",
                LocalDate.of(2025, 1, 2),
                LocalDate.of(2025, 1, 2),
                Tache.Priorite.NORMAL
        );

        liste.ajouterCarte(t1);
        liste.ajouterCarte(t2);

        assertEquals(2, liste.getTaches().size());

        liste.supprimerTache(t1);

        assertEquals(1, liste.getTaches().size(),
                "Une tâche doit rester après suppression");
        assertFalse(liste.getTaches().contains(t1),
                "La tâche supprimée ne doit plus être présente");
        assertTrue(liste.getTaches().contains(t2),
                "La tâche restante doit toujours être présente");
    }

    @Test
    void deuxListesOntDesTachesIndependantes() {
        Liste liste1 = new Liste("Liste 1");
        Liste liste2 = new Liste("Liste 2");

        CompositeTache t1 = new CompositeTache(
                "Tâche L1",
                "Desc",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1),
                Tache.Priorite.NORMAL
        );

        CompositeTache t2 = new CompositeTache(
                "Tâche L2",
                "Desc",
                LocalDate.of(2025, 1, 2),
                LocalDate.of(2025, 1, 2),
                Tache.Priorite.NORMAL
        );

        liste1.ajouterCarte(t1);
        liste2.ajouterCarte(t2);

        assertEquals(1, liste1.getTaches().size());
        assertEquals(1, liste2.getTaches().size());

        assertTrue(liste1.getTaches().contains(t1));
        assertFalse(liste1.getTaches().contains(t2));

        assertTrue(liste2.getTaches().contains(t2));
        assertFalse(liste2.getTaches().contains(t1));
    }
}
