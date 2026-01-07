package appSAE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class ParcoursTaches implements Iterator<Tache> {
    protected List<Tache> data;
    protected int index = 0;

    public ParcoursTaches(List<Tache> source, Comparator<Tache> cmp) {
        this.data = new ArrayList<>(source);
        trier(data, cmp);
    }

    protected abstract void trier(List<Tache> liste, Comparator<Tache> cmp);

    @Override
    public boolean hasNext() {
        return index < data.size();
    }

    @Override
    public Tache next() {
        return data.get(index++);
    }
}