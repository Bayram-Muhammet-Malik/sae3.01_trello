package appSAE;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 vue gantt tres simple :
 - 7 jours (semaine)
 - date de la tache = debut (jj-mm-aaaa)
 - duree par defaut : 1 jour, ou 3 jours si la tache a des sous-taches
 */
public class VueGantt extends BorderPane implements Observateur {
    private final Model modele;

    private final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private LocalDate debutSemaine = LocalDate.now();
    private double largeurJour = 90;

    private final VBox listeNoms = new VBox(6);
    private final Pane zoneBarres = new Pane();

    private final ScrollPane scrollNoms = new ScrollPane(listeNoms);
    private final ScrollPane scrollBarres = new ScrollPane(zoneBarres);

    private final List<Ligne> lignes = new ArrayList<>();

    public VueGantt(Model modele) {
        this.modele = modele;

        setTop(creerBarreOutils());
        setCenter(creerCentre());
    }

    // barre du haut : aujourd'hui + zoom
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

    // centre : colonne gauche (noms) + colonne droite (barres)
    private HBox creerCentre() {
        scrollNoms.setFitToWidth(true);
        scrollNoms.setPrefViewportWidth(260);

        scrollBarres.setPannable(true);
        scrollBarres.setFitToHeight(true);

        HBox centre = new HBox(10, scrollNoms, scrollBarres);
        centre.setPadding(new Insets(12));
        return centre;
    }

    // reconstruit tout l'affichage
    @Override
    public void actualiser(Sujet sujet) {
        listeNoms.getChildren().clear();
        zoneBarres.getChildren().clear();

        if (modele.getListes().isEmpty()) {
            listeNoms.getChildren().add(new Label("aucune liste"));
            return;
        }

        ajouterHeaderJours();

        // on reconstruit toujours les lignes, pour TOUTES les listes
        lignes.clear();
        for (Liste liste : modele.getListes()) {
            for (Tache t : liste.getTaches()) {
                construireLignesRec(t, 0, null);
            }
        }

        double y = 60; // un peu plus bas pour laisser la place au header
        for (Ligne l : lignes) {

            // Colonne de gauche : checkbox + nom
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

            // Petite ligne horizontale de fond pour chaque tâche principale
            if (l.niveau == 0) {
                Line fond = new Line(40, y + 24, 40 + largeurJour * 7, y + 24);
                fond.setStroke(Color.web("#e5e7eb"));
                zoneBarres.getChildren().add(fond);
            }

            // Tâches principales : grande barre + toutes les sous-tâches dedans
            if (l.niveau == 0) {
                int profMax = profondeurMax(l);     // profondeur de la hiérarchie de sous-tâches
                double hauteurParNiveau = 16;       // hauteur pour chaque niveau de sous-tâche
                double h = (profMax + 1) * hauteurParNiveau + 10;

                dessinerBarrePrincipaleEtSousTachesRec(l, y, h, profMax, hauteurParNiveau);
                y += h + 8;
            }
        }

        zoneBarres.setMinWidth(40 + largeurJour * 7 + 40);
        zoneBarres.setMinHeight(y + 20);
    }


    // affiche les numeros des jours
    private void ajouterHeaderJours() {
        listeNoms.getChildren().add(new Label("diagramme de gantt"));

        for (int i = 0; i < 7; i++) {
            LocalDate d = debutSemaine.plusDays(i);
            String txt = String.format("%02d", d.getDayOfMonth());

            Label lab = new Label(txt);
            lab.setLayoutX(40 + i * largeurJour + (largeurJour / 2) - 10);
            lab.setLayoutY(10);

            zoneBarres.getChildren().add(lab);
        }
    }

