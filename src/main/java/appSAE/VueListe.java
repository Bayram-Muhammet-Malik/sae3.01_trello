package appSAE;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VueListe extends ScrollPane implements Observateur {
    private final Model model;
    private final ComboBox<String> triBox;

    public VueListe(Model modele) {
        this.model = modele;
        this.setStyle("-fx-background-color: transparent;");
        this.setFitToWidth(true);

        triBox = new ComboBox<>();
        triBox.getItems().addAll("Titre A→Z", "Priorité", "Durée");
        triBox.setValue("Titre A→Z");
        triBox.valueProperty().addListener((obs, o, n) -> actualiser(model));
    }

    @Override
    public void actualiser(Sujet sujet) {
        VBox conteneurAgenda = new VBox(8);

        HBox barreTri = new HBox(5, new Label("Trier par :"), triBox);
        barreTri.setStyle("-fx-alignment: center; -fx-padding: 4 0;");
        conteneurAgenda.getChildren().add(barreTri);

        if (model.getListes().isEmpty()) {
            conteneurAgenda.getChildren().add(new Label("aucune liste"));
            this.setContent(conteneurAgenda);
            return;
        }

        LocalDate lundi = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        for (int i = 0; i < 7; i++) conteneurAgenda.getChildren().add(creerJour(lundi.plusDays(i)));

        this.setContent(conteneurAgenda);
    }

    private VBox creerJour(LocalDate jour) {
        Button fleche = creerFleche("▾ " + DateTimeFormatter.ofPattern("EEEE").format(jour)
                + " - " + DateTimeFormatter.ofPattern("dd-MM-yyyy").format(jour));

        VBox contenu = new VBox(8);
        contenu.setStyle("-fx-background-color: white; -fx-border-radius: 6; -fx-padding: 5px");

        boolean auMoinsUne = false;

        for (Liste liste : model.getListes()) {
            VBox tachesListe = new VBox(3);

            List<Tache> tachesDuJour = new ArrayList<>();
            for (Iterator<Tache> it = model.getTachesFromListe(liste).iterator(); it.hasNext(); ) {
                Tache t = it.next();
                LocalDate d = t.getDebut() != null ? t.getDebut().toLocalDate() : null;
                LocalDate f = t.getFin() != null ? t.getFin().toLocalDate() : null;

                if (d != null && !jour.isBefore(d) && (f == null || !jour.isAfter(f))) {
                    tachesDuJour.add(t);
                }
            }

// tri selon le choix de triBox
            tachesDuJour.sort((t1, t2) -> {
                switch (triBox.getValue()) {
                    case "Priorité":
                        return t1.getPriorite().compareTo(t2.getPriorite());
                    case "Durée": {
                        // TODO : A faire
                    }
                    case "Titre A→Z":
                    default:
                        return t1.getTitre().compareToIgnoreCase(t2.getTitre());
                }
            });


            for (Iterator<Tache> it = tachesDuJour.iterator(); it.hasNext(); ) {
                Tache t = it.next();
                tachesListe.getChildren().add(creerTache(t, liste, 0));
            }

            if (!tachesListe.getChildren().isEmpty()) {
                auMoinsUne = true;
                contenu.getChildren().add(tachesListe);
            }
        }

        if (!auMoinsUne) contenu.getChildren().add(new Label("Aucune tâche"));

        fleche.setOnAction(e -> toggle(contenu, fleche));
        return new VBox(6, fleche, contenu);
    }

    private VBox creerTache(Tache t, Liste l, int niveau) {
        boolean hasChildren = t instanceof CompositeTache ct && !ct.getTaches().isEmpty();

        Button fleche = creerFleche(hasChildren ? "▾" : "");
        fleche.setDisable(!hasChildren);
        fleche.setOpacity(hasChildren ? 1 : 0);

        CheckBox check = new CheckBox();
        check.setSelected(t.estFait());

        Label badge = new Label(t.getPriorite().getLabel());
        String styleBase = "-fx-text-fill: white; -fx-padding: 1 4; -fx-background-radius: 999; "
                + "-fx-font-size: 10px; -fx-font-weight: bold;";
        badge.setStyle(
                switch (t.getPriorite()) {
                    case NORMAL -> "-fx-background-color: #93c47d;" + styleBase;
                    case IMPORTANT -> "-fx-background-color: #ffbb42;" + styleBase;
                    case URGENT -> "-fx-background-color: #ef4444;" + styleBase;
                }
        );

        Button edit = new Button("Modifier");
        edit.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; "
                + "-fx-padding: 0 4; -fx-font-size: 11px;");
        edit.setOnAction(e -> Popup.ouvrirPopUpTache(l, t, null, model));

        ImageView deleteIcon = new ImageView("file:icons/trash-can-solid-full.png");
        deleteIcon.setFitWidth(15);
        deleteIcon.setFitHeight(15);
        deleteIcon.setPickOnBounds(true);
        Tooltip.install(deleteIcon, new Tooltip("Supprimer"));
        deleteIcon.setOnMouseClicked(e -> Popup.supprimerTache(l, t, model));

        HBox ligne = new HBox(5, fleche, check, badge, new Label(t.getTitre()), edit, deleteIcon);
        ligne.setStyle("-fx-padding: 2 0;");

        VBox enfants = new VBox(2);
        if (hasChildren) {
            CompositeTache ct = (CompositeTache) t;

            List<Tache> enfantsSource;
            if ("Priorité".equals(triBox.getValue())) {
                // tri uniquement en mode Priorité
                List<Tache> enfantsTries = new ArrayList<>(ct.getTaches());
                enfantsTries.sort((t1, t2) -> t1.getPriorite().compareTo(t2.getPriorite()));
                enfantsSource = enfantsTries;
            } else {
                // autres modes : ordre d'insertion (création)
                enfantsSource = ct.getTaches();
            }

            for (Tache child : enfantsSource) {
                enfants.getChildren().add(creerTache(child, null, niveau + 1));
            }
            fleche.setOnAction(e -> toggle(enfants, fleche));
        }

        VBox bloc = new VBox(2, ligne);
        if (hasChildren) bloc.getChildren().add(enfants);

        if (niveau == 0) {
            bloc.setStyle("-fx-padding: 2 4;");
        } else {
            int indent = niveau * 18;
            bloc.setStyle("-fx-padding: 2 4;"
                    + " -fx-border-color: #d4d4d4;"
                    + " -fx-border-width: 0 0 0 2;"
                    + " -fx-border-insets: 0 0 0 " + indent + ";");
            bloc.setTranslateX(indent);
        }

        return bloc;
    }


    private Button creerFleche(String texte) {
        Button b = new Button(texte);
        b.setStyle("-fx-background-color: transparent; -fx-font-weight: Bold; -fx-font-size: 16px; -fx-padding: 0;");
        return b;
    }

    private void toggle(VBox box, Button fleche) {
        boolean visible = box.isVisible();
        box.setVisible(!visible);
        box.setManaged(!visible);
        String updated = fleche.getText().replaceFirst("^[▸▾]", visible ? "▸" : "▾");
        fleche.setText(updated);
    }

    private List<Tache> tachesDuJour(LocalDate jour) {
        List<Tache> res = new ArrayList<>();
        for (Liste liste : model.getListes()) {
            for (Tache t : model.getTachesFromListe(liste)) {
                if (t.getDebut() != null && t.getDebut().toLocalDate().equals(jour)) {
                    res.add(t);
                }
            }
        }
        return res;
    }
}