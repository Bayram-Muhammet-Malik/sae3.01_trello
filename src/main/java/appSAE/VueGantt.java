package appSAE;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VueGantt extends BorderPane implements Observateur {
    private final Model modele;

    private LocalDate debutSemaine = LocalDate.now();
    private double largeurJour = 90;

    private final VBox listeNoms = new VBox(6);
    private final Pane zoneBarres = new Pane();

    private final ScrollPane scrollNoms = new ScrollPane(listeNoms);
    private final ScrollPane scrollBarres = new ScrollPane(zoneBarres);

    private final List<Ligne> lignes = new ArrayList<>();

    private final java.util.Map<Tache, Boolean> visibilite = new java.util.HashMap<>();

    public VueGantt(Model modele) {
        this.modele = modele;
        setTop(creerBarreOutils());
        setCenter(creerCentre());
    }

    private HBox creerBarreOutils() {
        Button btnAujourdHui = new Button("aujourd'hui");
        Button btnZoomPlus = new Button("zoom +");
        Button btnZoomMoins = new Button("zoom -");

        btnAujourdHui.setOnAction(e -> {
            debutSemaine = LocalDate.now();
            modele.notifierObservateur();
        });

        btnZoomPlus.setOnAction(e -> {
            largeurJour = Math.min(160, largeurJour + 10);
            modele.notifierObservateur();
        });

        btnZoomMoins.setOnAction(e -> {
            largeurJour = Math.max(40, largeurJour - 10);
            modele.notifierObservateur();
        });

        HBox barre = new HBox(10, btnAujourdHui, btnZoomPlus, btnZoomMoins);
        barre.setPadding(new Insets(12));
        barre.setAlignment(Pos.CENTER_LEFT);
        return barre;
    }

    private HBox creerCentre() {
        scrollNoms.setFitToWidth(true);
        scrollNoms.setPrefViewportWidth(260);

        scrollBarres.setPannable(true);
        scrollBarres.setFitToHeight(true);

        HBox centre = new HBox(10, scrollNoms, scrollBarres);
        centre.setPadding(new Insets(12));
        return centre;
    }

    @Override
    public void actualiser(Sujet sujet) {
        listeNoms.getChildren().clear();
        zoneBarres.getChildren().clear();

        if (modele.getListes().isEmpty()) {
            listeNoms.getChildren().add(new Label("aucune liste"));
            return;
        }

        ajouterHeaderJours();

        lignes.clear();
        for (Liste liste : modele.getListes())
            for (Tache t : liste.getTaches())
                construireLignesRec(t, 0, null);

        double y = 60;
        for (Ligne l : lignes) {
            l.visible = visibilite.getOrDefault(l.tacheAssociee, true);

            if (l.checkbox == null) {
                listeNoms.getChildren().add(creerNom(l));
            } else {
                HBox ligneBox = new HBox(6);
                ligneBox.setAlignment(Pos.CENTER_LEFT);
                ligneBox.setPadding(new Insets(2, 6, 2, 6));
                Region indent = new Region();
                indent.setMinWidth(l.niveau * 16);
                ligneBox.getChildren().addAll(indent, l.checkbox);
                listeNoms.getChildren().add(ligneBox);
            }

            if (!l.visible) continue;

            if (l.niveau == 0) {
                int profMax = profondeurMax(l);
                double h = (profMax + 1) * 16 + 10;

                dessinerBarrePrincipaleEtSousTachesRec(l, y, h, 16);
                y += h + 8;
            }
        }

        zoneBarres.setMinWidth(40 + largeurJour * 7 + 40);
        zoneBarres.setMinHeight(y + 20);
    }

    private void ajouterHeaderJours() {
        listeNoms.getChildren().add(new Label("diagramme de gantt"));

        for (int i = 0; i < 7; i++) {
            LocalDate d = debutSemaine.plusDays(i);
            Label lab = new Label(String.format("%02d", d.getDayOfMonth()));
            lab.setLayoutX(40 + i * largeurJour + largeurJour / 2 - 10);
            lab.setLayoutY(10);
            zoneBarres.getChildren().add(lab);
        }
    }

    private HBox creerNom(Ligne l) {
        HBox ligneBox = new HBox(6);
        ligneBox.setAlignment(Pos.CENTER_LEFT);
        ligneBox.setPadding(new Insets(2, 6, 2, 6));

        Region indent = new Region();
        indent.setMinWidth(l.niveau * 16);

        CheckBox cb = new CheckBox(l.nom);
        cb.setSelected(l.visible);
        l.checkbox = cb;

        cb.selectedProperty().addListener((obs, oldV, newV) -> {
            visibilite.put(l.tacheAssociee, newV);
            modele.notifierObservateur();
        });

        ligneBox.getChildren().addAll(indent, cb);
        return ligneBox;
    }

    private void dessinerBarre(Ligne l, double y, double h) {
        if (l.debut == null) return;

        long decalageJour = l.debut.toEpochDay() - debutSemaine.toEpochDay();
        if (decalageJour < 0 || decalageJour > 6) return;

        Rectangle rect = new Rectangle(largeurJour, h);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setFill(Color.web("#3b82f6"));
        rect.setOpacity(0.9);

        Label texte = new Label(l.nom);
        texte.setTextFill(Color.WHITE);
        texte.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        texte.setMaxWidth(largeurJour - 8);
        texte.setEllipsisString("…");

        StackPane barre = new StackPane(rect, texte);
        barre.setLayoutX(40 + decalageJour * largeurJour);
        barre.setLayoutY(y);

        if (l.tacheAssociee instanceof CompositeTache ct && !ct.getTaches().isEmpty()) {
            StackPane.setAlignment(texte, Pos.TOP_LEFT);
            StackPane.setMargin(texte, new Insets(4, 4, 0, 6));
        } else {
            StackPane.setAlignment(texte, Pos.CENTER_LEFT);
            StackPane.setMargin(texte, new Insets(0, 4, 0, 6));
        }

        Tooltip.install(barre, new Tooltip(l.nom));
        barre.setOnMouseClicked(e -> Popup.ouvrirPopUpTache(modele.getListes().getFirst(), l.tacheAssociee, l.parentTache, modele));
        zoneBarres.getChildren().add(barre);
    }

    private int profondeurMax(Ligne racine) {
        return profondeurMaxRec(racine.tacheAssociee, 0);
    }

    private int profondeurMaxRec(Tache tache, int niveauActuel) {
        int max = niveauActuel;
        if (tache instanceof CompositeTache ct) {
            for (Tache st : ct.getTaches()) {
                for (Ligne l : lignes) {
                    if (l.tacheAssociee == st && l.visible) {
                        max = Math.max(max, profondeurMaxRec(st, niveauActuel + 1));
                        break;
                    }
                }
            }
        }
        return max;
    }

    private void dessinerBarrePrincipaleEtSousTachesRec(Ligne lignePrincipale, double y, double h, double hauteurParNiveau) {
        dessinerBarre(lignePrincipale, y, h);
        dessinerSousTachesRec(lignePrincipale.tacheAssociee, y, 1, hauteurParNiveau);
    }

    private void dessinerSousTachesRec(Tache parentTache, double yParent, int niveauRelatif, double hauteurParNiveau) {
        for (Ligne l : lignes) {
            if (l.parentTache == parentTache && l.visible) {
                dessinerSousTacheDansBarre(l, yParent + niveauRelatif * hauteurParNiveau, hauteurParNiveau);
                if (l.tacheAssociee instanceof CompositeTache) dessinerSousTachesRec(l.tacheAssociee, yParent, niveauRelatif + 1, hauteurParNiveau);
            }
        }
    }

    private void dessinerSousTacheDansBarre(Ligne l, double y, double h) {
        if (l.debut == null) return;

        long decalageJour = l.debut.toEpochDay() - debutSemaine.toEpochDay();
        if (decalageJour < 0 || decalageJour > 6) return;

        double x = 40 + decalageJour * largeurJour;

        Rectangle rect = new Rectangle(largeurJour, h);
        rect.setArcWidth(6);
        rect.setArcHeight(6);
        rect.setFill(Color.web("#22c55e"));

        Label texte = new Label(l.nom);
        texte.setTextFill(Color.BLACK);
        texte.setStyle("-fx-font-size: 10;");
        texte.setMaxWidth(largeurJour - 6);
        texte.setEllipsisString("…");

        StackPane barre = new StackPane(rect, texte);
        barre.setLayoutX(x);
        barre.setLayoutY(y);
        barre.setPadding(new Insets(1, 4, 1, 4));
        barre.setAlignment(Pos.CENTER_LEFT);

        Tooltip.install(barre, new Tooltip(l.nom));
        barre.setOnMouseClicked(e -> Popup.ouvrirPopUpTache(modele.getListes().getFirst(), l.tacheAssociee, l.parentTache, modele));

        zoneBarres.getChildren().add(barre);
    }

    private void construireLignesRec(Tache t, int niveau, Tache parent) {
        visibilite.putIfAbsent(t, true);

        Ligne ligne = new Ligne(t.getTitre(), niveau, t.getDebut(), t.getFin(), t, parent);
        ligne.visible = visibilite.get(t);
        lignes.add(ligne);

        if (t instanceof CompositeTache ct) for (Tache st : ct.getTaches()) construireLignesRec(st, niveau + 1, t);
    }

    private static class Ligne {
        String nom;
        int niveau;
        LocalDate debut, fin;
        Tache tacheAssociee, parentTache;
        boolean visible = true;
        CheckBox checkbox;

        Ligne(String nom, int niveau, LocalDate debut, LocalDate fin, Tache tache, Tache parent) {
            this.nom = nom;
            this.niveau = niveau;
            this.debut = debut;
            this.fin = fin;
            this.tacheAssociee = tache;
            this.parentTache = parent;
        }
    }
}
