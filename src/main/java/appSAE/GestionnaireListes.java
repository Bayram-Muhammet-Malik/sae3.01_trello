package appSAE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireListes implements Serializable {
    private List<Liste> listes;

    public GestionnaireListes() {
        this.listes = new ArrayList<>();
    }

    public List<Liste> getListes() {
        return listes;
    }

    public void ajouterListe(String titre) {
        listes.add(new Liste(titre));
    }

    public void supprimerListe(int index) {
        if (index >= 0 && index < listes.size()) {
            listes.remove(index);
        }
    }

    public Liste getListe(int index) {
        if (index >= 0 && index < listes.size()) {
            return listes.get(index);
        }
        return null;
    }
}
