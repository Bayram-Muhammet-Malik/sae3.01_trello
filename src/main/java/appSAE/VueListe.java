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

    // Format demandé : JJ-MM-AAAA
    private final DateTimeFormatter fmtStockage = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter fmtTitre = DateTimeFormatter.ofPattern("EEEE dd/MM");

    private final VBox agendaContainer = new VBox(12);
    private final ScrollPane scroll = new ScrollPane(agendaContainer);

    private final List<ToggleGroupNode> toggles = new ArrayList<>();

    private final int NB_JOURS = 7;

    public VueListe(Model modele) {
        this.modele = modele;

        agendaContainer.setPadding(new Insets(14));
        scroll.setFitToWidth(true);

        setCenter(scroll);

        rebuild();
    }

    private void rebuild() {
        toggles.clear();
        agendaContainer.getChildren().clear();

        if (modele.getListes().isEmpty()) {
            setTop(creerHeader("Tableau : (aucun)"));
            agendaContainer.getChildren().add(new Label("Aucune liste pour le moment."));
            setBottom(creerFooter());
            return;
        }

        // Tableau courant = 1ère liste (comme tu fais)
        Liste liste = modele.getListes().get(0);

        setTop(creerHeader("Tableau : " + liste.getTitre()));
        setBottom(creerFooter());

        LocalDate today = LocalDate.now();
        for (int i = 0; i < NB_JOURS; i++) {
            LocalDate jour = today.plusDays(i);
            agendaContainer.getChildren().add(creerBlocJour(jour, liste));
        }
    }

    // ---------------- HEADER (barre bleue) ----------------

    private HBox creerHeader(String titre) {
        Label label = new Label(titre);
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox header = new HBox(label);
        header.setPadding(new Insets(16));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #4667d6;");

        return header;
    }

    // ---------------- AGENDA (par jour) ----------------

    private VBox creerBlocJour(LocalDate jour, Liste liste) {
        VBox blocJour = new VBox(8);

        String dateKey = fmtStockage.format(jour);
        String titre;

        if (jour.equals(LocalDate.now())) titre = "Aujourd'hui (" + dateKey + ")";
        else if (jour.equals(LocalDate.now().plusDays(1))) titre = "Demain (" + dateKey + ")";
        else titre = fmtTitre.format(jour) + " (" + dateKey + ")";

        // Ligne jour avec flèche à gauche
        HBox ligneJour = new HBox(8);
        ligneJour.setAlignment(Pos.CENTER_LEFT);

        Button arrowJour = new Button("▾");
        arrowJour.setMinSize(18, 18);
        arrowJour.setPrefSize(18, 18);
        arrowJour.setMaxSize(18, 18);
        arrowJour.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-font-size: 12;");

        Label titreJour = new Label(titre);
        titreJour.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #111827;");

        ligneJour.getChildren().addAll(arrowJour, titreJour);

        // Carte blanche (contenu du jour)
        VBox carte = new VBox(6);
        carte.setPadding(new Insets(10));
        carte.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 10;"
        );

        // ✅ IMPORTANT : remplir la carte avec les tâches de CE jour
        List<CompositeTache> tachesDuJour = new ArrayList<>();
        for (CompositeTache t : liste.getTaches()) {
            if (eqDate(dateKey, t.getDate())) {
                tachesDuJour.add(t);
            }
        }

        if (tachesDuJour.isEmpty()) {
            Label rien = new Label("Aucune tâche");
            rien.setStyle("-fx-text-fill: #6b7280;");
            carte.getChildren().add(rien);
        } else {
            for (CompositeTache t : tachesDuJour) {
                carte.getChildren().add(creerNoeudTache(t, 0));
            }
        }

        // Toggle du jour (replier/déplier la carte)
        carte.setVisible(true);
        carte.setManaged(true);
        arrowJour.setOnAction(e -> {
            boolean visible = carte.isVisible();
            carte.setVisible(!visible);
            carte.setManaged(!visible);
            arrowJour.setText(visible ? "▸" : "▾");
        });

        blocJour.getChildren().addAll(ligneJour, carte);
        return blocJour;
    }

    // comparaison robuste (espaces, null)
    private boolean eqDate(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equals(b.trim());
    }

    // ---------------- NOEUD TÂCHE (arborescence maquette) ----------------

    private VBox creerNoeudTache(Tache tache, int niveau) {
        VBox noeud = new VBox(4);

        boolean aDesEnfants = (tache instanceof CompositeTache ct) && !ct.getTaches().isEmpty();

        // Ligne principale : [indent] [flèche] [checkbox] [texte]
        HBox ligne = new HBox(8);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setPadding(new Insets(4, 4, 4, 6));

        Region indent = new Region();
        indent.setMinWidth(niveau * 18);
        indent.setPrefWidth(niveau * 18);

        Button arrow = new Button(aDesEnfants ? "▾" : "");
        arrow.setMinSize(18, 18);
        arrow.setPrefSize(18, 18);
        arrow.setMaxSize(18, 18);
        arrow.setDisable(!aDesEnfants);
        arrow.setOpacity(aDesEnfants ? 1.0 : 0.0);
        arrow.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-font-size: 12;");

        CheckBox cb = new CheckBox();
        cb.setSelected(tache.estFait());
        cb.setMinWidth(18);

        Label titre = new Label(tache.getTitre());
        titre.setStyle("-fx-font-size: 14; -fx-text-fill: #111827;");

        ligne.getChildren().addAll(indent, arrow, cb, titre);

        VBox enfantsBox = new VBox(4);

        ToggleGroupNode node = new ToggleGroupNode(arrow, enfantsBox, aDesEnfants);
        toggles.add(node);

        noeud.getChildren().add(ligne);

        if (aDesEnfants) {
            CompositeTache ct = (CompositeTache) tache;

            for (Tache enfant : ct.getTaches()) {
                enfantsBox.getChildren().add(creerNoeudTache(enfant, niveau + 1));
            }

            noeud.getChildren().add(enfantsBox);

            arrow.setOnAction(e -> node.toggle());
        }

        return noeud;
    }

    // ---------------- FOOTER (boutons bas) ----------------

    private HBox creerFooter() {
        Button addTask = new Button("Ajouter une tâche");
        addTask.setStyle(styleBtnBleu());

        Button addSub = new Button("Ajouter une sous-tâche");
        addSub.setStyle(styleBtnBleu());

        Button expandAll = new Button("Déplier tout");
        expandAll.setStyle(styleBtnGris());

        Button collapseAll = new Button("Replier tout");
        collapseAll.setStyle(styleBtnGris());

        addTask.setOnAction(e -> System.out.println("TODO: ajouter une tâche"));
        addSub.setOnAction(e -> System.out.println("TODO: ajouter une sous-tâche"));

        expandAll.setOnAction(e -> toggles.forEach(ToggleGroupNode::expand));
        collapseAll.setOnAction(e -> toggles.forEach(ToggleGroupNode::collapse));

        HBox footer = new HBox(12, addTask, addSub, expandAll, collapseAll);
        footer.setPadding(new Insets(12, 14, 14, 14));
        footer.setAlignment(Pos.CENTER_LEFT);

        return footer;
    }

    private String styleBtnBleu() {
        return "-fx-background-color: #5474e7; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16;";
    }

    private String styleBtnGris() {
        return "-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 10 16;";
    }

    // ---------------- Observateur ----------------

    @Override
    public void actualiser(Sujet sujet) {
        rebuild();
    }

    // ---------------- utilitaire toggle ----------------

    private static class ToggleGroupNode {
        private final Button arrow;
        private final VBox childrenBox;
        private final boolean enabled;
        private boolean expanded = true;

        ToggleGroupNode(Button arrow, VBox childrenBox, boolean enabled) {
            this.arrow = arrow;
            this.childrenBox = childrenBox;
            this.enabled = enabled;

            // état initial : déplié
            if (enabled) {
                childrenBox.setVisible(true);
                childrenBox.setManaged(true);
            }
        }

        void toggle() {
            if (!enabled) return;
            if (expanded) collapse(); else expand();
        }

        void expand() {
            if (!enabled) return;
            expanded = true;
            childrenBox.setVisible(true);
            childrenBox.setManaged(true);
            arrow.setText("▾");
        }

        void collapse() {
            if (!enabled) return;
            expanded = false;
            childrenBox.setVisible(false);
            childrenBox.setManaged(false);
            arrow.setText("▸");
        }
    }
}
