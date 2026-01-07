package appSAE;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public VueGantt(Model modele) {
        this.modele = modele;

        setTop(creerBarreOutils());
        setCenter(creerCentre());

        rafraichir();
    }

    // barre du haut : aujourd'hui + zoom
    private HBox creerBarreOutils() {
        Button btnAujourdHui = new Button("aujourd'hui");
        Button btnZoomPlus = new Button("zoom +");
        Button btnZoomMoins = new Button("zoom -");

        btnAujourdHui.setOnAction(e -> {
            debutSemaine = LocalDate.now();
            rafraichir();
        });

        btnZoomPlus.setOnAction(e -> {
            largeurJour = Math.min(160, largeurJour + 10);
            rafraichir();
        });

        btnZoomMoins.setOnAction(e -> {
            largeurJour = Math.max(40, largeurJour - 10);
            rafraichir();
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
    private void rafraichir() {
        Map<Tache, Double> yParTache = new HashMap<>();

        listeNoms.getChildren().clear();
        zoneBarres.getChildren().clear();

        if (modele.getListes().isEmpty()) {
            listeNoms.getChildren().add(new Label("aucune liste"));
            return;
        }

        Liste liste = modele.getListes().get(0);

        // header simple : jours 01 02 03 ...
        ajouterHeaderJours();

        // lignes a afficher (racines + sous-taches)
        List<Ligne> lignes = new ArrayList<>();
        for (Tache t : liste.getTaches()) {
            ajouterRec(t, 0, lignes, null);
        }

        Map<Ligne, Double> yParLigne = new HashMap<>();

        double y = 40;
        double h = 22;

        for (Ligne l : lignes) {
            if (l.niveau == 0) {
                listeNoms.getChildren().add(creerNom(l));
                yParTache.put(l.tacheAssociee, y); // stocke par tache
                dessinerBarre(l, y, h);
                y += h + 6;
            } else {
                // dessiner la sous-tâche DANS la barre du parent
                Double yParent = yParTache.get(l.parentTache);
                if (yParent != null) {
                    dessinerBarre(l, yParent, h - 8); // plus fine
                }
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

    // transforme les taches en lignes (avec indentation)
    private void ajouterRec(Tache t, int niveau, List<Ligne> out, Tache parent) {
        LocalDateTime deb = (t.getDebut() != null) ? t.getDebut() : LocalDateTime.now();
        LocalDateTime fin = (t.getFin() != null) ? t.getFin() : deb;

        Ligne ligne = new Ligne(t.getTitre(), niveau, deb, fin, t, parent);
        out.add(ligne);

        if (t instanceof CompositeTache ct) {
            for (Tache enfant : ct.getTaches()) {
                ajouterRec(enfant, niveau + 1, out, t);
            }
        }
    }



    // regle simple pour la duree
    private int dureeParDefaut(Tache t) {
        if (t instanceof CompositeTache ct && !ct.getTaches().isEmpty()) return 3;
        return 1;
    }

    // si date invalide, on met aujourd'hui (pour ne pas planter)
    private LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s, formatDate);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    // affiche le nom a gauche
    private HBox creerNom(Ligne l) {
        HBox ligne = new HBox(6);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setPadding(new Insets(2, 6, 2, 6));

        Region indent = new Region();
        indent.setMinWidth(l.niveau * 16);

        Label nom = new Label(l.nom);

        ligne.getChildren().addAll(indent, nom);
        return ligne;
    }

    // dessine la barre a droite
    private void dessinerBarre(Ligne l, double y, double h) {

        long decalageJour = l.debut.toLocalDate().toEpochDay() - debutSemaine.toEpochDay();
        if (decalageJour < 0 || decalageJour > 6) return;

        double offsetHeure = (l.debut.getHour() + l.debut.getMinute() / 60.0) / 24.0;
        double dureeHeures = java.time.Duration.between(l.debut, l.fin).toMinutes() / 60.0;
        if (dureeHeures <= 0) dureeHeures = 0.25;

        double x = 40 + decalageJour * largeurJour + offsetHeure * largeurJour;
        double w = (dureeHeures / 24.0) * largeurJour;

        Rectangle barre = new Rectangle(x, y, w, h);
        barre.setArcWidth(10);
        barre.setArcHeight(10);

        boolean estSousTache = l.niveau > 0;
        String couleur = estSousTache ? "#22c55e" : "#3b82f6";
        barre.setStyle("-fx-fill: " + couleur + "; -fx-opacity: 0.9;");

        // contour noir pour sous-tâches
        if (estSousTache) {
            barre.setStroke(javafx.scene.paint.Color.BLACK);
            barre.setStrokeWidth(1.2);
        }

        // tooltip
        Tooltip.install(barre, new Tooltip(l.nom));

        // popup au clic
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



    @Override
    public void actualiser(Sujet sujet) {
        rafraichir();
    }

    // petite structure pour une ligne du gantt
    private static class Ligne {
        String nom;
        int niveau;
        LocalDateTime debut;
        LocalDateTime fin;
        Tache tacheAssociee;
        Tache parentTache;

        Ligne(String nom, int niveau, LocalDateTime debut, LocalDateTime fin, Tache tache, Tache parent) {
            this.nom = nom;
            this.niveau = niveau;
            this.debut = debut;
            this.fin = fin;
            this.tacheAssociee = tache;
            this.parentTache = parent;
        }
    }

}
