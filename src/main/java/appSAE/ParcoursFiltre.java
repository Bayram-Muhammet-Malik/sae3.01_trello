package appSAE;

import java.util.Comparator;
import java.util.List;

public class ParcoursFiltre extends ParcoursTaches {
    public ParcoursFiltre(List<Tache> source, boolean garderFait) {
        super(source, null);
        data.removeIf(t -> t.estFait() != garderFait);
    }

    @Override
    protected void trier(List<Tache> liste, Comparator<Tache> cmp) {
        // Filtrage donc pas de tri
    }
}