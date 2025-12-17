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
            model.setModel(((Model) ois.readObject()), fichier);
            System.out.println("Model chargé depuis " + fichier);
        } catch (IOException | ClassNotFoundException e) {
            model.setModel(null, fichier);
            System.out.println("Aucune sauvegarde trouvée.");
        }
    }
}
