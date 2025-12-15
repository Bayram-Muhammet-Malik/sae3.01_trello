package appSAE;

import java.util.Scanner;

public class MainTerminal {

    public static void main(String[] args) {
        GestionnaireListes gestion = FichierManager.charger("listes.data");
        //GestionnaireListes gestion = new GestionnaireListes();

        Scanner sc = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {

            System.out.println("\n===== MENU LISTES =====");
            System.out.println("1 - Afficher les listes");
            System.out.println("2 - Ajouter une liste");
            System.out.println("3 - Supprimer une liste");
            System.out.println("4 - Ouvrir une liste");
            System.out.println("5 - Ajouter tâche");
            System.out.println("6 - Ajouter sous-tâche");
            System.out.println("0 - Quitter");
            System.out.print("Choix : ");

            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {

                case 1:
                    afficherListes(gestion);
                    break;

                case 2:
                    ajouterListe(gestion, sc);
                    break;

                case 3:
                    supprimerListe(gestion, sc);
                    break;

                case 4:
                    ouvrirListe(gestion, sc);
                    break;

                case 5:
                    ajouterTacheDansUneListe(gestion, sc);
                    break;

                case 6:
                    ajouterSousTacheDansUneListe(gestion, sc);
                    break;
                case 0:
                    continuer = false;
                    break;

                default:
                    System.out.println("Choix invalide.");
            }

            FichierManager.sauvegarder(gestion, "listes.data");
        }

        sc.close();
    }








    private static void menuTaches(Liste liste, Scanner sc) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n===== LISTE : " + liste.getTitre() + " =====");
            System.out.println("1 - Afficher les tâches");
            System.out.println("2 - Ajouter une tâche");
            System.out.println("3 - Ajouter une sous-tâche");
            System.out.println("4 - Supprimer une tâche");
            System.out.println("0 - Retour");
            System.out.print("Choix : ");

            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1:
                    afficherListe(liste);
                    break;
                case 2:
                    ajouterTache(liste, sc);
                    break;
                case 3:
                    ajouterSousTache(liste, sc);
                    break;
                case 4:
                    supprimerTache(liste, sc);
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }


    /**************** LISTE*************/

            // On crée une nouvelle liste
            Liste liste = new Liste("Ma liste de test");
            
            //tache principale
            CompositeTache t1 = new CompositeTache(
                    "Préparer la soutenance",
                    "Faire un plan + des slides",
                    "XX/XX/XXXX",
                    null
            );

    //afficher les listes
    private static void afficherListes(GestionnaireListes gestion) {
        System.out.println("\n===== LISTES =====");
        int i = 1;
        for (Liste liste : gestion.getListes()) {
            System.out.println("==============================");
            System.out.println(" Liste : " + liste.getTitre());
            System.out.println("==============================\n");

            if (liste.getTaches().isEmpty()) {
                System.out.println("Aucune tache pour le moment.");
                return;
            }

            int numero = 1;
            for (CompositeTache tachePrincipale : liste.getTaches()) {
                System.out.println(numero + ". " + formatTache(tachePrincipale));
                afficherSousTaches(tachePrincipale, "   ");
                System.out.println();
                numero++;
            }
            i++;
        }
        if (gestion.getListes().isEmpty()) {
            System.out.println("(Aucune liste)");
        }
        System.out.println("\n======================");
    }

    private static void ajouterListe(GestionnaireListes gestion, Scanner sc) {
        System.out.print("Titre de la liste : ");
        String titre = sc.nextLine();
        gestion.ajouterListe(titre);
        System.out.println("Liste ajoutée !!!!!!!!");
    }

    private static void supprimerListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("Numéro de la liste à supprimer : ");
        int num = sc.nextInt();
        sc.nextLine();
        gestion.supprimerListe(num - 1);
        System.out.println("Liste supprimée.");
    }

    private static void ouvrirListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("Numéro de la liste : ");
        int num = sc.nextInt();
        sc.nextLine();

        Liste liste = gestion.getListe(num-1);
        if (liste == null) {
            System.out.println("Numéro invalide.");
            return;
        }

        menuTaches(liste, sc);
    }

    //afficher une liste
    private static void afficherListe(Liste liste) {
        System.out.println("==============================");
        System.out.println(" Liste : " + liste.getTitre());
        System.out.println("==============================\n");

        if (liste.getTaches().isEmpty()) {
            System.out.println("Aucune tache pour le moment.");
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



    /****************TACHE*************/



    private static String formatTache(CompositeTache tache) {
        return " " + tache.getTitre() + " (" + tache.getDate() + ")" + " - " + tache.getDescription();
    }

    private static void ajouterTache(Liste liste, Scanner sc) {
        System.out.print("Titre de la tâche : ");
        String titre = sc.nextLine();

        System.out.print("Description : ");
        String desc = sc.nextLine();

        System.out.print("Date (JJ/MM/AAAA) : ");
        String date = sc.nextLine();

        CompositeTache t = new CompositeTache(titre, desc, date, null);
        liste.getTaches().add(t);

        System.out.println("Tâche " + titre + " ajoutée !");
    }


    private static void supprimerTache(Liste liste, Scanner sc) {
        afficherListe(liste);
        System.out.print("Numéro de la tâche à supprimer : ");
        int num = sc.nextInt();
        sc.nextLine();

        if (num < 1 || num > liste.getTaches().size()) {
            System.out.println("Numéro invalide.");
            return;
        }

        liste.getTaches().remove(num - 1);
        System.out.println("Tâche supprimée.");
    }


    private static void ajouterTacheDansUneListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("Numéro de la liste où ajouter la tâche : ");
        int num = sc.nextInt();
        sc.nextLine();

        Liste liste = gestion.getListe(num - 1);
        if (liste == null) {
            System.out.println("Numéro invalide.");
            return;
        }

        ajouterTache(liste, sc);
    }


    /****************** SOUS TACHE*************/



    private static void afficherSousTaches(CompositeTache tache, String indent) {
        if (tache.getTaches().isEmpty()) return;

        for (Tache t : tache.getTaches()) {
            CompositeTache sousTache = (CompositeTache) t;
            System.out.println(indent + "- " + formatTache(sousTache));
            afficherSousTaches(sousTache, indent + "   ");
        }
    }

    private static void ajouterSousTache(Liste liste, Scanner sc) {
        afficherListe(liste);
        System.out.print("Numéro de la tâche parente : ");
        int num = sc.nextInt();
        sc.nextLine();

        if (num < 1 || num > liste.getTaches().size()) {
            System.out.println("Numéro invalide.");
            return;
        }

        CompositeTache parent = liste.getTaches().get(num - 1);

        System.out.print("Titre de la sous-tâche : ");
        String titre = sc.nextLine();

        System.out.print("Description : ");
        String desc = sc.nextLine();

        System.out.print("Date (JJ/MM/AAAA) : ");
        String date = sc.nextLine();

        CompositeTache sousTache = new CompositeTache(titre, desc, date, null);
        parent.ajouterTache(sousTache);

        System.out.println("Sous-tâche ajoutée !");
    }

    private static void ajouterSousTacheDansUneListe(GestionnaireListes gestion, Scanner sc) {
        afficherListes(gestion);
        System.out.print("Numéro de la liste où ajouter une sous-tâche : ");
        int num = sc.nextInt();
        sc.nextLine();

        Liste liste = gestion.getListe(num - 1);
        if (liste == null) {
            System.out.println("Numéro invalide.");
            return;
        }

        ajouterSousTache(liste, sc);
    }
}
