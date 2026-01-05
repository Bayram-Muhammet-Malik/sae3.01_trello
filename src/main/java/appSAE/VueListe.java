package appSAE;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VueListe extends BorderPane implements Observateur {

    private final Model modele;
    private final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final VBox conteneurAgenda = new VBox(10);
    private final ScrollPane scroll = new ScrollPane(conteneurAgenda);
    private final List<ElementRepliable> elements = new ArrayList<>();
    private final int NB_JOURS = 7;

    public VueListe(Model modele) {
        this.modele = modele;

        conteneurAgenda.setPadding(new Insets(12));
        scroll.setFitToWidth(true);

        setCenter(scroll);

        rafraichir();
    }

    // reconstruit la vue
    private void rafraichir() {
        conteneurAgenda.getChildren().clear();
        elements.clear();

        if (modele.getListes().isEmpty()) {
            conteneurAgenda.getChildren().add(new Label("aucune liste"));
            return;
        }

        LocalDate aujourdHui = LocalDate.now();
        for (int i = 0; i < NB_JOURS; i++) {
            LocalDate jour = aujourdHui.plusDays(i);
            conteneurAgenda.getChildren().add(creerJour(jour));
        }
    }

    // crée un bloc pour un jour
    private VBox creerJour(LocalDate jour) {
        VBox blocJour = new VBox(6);

        String dateJour = formatDate.format(jour);

        // ligne du jour
        HBox ligneJour = new HBox(6);
        ligneJour.setAlignment(Pos.CENTER_LEFT);

        Button flecheJour = creerFleche("▾");
        Label labelJour = new Label(dateJour);

        ligneJour.getChildren().addAll(flecheJour, labelJour);

        // Contenu par jour (avec les listes)
        VBox boiteContenuJour = new VBox(8);
        boiteContenuJour.setPadding(new Insets(8));
        boiteContenuJour.setStyle("-fx-background-color: white;" + "-fx-border-color: #dddddd;" + "-fx-border-radius: 6;");

        boolean auMoinsUneTache = false;

        // Affichage des tâches
        for (Liste liste : modele.getListes()) {
            VBox boiteTachesListe = new VBox(3);

            for (Tache t : modele.getTachesFromListe(liste)) {
                if (t.getDebut() == null) continue;

                if (t.getDebut().toLocalDate().equals(jour)) {
                    boiteTachesListe.getChildren().add(creerTache(t, 0));
                    auMoinsUneTache = true;
                }
            }

            // s'il y a au moins une tâche pour cette liste ce jour
            if (!boiteTachesListe.getChildren().isEmpty()) {
                Label titreListe = new Label(liste.getTitre() + " :");
                titreListe.setStyle("-fx-font-weight: bold;");
                VBox blocListe = new VBox(3, titreListe, boiteTachesListe);
                boiteContenuJour.getChildren().add(blocListe);
            }
        }

        if (!auMoinsUneTache) {
            boiteContenuJour.getChildren().add(new Label("aucune tache"));
        }

        // replier / déplier le jour
        flecheJour.setOnAction(e -> {
            boolean visible = boiteContenuJour.isVisible();
            boiteContenuJour.setVisible(!visible);
            boiteContenuJour.setManaged(!visible);
            flecheJour.setText(visible ? "▸" : "▾");
        });

        blocJour.getChildren().addAll(ligneJour, boiteContenuJour);
        return blocJour;
    }

    // crée une tâche
    private VBox creerTache(Tache tache, int niveau) {
        VBox bloc = new VBox(2);

        boolean aDesSousTaches =
                (tache instanceof CompositeTache ct) && !ct.getTaches().isEmpty();

        HBox ligne = new HBox(6);
        ligne.setAlignment(Pos.CENTER_LEFT);

        Region indent = new Region();
        indent.setMinWidth(niveau * 16);

        Button fleche = creerFleche(aDesSousTaches ? "▾" : "");
        fleche.setDisable(!aDesSousTaches);
        fleche.setOpacity(aDesSousTaches ? 1 : 0);

        CheckBox check = new CheckBox();
        check.setSelected(tache.estFait());

        Label titre = new Label(tache.getTitre());

        ligne.getChildren().addAll(indent, fleche, check, titre);

        VBox boiteEnfants = new VBox(2);

        ElementRepliable element =
                new ElementRepliable(fleche, boiteEnfants, aDesSousTaches);
        elements.add(element);

        bloc.getChildren().add(ligne);

        if (aDesSousTaches) {
            CompositeTache ct = (CompositeTache) tache;

            for (Tache enfant : ct.getTaches()) {
                boiteEnfants.getChildren().add(creerTache(enfant, niveau + 1));
            }

            bloc.getChildren().add(boiteEnfants);
            fleche.setOnAction(e -> element.changerEtatAffichage());
        }

        return bloc;
    }

    // crée un bouton fleche pour le replier le contenu du jour
    private Button creerFleche(String texte) {
        Button b = new Button(texte);
        b.setMinSize(16, 16);
        b.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        return b;
    }

    // mise à jour
    @Override
    public void actualiser(Sujet sujet) {
        rafraichir();
    }

    /*
     * classe pour gérer le repli / dépli
     */
    private static class ElementRepliable {

        private final Button fleche;
        private final VBox enfants;
        private final boolean actif;
        private boolean affiche = true;

        ElementRepliable(Button fleche, VBox enfants, boolean actif) {
            this.fleche = fleche;
            this.enfants = enfants;
            this.actif = actif;
        }

        void changerEtatAffichage() {
            if (!actif) return;

            if (affiche) cacherEnfants();
            else afficherEnfants();
        }

        void afficherEnfants() {
            affiche = true;
            enfants.setVisible(true);
            enfants.setManaged(true);
            fleche.setText("▾");
        }

        void cacherEnfants() {
            affiche = false;
            enfants.setVisible(false);
            enfants.setManaged(false);
            fleche.setText("▸");
        }
    }
}