package appSAE;

import java.time.LocalDate;

public class FeuilleTache extends Tache {
    public FeuilleTache(String titre, String description, LocalDate debut, LocalDate fin, Priorite prio) {
        super(titre, description, debut, fin, prio);
    }
}
