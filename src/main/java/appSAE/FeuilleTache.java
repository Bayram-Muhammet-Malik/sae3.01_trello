package appSAE;

import java.time.LocalDateTime;

public class FeuilleTache extends Tache {
    public FeuilleTache(String titre, String description, LocalDateTime debut, LocalDateTime fin, Priorite prio) {
        super(titre, description, debut, fin, prio);
    }
}
