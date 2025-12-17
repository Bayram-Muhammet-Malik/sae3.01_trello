package appSAE;

import java.io.*;

public class FichierManager {

    // Sauvegarde du modèle
    public static void sauvegarder(Model model, String fichier) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
            oos.writeObject(model);
            System.out.println("Model sauvegardé dans " + fichier);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde :");
            e.printStackTrace();
        }
    }

    // Chargement dans un modèle existant
    public static void charger(Model model, String fichier) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            Model loaded = (Model) ois.readObject();

            // ⚡️ Mettre à jour l'instance existante au lieu d'en créer une nouvelle
            model.setModel(loaded);
            // filepath reste à gérer manuellement si besoin

            System.out.println("Model chargé depuis " + fichier);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aucune sauvegarde trouvée.");
        }
    }
}
