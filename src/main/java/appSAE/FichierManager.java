package appSAE;

import java.io.*;

public class FichierManager {
    /**
     * Méthode qui permet de sauvegarder le Model dans un fichier
     * @param model Model a sauvegarder
     * @param fichier Chemin du fichier où le Model est enregistré
     */
    public static void sauvegarder(Model model, String fichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
            oos.writeObject(model);
            System.out.println("Model sauvegardé dans " + fichier);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde :");
            e.printStackTrace();
        }
    }

    /**
     * Permet de charger le Model enregistré dans un fichier
     * @param model Objet Model où les données seront extraite
     * @param fichier Fichier où le Model à charger est enregistré
     */
    public static void charger(Model model, String fichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            model.setModel(((Model) ois.readObject()), fichier);
            System.out.println("Model chargé depuis " + fichier);
        } catch (IOException | ClassNotFoundException e) {
            model.setModel(null, fichier);
            System.out.println("Aucune sauvegarde trouvée.");
        }
    }
}
