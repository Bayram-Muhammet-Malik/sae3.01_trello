package appSAE;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class VueBureau extends BorderPane implements Observateur {

    private final Model modele;

    // Container principal : HBox dans un ScrollPane
    private final HBox colonnesContainer = new HBox(16);
    private final ScrollPane scroll = new ScrollPane(colonnesContainer);

    public VueBureau(Model modele) {
        this.modele = modele;

        colonnesContainer.setPadding(new Insets(16));
        colonnesContainer.setAlignment(Pos.TOP_LEFT);

        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background-color:transparent;");

        setCenter(scroll);

        rebuild();
    }

    /* ---------------- REBUILD GLOBAL ---------------- */

    private void rebuild() {
        colonnesContainer.getChildren().clear();

        setTop(creerHeader("Tableau : Projet SAE"));
        setBottom(creerFooter());

        // 4 colonnes fixes, chacune filtrée par EtatTache
        colonnesContainer.getChildren().add(creerColonneEtat("À faire", EtatTache.A_FAIRE));
        colonnesContainer.getChildren().add(creerColonneEtat("En cours", EtatTache.EN_COURS));
        colonnesContainer.getChildren().add(creerColonneEtat("En revue", EtatTache.EN_REVUE));
        colonnesContainer.getChildren().add(creerColonneEtat("Terminé", EtatTache.TERMINE));
    }

    /* ---------------- HEADER ---------------- */

    private HBox creerHeader(String titre) {
        Label label = new Label(titre);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        Button ajouterTache = new Button("Ajouter une tâche");
        ajouterTache.setStyle(styleBtnBleu());

        Button filtrer = new Button("Filtrer");
        filtrer.setStyle(styleBtnGris());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(12, label, spacer, ajouterTache, filtrer);
        header.setPadding(new Insets(12, 16, 12, 16));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #4667d6;");

        return header;
    }

    /* ---------------- COLONNE PAR ÉTAT ---------------- */

    private VBox creerColonneEtat(String titreColonne, EtatTache etatVoulu) {
        VBox colonne = new VBox(12);
        colonne.setPadding(new Insets(12));
        colonne.setPrefWidth(260);
        colonne.setStyle(
                "-fx-background-color: #f3f4f6;" +
                        "-fx-background-radius: 10;"
        );

        Label titreCol = new Label(titreColonne);
        titreCol.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox cartesBox = new VBox(10);

        // Parcourt toutes les listes et toutes les tâches,
        // ne garde que celles qui ont l'état voulu
        for (Liste liste : modele.getListes()) {
            for (CompositeTache t : liste.getTaches()) {
                if (t.getEtat() == etatVoulu) {
                    cartesBox.getChildren().add(creerCarte(t));
                }
            }
        }

        if (cartesBox.getChildren().isEmpty()) {
            Label vide = new Label("Aucune carte");
            vide.setStyle("-fx-text-fill: #6b7280;");
            cartesBox.getChildren().add(vide);
        }

        Button ajouterCarte = new Button("+ Ajouter une carte");
        ajouterCarte.setMaxWidth(Double.MAX_VALUE);
        ajouterCarte.setStyle(
                "-fx-background-color: #e5e7eb;" +
                        "-fx-text-fill: #111827;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;" +
                        "-fx-font-size: 13;"
        );
        ajouterCarte.setOnAction(e ->
                System.out.println("TODO: ajouter une carte dans la colonne " + titreColonne)
        );

        colonne.getChildren().addAll(titreCol, cartesBox, ajouterCarte);
        return colonne;
    }

    /* ---------------- CARTE ---------------- */

    private VBox creerCarte(CompositeTache tache) {
        VBox carte = new VBox(6);
        carte.setPadding(new Insets(10));
        carte.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 10;"
        );

        Label titre = new Label(tache.getTitre());
        titre.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #111827;");

        // Badge de priorité (placeholder)
        Label priorite = new Label("Priorité");
        priorite.setStyle(
                "-fx-background-color: #ef4444;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 999;" +
                        "-fx-padding: 2 8;" +
                        "-fx-font-size: 11;"
        );

        Label desc = new Label(tache.getDescription() == null ? "" : tache.getDescription());
        desc.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12;");

        // Ligne bas : date + progression sous-tâches
        HBox bas = new HBox(8);
        bas.setAlignment(Pos.CENTER_LEFT);

        String date = tache.getDate() == null ? "" : tache.getDate();
        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");

        int total = tache.getTaches().size();
        long faits = tache.getTaches().stream().filter(Tache::estFait).count();
        Label progression = new Label(faits + "/" + total);
        progression.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");

        bas.getChildren().addAll(dateLbl, progression);

        carte.getChildren().addAll(titre, priorite, desc, bas);
        return carte;
    }

    /* ---------------- FOOTER ---------------- */

    private HBox creerFooter() {
        Button vueListe = new Button("Vue liste");
        vueListe.setStyle(styleBtnGris());

        Button vueGantt = new Button("Vue Gantt");
        vueGantt.setStyle(styleBtnGris());

        HBox footer = new HBox(12, vueListe, vueGantt);
        footer.setPadding(new Insets(10, 14, 12, 14));
        footer.setAlignment(Pos.CENTER_LEFT);

        return footer;
    }

    /* ---------------- STYLES ---------------- */

    private String styleBtnBleu() {
        return "-fx-background-color: #5474e7; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14;";
    }

    private String styleBtnGris() {
        return "-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 8 14;";
    }

    /* ---------------- OBSERVATEUR ---------------- */

    @Override
    public void actualiser(Sujet sujet) {
        rebuild();
    }
}
