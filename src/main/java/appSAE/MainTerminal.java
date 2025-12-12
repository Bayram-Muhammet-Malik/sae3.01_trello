package appSAE;

// CLASSE TEMPORAIRE UTILISANT DES VARIABLES INTERNES POUR CREER ET PARCOURIR LA LISTE
// PLUS TARD (QUAND ON AURA LES METHODES PERMETTANT DE RECUP LES INFOS DANS LES FICHIERS)
// ON REMPLACERA LES VARIABLES INTERNES PAR L'UTILISATION DE CES METHODES ET DE CES FICHIERS LA
public class MainTerminal {

        public static void main(String[] args) {

            // On crée une liste remplie avec quelques taches pour tester l'affichage
            Liste liste = creerListeDeTest();

            // On affiche cette liste dans le terminal
            afficherListe(liste);
        }

        /**
         * Cette méthode crée une Liste avec 2 taches principales et 2 sous taches.
         * Elle sert juste à avoir un exemple visuel avant JavaFX.
         */
        private static Liste creerListeDeTest() {

            // On crée une nouvelle liste
            Liste liste = new Liste("Ma liste de test");
            
            //tache principale
            CompositeTache t1 = new CompositeTache(
                    "Préparer la soutenance",
                    "Faire un plan + des slides",
                    "XX/XX/XXXX"
            );

            // Sous tache 1
            CompositeTache t1_1 = new CompositeTache(
                    "Faire le plan",
                    "Trouver les parties",
                    "XX/XX/XXXX"
            );

            // Sous tache 2
            CompositeTache t1_2 = new CompositeTache(
                    "Faire les slides",
                    "Faire les diapositives",
                    "XX/XX/XXXX"
            );

            // On ajoute les sous taches à la tache principale
            t1.ajouterTache(t1_1);
            t1.ajouterTache(t1_2);


            // tache principale 2
            CompositeTache t2 = new CompositeTache(
                    "Faire l'affichage JavaFX",
                    "Préparer les pages et le design",
                    "XX/XX/XXXX"
            );

            // On ajoute les taches principales à la liste
            liste.getTaches().add(t1);
            liste.getTaches().add(t2);

            return liste;
        }


        /**
         * Affiche la liste dans le terminal :
         * - son titre
         * - toutes les taches principales
         * - leurs sous taches avec indentation
         */
        private static void afficherListe(Liste liste) {

            System.out.println("==============================");
            System.out.println(" Liste : " + liste.getTitre());
            System.out.println("==============================\n");

            // Si aucune tache n'a été ajoutée
            if (liste.getTaches().isEmpty()) {
                System.out.println("Aucune tache pour le moment.");
                return;
            }

            // On parcourt toutes les taches principales
            int numero = 1;
            for (CompositeTache tachePrincipale : liste.getTaches()) {

                // On affiche la tache principale
                System.out.println(numero + ". " + formatTache(tachePrincipale));

                // On affiche ses sous taches
                afficherSousTaches(tachePrincipale, "   ");

                System.out.println(); // saute une ligne
                numero++;
            }
        }


        /**
         * Affiche les sous taches avec indentation.
         * Exemple :
         *   - [ ] Faire le plan...
         *   - [ ] Faire les slides...
         */
        private static void afficherSousTaches(CompositeTache tache, String indent) {

            // Si aucune sous tache
            if (tache.getTaches().isEmpty()) {
                return;
            }

            // Pour chaque sous tache
            for (Tache t : tache.getTaches()) {

                // Comme on a que CompositeTache, on peut caster simplement
                CompositeTache sousTache = (CompositeTache) t;

                // On affiche la sous tache avec une indentation (ex: 3 espaces)
                System.out.println(indent + "- " + formatTache(sousTache));

                // Si la sous tache contient elle-même d'autres sous taches,
                // on rappelle la méthode (affichage en mode "arborescence")
                afficherSousTaches(sousTache, indent + "   ");
            }
        }


        /**
         * Transforme une tache en texte à afficher.
         * Exemple :
         * Faire les slides - Faire les diapo
         */
        private static String formatTache(CompositeTache tache) {
            String etat = tache.estFait() ? "[X]" : "[ ]";

            return etat + " " + tache.getTitre()
                    + " (" + tache.getDate() + ")"
                    + " - " + tache.getDescription();
        }
    }
    
