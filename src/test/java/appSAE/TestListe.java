package appSAE;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestListe {

    @Test
    void constructeurCreeListeAvecBonTitreEtListeVide() {
        Liste liste = new Liste("À faire");

        Assertions.assertEquals("À faire", liste.getTitre(),
                "Le titre passé au constructeur doit être correctement enregistré.");
        Assertions.assertNotNull(liste.getTaches(),
                "La liste de tâches ne doit pas être null après construction.");
        Assertions.assertTrue(liste.getTaches().isEmpty(),
                "Une nouvelle liste doit être vide.");
    }

    @Test
    void changerNomModifieLeTitre() {
        Liste liste = new Liste("Ancien titre");

        liste.changerNom("Nouveau titre");

        Assertions.assertEquals("Nouveau titre", liste.getTitre(),
                "changerNom doit modifier le titre de la liste.");
    }

    @Test
    void changerNomPlusieursFoisFonctionne() {
        Liste liste = new Liste("Titre 1");

        liste.changerNom("Titre 2");
        Assertions.assertEquals("Titre 2", liste.getTitre());

        liste.changerNom("Titre 3");
        Assertions.assertEquals("Titre 3", liste.getTitre());
    }

    @Test
    void getTachesPermetDAjouterUneCompositeTache() {
        Liste liste = new Liste("Liste de tâches");
        CompositeTache t1 = new CompositeTache("Tâche 1", "Desc 1", "2025-01-01", null);

        // On ajoute via la liste retournée par getTaches()
        List<CompositeTache> taches = liste.getTaches();
        taches.add(t1);

        Assertions.assertEquals(1, liste.getTaches().size(),
                "Après ajout, la liste doit contenir une tâche.");
        Assertions.assertSame(t1, liste.getTaches().get(0),
                "La tâche contenue doit être exactement celle qui a été ajoutée.");
    }

    @Test
    void getTachesPermetDeSupprimerUneCompositeTache() {
        Liste liste = new Liste("Liste");
        CompositeTache t1 = new CompositeTache("Tâche 1", "Desc 1", "2025-01-01", null);
        CompositeTache t2 = new CompositeTache("Tâche 2", "Desc 2", "2025-01-02", null);

        liste.getTaches().add(t1);
        liste.getTaches().add(t2);

        Assertions.assertEquals(2, liste.getTaches().size(),
                "La liste doit contenir deux tâches après les ajouts.");

        liste.getTaches().remove(t1);

        Assertions.assertEquals(1, liste.getTaches().size(),
                "Après suppression, la liste doit contenir une seule tâche.");
        Assertions.assertFalse(liste.getTaches().contains(t1),
                "La tâche supprimée ne doit plus être présente.");
        Assertions.assertTrue(liste.getTaches().contains(t2),
                "La tâche non supprimée doit encore être présente.");
    }

    @Test
    void deuxListesOntDesListesDeTachesIndependantes() {
        Liste liste1 = new Liste("Liste 1");
        Liste liste2 = new Liste("Liste 2");

        CompositeTache t1 = new CompositeTache("Tâche L1", "Desc", "2025-01-01", null);
        CompositeTache t2 = new CompositeTache("Tâche L2", "Desc", "2025-01-02", null);

        liste1.getTaches().add(t1);
        liste2.getTaches().add(t2);

        Assertions.assertEquals(1, liste1.getTaches().size(),
                "Liste 1 doit avoir une tâche.");
        Assertions.assertEquals(1, liste2.getTaches().size(),
                "Liste 2 doit avoir une tâche différente.");

        Assertions.assertTrue(liste1.getTaches().contains(t1));
        Assertions.assertFalse(liste1.getTaches().contains(t2));

        Assertions.assertTrue(liste2.getTaches().contains(t2));
        Assertions.assertFalse(liste2.getTaches().contains(t1));
    }
}
