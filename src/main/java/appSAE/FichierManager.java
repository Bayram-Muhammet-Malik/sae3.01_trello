package appSAE;

import java.io.*;

public class FichierManager {
    //Sauvegarde liste dans un fichier
    public static void sauvegarder(Model modele, String fichier) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier));

            //sérialisation
            oos.writeObject(modele);
            oos.close();

            System.out.println("Liste sauvegardée dans " + fichier);

        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde :");
            e.printStackTrace();
        }
    }

    //Charger liste du fichier
    public static Model charger(String fichier) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier));
            //désérialisation
            Model  modele = (Model) ois.readObject();
            ois.close();

            System.out.println("Liste chargée depuis " + fichier);
            return modele;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aucune sauvegarde trouvée.");
            return null;
        }
    }
}
