package appSAE;

import java.time.LocalDate;

public class FeuilleTache extends Tache {
    /**
     * Constructeur FeuilleTache (appel le constructeur de Tache auquel il hérite)
     * @param titre Titre de la tâche
     * @param description Description de la tâche
     * @param debut Date début
     * @param fin Date de fin
     * @param prio Tag Normal/Important/Urgent
     */
    public FeuilleTache(String titre, String description, LocalDate debut, LocalDate fin, Priorite prio) {
        super(titre, description, debut, fin, prio);
    }
}
