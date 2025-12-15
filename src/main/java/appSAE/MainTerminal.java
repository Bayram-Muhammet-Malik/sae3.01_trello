package appSAE;

import java.util.Scanner;

public class MainTerminal {

    public static void main(String[] args) {

        GestionnaireListes gestion = FichierManager.charger("listes.data");
        if (gestion == null) {
            gestion = new GestionnaireListes();
        }

        Scanner sc = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {

            System.out.println("\n===== menu listes =====");
            System.out.println("1 - afficher les listes");
            System.out.println("2 - ajouter une liste");
            System.out.println("3 - supprimer une liste");
            System.out.println("4 - ouvrir une liste");
            System.out.println("5 - ajouter tache dans une liste");
            System.out.println("6 - ajouter sous-tache dans une liste");
            System.out.println("0 - quitter");
            System.out.print("choix : ");

            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1 -> afficherListes(gestion);
                case 2 -> ajouterListe(gestion, sc);
                case 3 -> supprimerListe(gestion, sc);
                case 4 -> ouvrirListe(gestion, sc);
                case 5 -> ajouterTacheDansUneListe(gestion, sc);
                case 6 -> ajouterSousTacheDansUneListe(gestion, sc);
                case 0 -> continuer = false;
                default -> System.out.println("choix invalide.");
            }

            FichierManager.sauvegarder(gestion, "listes.data");
        }

