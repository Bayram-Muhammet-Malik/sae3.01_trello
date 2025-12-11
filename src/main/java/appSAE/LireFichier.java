package appSAE;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LireFichier {


    public static String lireFicher(String fichier) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(fichier));

        String ligne = bf.readLine();
        StringBuilder contenu = new StringBuilder();

        while (ligne != null) {
            contenu.append(ligne).append("\n");

            ligne = bf.readLine();
        }
        bf.close();
        return contenu.toString();
    }

/*
    public static String lireFicher(String fichier, ) throws IOException {}
*/
    @Override
    public String toString() {
        return super.toString();
    }
}
//lire le fichier au bonne endroit