    // affiche le nom a gauche
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
            l.visible = newV;
            modele.notifierObservateur();
        });

        ligneBox.getChildren().addAll(indent, cb);
        return ligneBox;
    }

    // dessine une barre (bleue pour niveau 0)
    private void dessinerBarre(Ligne l, double y, double h) {
        if (l.debut == null) return;

        // l.debut est maintenant un LocalDate
        long decalageJour = l.debut.toEpochDay() - debutSemaine.toEpochDay();
        if (decalageJour < 0 || decalageJour > 6) return;

        // Plus d'heure → la barre occupe toute la largeur du jour
        double x = 40 + decalageJour * largeurJour;
        double w = largeurJour;

        Rectangle barre = new Rectangle(x, y, w, h);
        barre.setArcWidth(10);
        barre.setArcHeight(10);

        barre.setStyle("-fx-fill: #3b82f6; -fx-opacity: 0.9;");

        Tooltip.install(barre, new Tooltip(l.nom));

        barre.setOnMouseClicked(e -> {
            Popup.ouvrirPopUpTache(
                    modele.getListes().get(0),
                    l.tacheAssociee,
                    l.parentTache,
                    modele
            );
        });

        zoneBarres.getChildren().add(barre);
    }

    // profondeur max d'une tâche (en nombre de niveaux de sous-tâches)
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

    // dessine la barre principale (bleu) + toutes les sous-tâches (verts) à l'intérieur
    private void dessinerBarrePrincipaleEtSousTachesRec(Ligne lignePrincipale,
                                                        double y,
                                                        double h,
                                                        int profMax,
                                                        double hauteurParNiveau) {
        // barre bleue
        dessinerBarre(lignePrincipale, y, h);

        // sous-tâches de tous niveaux
        dessinerSousTachesRec(lignePrincipale.tacheAssociee, y, 1, hauteurParNiveau);
    }

    // dessine récursivement toutes les sous-tâches (1.1, 1.1.1, 1.1.1.1, ...) en vert
    private void dessinerSousTachesRec(Tache parentTache, double yParent, int niveauRelatif, double hauteurParNiveau) {
        for (Ligne l : lignes) {
            if (l.parentTache == parentTache && l.visible) {
                double yRect = yParent + (niveauRelatif * hauteurParNiveau);
                dessinerSousTacheDansBarre(l, yRect, hauteurParNiveau);

                if (l.tacheAssociee instanceof CompositeTache) dessinerSousTachesRec(l.tacheAssociee, yParent, niveauRelatif + 1, hauteurParNiveau);
            }
        }
    }

    // dessine une sous-tâche (rectangle vert) dans la barre de son parent
    private void dessinerSousTacheDansBarre(Ligne l, double y, double h) {
        if (l.debut == null) return;

        long decalageJour = l.debut.toEpochDay() - debutSemaine.toEpochDay();
        if (decalageJour < 0 || decalageJour > 6) return;

        double x = 40 + decalageJour * largeurJour;
        double w = largeurJour;

        Rectangle barre = new Rectangle(x, y, w, h);
        barre.setArcWidth(6);
        barre.setArcHeight(6);

        barre.setStyle("-fx-fill: #22c55e; -fx-opacity: 0.9;");
        barre.setStroke(javafx.scene.paint.Color.BLACK);
        barre.setStrokeWidth(1.0);

        Tooltip.install(barre, new Tooltip(l.nom));

        barre.setOnMouseClicked(e -> {
            Popup.ouvrirPopUpTache(
                    modele.getListes().get(0),
                    l.tacheAssociee,
                    l.parentTache,
                    modele
            );
        });

        zoneBarres.getChildren().add(barre);
    }

    // construit la structure des lignes à partir des tâches
    private void construireLignesRec(Tache t, int niveau, Tache parent) {
        Ligne ligne = new Ligne(t.getTitre(), niveau, t.getDebut(), t.getFin(), t, parent);
        lignes.add(ligne);

        if (t instanceof CompositeTache ct) {
            for (Tache st : ct.getTaches()) {
                construireLignesRec(st, niveau + 1, t);
            }
        }
    }

    // petite structure pour une ligne du gantt
    private static class Ligne {
        String nom;
        int niveau;
        LocalDate debut;
        LocalDate fin;
        Tache tacheAssociee;
        Tache parentTache;
        boolean visible = true; // par défaut visible
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
