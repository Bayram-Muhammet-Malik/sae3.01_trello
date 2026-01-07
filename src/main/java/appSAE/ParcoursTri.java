package appSAE;

import java.util.Comparator;
import java.util.List;

public class ParcoursTri extends ParcoursTaches {
    public ParcoursTri(List<Tache> source, Comparator<Tache> cmp) {
        super(source, cmp);
    }

    @Override
    protected void trier(List<Tache> liste, Comparator<Tache> cmp) {
        liste.sort(cmp);
    }
}