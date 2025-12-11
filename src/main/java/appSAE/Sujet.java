package appSAE;

public interface Sujet {
    public void enregistrerObservateur(Observateur o);
    public void notifierObservateur();
    public void supprimerObservateur(Observateur o);
}
