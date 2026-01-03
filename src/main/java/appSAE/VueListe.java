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

    // modele principal
    private final Model modele;

    // format de date utilise dans les taches : jj-mm-aaaa
    private final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // conteneur principal
    private final VBox conteneurAgenda = new VBox(10);
    private final ScrollPane scroll = new ScrollPane(conteneurAgenda);

    // liste des elements repliables
    private final List<ElementRepliable> elements = new ArrayList<>();

    // nombre de jours affiches
    private final int NB_JOURS = 7;

    public VueListe(Model modele) {
        this.modele = modele;

        conteneurAgenda.setPadding(new Insets(12));
        scroll.setFitToWidth(true);

        setCenter(scroll);

        rafraichir();
    }

    // reconstruit toute la vue
    private void rafraichir() {
        conteneurAgenda.getChildren().clear();
        elements.clear();

        if (modele.getListes().isEmpty()) {
            conteneurAgenda.getChildren().add(new Label("aucune liste"));
            return;
        }

        Liste liste = modele.getListes().get(0);

        LocalDate aujourdHui = LocalDate.now();
        for (int i = 0; i < NB_JOURS; i++) {
            LocalDate jour = aujourdHui.plusDays(i);
            conteneurAgenda.getChildren().add(creerJour(jour, liste));
        }
    }

    // cree un bloc pour un jour
    private VBox creerJour(LocalDate jour, Liste liste) {
        VBox blocJour = new VBox(6);

        String dateJour = formatDate.format(jour);

        // ligne du jour
        HBox ligneJour = new HBox(6);
        ligneJour.setAlignment(Pos.CENTER_LEFT);

        Button flecheJour = creerFleche("▾");
        Label labelJour = new Label(dateJour);

        ligneJour.getChildren().addAll(flecheJour, labelJour);

        // conteneur des taches du jour
        VBox boiteTaches = new VBox(4);
        boiteTaches.setPadding(new Insets(8));
        boiteTaches.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dddddd;" +
                        "-fx-border-radius: 6;"
        );

        boolean trouve = false;
        for (CompositeTache t : liste.getTaches()) {
            /*
            if (dateJour.equals(t.getDate())) {
                boiteTaches.getChildren().add(creerTache(t, 0));
                trouve = true;
            }*/
        }

        if (!trouve) {
            boiteTaches.getChildren().add(new Label("aucune tache"));
        }

        // replier / deplier le jour
        flecheJour.setOnAction(e -> {
            boolean visible = boiteTaches.isVisible();
            boiteTaches.setVisible(!visible);
            boiteTaches.setManaged(!visible);
            flecheJour.setText(visible ? "▸" : "▾");
        });

        blocJour.getChildren().addAll(ligneJour, boiteTaches);
        return blocJour;
    }

    // cree une tache (avec sous-taches)
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

    // cree un bouton fleche simple
    private Button creerFleche(String texte) {
        Button b = new Button(texte);
        b.setMinSize(16, 16);
        b.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        return b;
    }

    // mise a jour depuis le modele
    @Override
    public void actualiser(Sujet sujet) {
        rafraichir();
    }

    /*
     classe simple pour gerer le repli / depli
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