        sc.close();
    }

    // ---------------- menu taches d'une liste ----------------

    private static void menuTaches(Liste liste, Scanner sc) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n===== liste : " + liste.getTitre() + " =====");
            System.out.println("1 - afficher les taches");
            System.out.println("2 - ajouter une tache");
            System.out.println("3 - ajouter une sous-tache");
            System.out.println("4 - supprimer une tache");
            System.out.println("0 - retour");
            System.out.print("choix : ");

            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1 -> afficherListe(liste);
                case 2 -> ajouterTache(liste, sc);
                case 3 -> ajouterSousTache(liste, sc);
                case 4 -> supprimerTache(liste, sc);
                case 0 -> continuer = false;
                default -> System.out.println("choix invalide.");
            }
        }
    }

    // ---------------- listes ----------------

    private static void afficherListes(GestionnaireListes gestion) {
        System.out.println("\n===== listes =====");

        if (gestion.getListes().isEmpty()) {
            System.out.println("(aucune liste)");
            return;
        }

        int i = 1;
        for (Liste liste : gestion.getListes()) {
            System.out.println("\n==============================");
            System.out.println(i + ") liste : " + liste.getTitre());
            System.out.println("==============================");

            if (liste.getTaches().isEmpty()) {
                System.out.println("aucune tache pour le moment.");
            } else {
                int numero = 1;
                for (CompositeTache tachePrincipale : liste.getTaches()) {
                    System.out.println(numero + ". " + formatTache(tachePrincipale));
                    afficherSousTaches(tachePrincipale, "   ");
                    System.out.println();
                    numero++;
                }
            }

            i++;
        }

        System.out.println("\n======================");
    }

    private static void ajouterListe(GestionnaireListes gestion, Scanner sc) {
        System.out.print("titre de la liste : ");
        String titre = sc.nextLine();
        gestion.ajouterListe(titre);
        System.out.println("liste ajoutee.");
    }

    private static void supprimerListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("numero de la liste a supprimer : ");
        int num = sc.nextInt();
        sc.nextLine();

        gestion.supprimerListe(num - 1);
        System.out.println("liste supprimee.");
    }

    private static void ouvrirListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("numero de la liste : ");
        int num = sc.nextInt();
        sc.nextLine();

        Liste liste = gestion.getListe(num - 1);
        if (liste == null) {
            System.out.println("numero invalide.");
            return;
        }

        menuTaches(liste, sc);
    }

    private static void afficherListe(Liste liste) {
        System.out.println("\n==============================");
        System.out.println(" liste : " + liste.getTitre());
        System.out.println("==============================\n");

        if (liste.getTaches().isEmpty()) {
            System.out.println("aucune tache pour le moment.");
            return;
        }

        int numero = 1;
        for (CompositeTache tachePrincipale : liste.getTaches()) {
            System.out.println(numero + ". " + formatTache(tachePrincipale));
            afficherSousTaches(tachePrincipale, "   ");
            System.out.println();
            numero++;
        }
    }

    // ---------------- taches ----------------

    private static String formatTache(CompositeTache tache) {
        return tache.getTitre()
                + " (" + tache.getDate() + ")"
                + " [" + tache.getPriorite() + "]"
                + " - " + tache.getDescription();
    }

    private static void ajouterTache(Liste liste, Scanner sc) {
        System.out.print("titre de la tache : ");
        String titre = sc.nextLine();

        System.out.print("description : ");
        String desc = sc.nextLine();

        System.out.print("date (jj-mm-aaaa) : ");
        String date = sc.nextLine();

        // par defaut : etat a faire, priorite normal
        CompositeTache t = new CompositeTache(
                titre,
                desc,
                date,
                EtatTache.A_FAIRE,
                Tache.Priorite.NORMAL
        );

        liste.getTaches().add(t);
        System.out.println("tache ajoutee.");
    }

    private static void supprimerTache(Liste liste, Scanner sc) {
        afficherListe(liste);
        System.out.print("numero de la tache a supprimer : ");
        int num = sc.nextInt();
        sc.nextLine();

        if (num < 1 || num > liste.getTaches().size()) {
            System.out.println("numero invalide.");
            return;
        }

        liste.getTaches().remove(num - 1);
        System.out.println("tache supprimee.");
    }

    private static void ajouterTacheDansUneListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("numero de la liste ou ajouter la tache : ");
        int num = sc.nextInt();
        sc.nextLine();

        Liste liste = gestion.getListe(num - 1);
        if (liste == null) {
            System.out.println("numero invalide.");
            return;
        }

        ajouterTache(liste, sc);
    }

    // ---------------- sous-taches ----------------

    private static void afficherSousTaches(CompositeTache tache, String indent) {
        if (tache.getTaches().isEmpty()) return;

        for (Tache t : tache.getTaches()) {
            if (t instanceof CompositeTache sousTache) {
                System.out.println(indent + "- " + formatTache(sousTache));
                afficherSousTaches(sousTache, indent + "   ");
            } else {
                System.out.println(indent + "- " + t.getTitre());
            }
        }
    }

    private static void ajouterSousTache(Liste liste, Scanner sc) {
        afficherListe(liste);
        System.out.print("numero de la tache parente : ");
        int num = sc.nextInt();
        sc.nextLine();

        if (num < 1 || num > liste.getTaches().size()) {
            System.out.println("numero invalide.");
            return;
        }

        CompositeTache parent = liste.getTaches().get(num - 1);

        System.out.print("titre de la sous-tache : ");
        String titre = sc.nextLine();

        System.out.print("description : ");
        String desc = sc.nextLine();

        System.out.print("date (jj-mm-aaaa) : ");
        String date = sc.nextLine();

        // par defaut : etat a faire, priorite normal
        CompositeTache sousTache = new CompositeTache(
                titre,
                desc,
                date,
                EtatTache.A_FAIRE,
                Tache.Priorite.NORMAL
        );

        parent.ajouterTache(sousTache);
        System.out.println("sous-tache ajoutee.");
    }

    private static void ajouterSousTacheDansUneListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("numero de la liste ou ajouter une sous-tache : ");
        int num = sc.nextInt();
        sc.nextLine();

        Liste liste = gestion.getListe(num - 1);
        if (liste == null) {
            System.out.println("numero invalide.");
            return;
        }

        ajouterSousTache(liste, sc);
    }
}
