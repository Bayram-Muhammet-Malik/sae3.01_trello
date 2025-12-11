package appSAE;

public class MainTerminal {

    public static void main(String[] args) {
        // 1) Créer une liste d'exemple
        //    Appeler une méthode privée qui retourne une Liste remplie de tâches
        //       Liste liste = creerListeDeTest();

        // 2) Afficher le contenu de cette liste dans le terminal
        //    Appeler une méthode privée d'affichage (si on en a pas)
        //       afficherListe(liste);
    }

     // Méthode qui crée une Liste avec quelques CompositeTache pour tester
     // Créer un objet Liste avec un titre
     // Créer 2 ou 3 CompositeTache (tâches principales)
            // un titre
            // une description
            // une date (sous forme de String)
     // pour une de ces CompositeTache, créer des sous-tâches
            // encore des CompositeTache
            // les ajouter à la tâche parent
     // Ajouter les tâches principales dans la liste :
            // avec getTache().add(tâche);
     // Retourner la liste

    private static Liste creerListeDeTest() {
        Liste liste = new Liste("Liste de tests");
        return liste; // → enlever commentaire quand méthode finie
    }

    /**
     * Affiche dans le terminal le contenu d'une Liste.
     *  - Afficher une ligne de séparation (ex: "====")
     *  - Afficher "Liste : " + le titre de la liste
     *  - Si la liste ne contient aucune tâche :
     *        - afficher un message "(Aucune tâche)" + return
     *  - Sinon :
     *        - parcourir la liste des CompositeTache
     *        - pour chaque CompositeTache :
     *              - afficher son numéro (1., 2., 3., etc.)
     *              - afficher ses infos (titre, date, description, état) (déléguer ça a une méthode formattage)
     *              - appeler une méthode afficherSousTaches(...) pour ses sous-tâches
     *              - afficher une ligne vide entre chaque tâche principale
     */
    private static void afficherListe(Liste liste) {
        // Le code au dessus
    }

    /**
     * Affiche les sous-tâches d'une CompositeTache avec indentation.
     *
     *  - Récupérer la liste des Tache contenues dans ct : ct.getTaches()
     *  - Si la liste est vide : ne rien faire (return)
     *  - Sinon :
     *        - pour chaque Tache de la liste :
     *              - Si c'est une CompositeTache (instanceof CompositeTache) :
     *                    - caster en CompositeTache
     *                    - afficher une ligne avec l'indentation suivi de "- " + formatTache(sousTache)
     *                    - rappeler récursivement afficherSousTaches(...) avec indent + "   "
     *              - (Plus tard : si tu ajoutes une TacheSimple, tu pourras l'afficher différemment)
     */
    private static void afficherSousTaches(CompositeTache ct, String indentation) {
        // code au dessus
    }

    /**
     * Transforme une CompositeTache en texte lisible.
     * Par exemple :
     *   [ ] Préparer la soutenance (date : 10-12-2025) - Faire l'exposé...
     *  - Créer une variable statut :
     *        - "[X]" si t.estFait() vaut true
     *        - "[ ]" sinon
     *  - Retourner une chaîne qui concatène :
     *        - le statut
     *        - le titre (t.getTitre())
     *        - la date (t.getDate())
     *        - la description (t.getDescription())
     */
    private static String formatTache(CompositeTache t) {
        // → Exemple : "[ ] Titre (date : ...) - description" ou autre
        return null; // À remplacer par la chaîne
    }
}
