package appSAE;

import java.util.ArrayList;
import java.util.List;

public class Liste {
    private String titre;
    private List<CompositeTache> taches;

    public Liste(String titre) {
        this.titre = titre;
        this.taches = new ArrayList<>();
    }

    public void changerNom(String newName){
        this.titre = newName;
    }

    public String getTitre(){
        return this.titre;
    }
    public List<CompositeTache> getTaches(){
        return this.taches;
    }
}
