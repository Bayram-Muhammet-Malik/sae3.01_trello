package appSAE;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VueListe extends ScrollPane implements Observateur {
    private final Model model;

    public VueListe(Model modele) {
        this.model = modele;
        this.setStyle("-fx-background-color: transparent;");
        this.setFitToWidth(true);
    }

    @Override
    public void actualiser(Sujet sujet) {
        VBox conteneurAgenda = new VBox();
        this.setContent(conteneurAgenda);

        if (model.getListes().isEmpty()) {
            conteneurAgenda.getChildren().add(new Label("aucune liste"));
            return;
        }

        LocalDate lundi = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        for (int i = 0; i < 7; i++) conteneurAgenda.getChildren().add(creerJour(lundi.plusDays(i)));
    }

    private VBox creerJour(LocalDate jour) {
        Button fleche = creerFleche("▾ " + DateTimeFormatter.ofPattern("EEEE").format(jour) + " - " + DateTimeFormatter.ofPattern("dd-MM-yyyy").format(jour));

        VBox contenu = new VBox(8);
        contenu.setStyle("-fx-background-color: white; -fx-border-radius: 6; -fx-padding: 5px");

        boolean auMoinsUne = false;
        for (Liste liste : model.getListes()) {
            VBox tachesListe = new VBox(3);

            for (Tache t : model.getTachesFromListe(liste)) {
                LocalDate d = t.getDebut() != null ? t.getDebut().toLocalDate() : null;
                LocalDate f = t.getFin() != null ? t.getFin().toLocalDate() : null;

                if (d != null && !jour.isBefore(d) && (f == null || !jour.isAfter(f))) tachesListe.getChildren().add(creerTache(t, liste, 0));
            }

            if (!tachesListe.getChildren().isEmpty()) {
                auMoinsUne = true;
                Label titre = new Label(liste.getTitre() + " :");
                contenu.getChildren().addAll(titre, tachesListe);
            }
        }
        if (!auMoinsUne) contenu.getChildren().add(new Label("Aucune tâche"));

        fleche.setOnAction(e -> toggle(contenu, fleche));
        return new VBox(6, fleche, contenu);
    }

    private VBox creerTache(Tache t, Liste l, int espace) {
        boolean hasChildren = t instanceof CompositeTache ct && !ct.getTaches().isEmpty();

        Button fleche = creerFleche(hasChildren ? "▾" : "");
        fleche.setDisable(!hasChildren);
        fleche.setOpacity(hasChildren ? 1 : 0);

        CheckBox check = new CheckBox();
        check.setSelected(t.estFait());

        Label badge = new Label(t.getPriorite().getLabel());
        String styleBase = "-fx-text-fill: white; -fx-padding: 1 4; -fx-background-radius: 999; -fx-font-size: 10px; -fx-font-weight: bold;";
        badge.setStyle(
                switch (t.getPriorite()) {
                    case NORMAL -> "-fx-background-color: #93c47d;" + styleBase;
                    case IMPORTANT -> "-fx-background-color: #ffbb42;" + styleBase;
                    case URGENT -> "-fx-background-color: #ef4444;" + styleBase;
                }
        );

        Button edit = new Button("Modifier");
        edit.setStyle("-fx-background-color: transparent; -fx-padding: 0px 10px; -fx-text-fill: blue");
        edit.setOnAction(e -> Popup.ouvrirPopUpTache(l, t, null, model));

        ImageView deleteIcon = new ImageView("file:icons/trash-can-solid-full.png");
        deleteIcon.setFitWidth(15);
        deleteIcon.setFitHeight(15);
        deleteIcon.setPickOnBounds(true);
        deleteIcon.setOnMouseClicked(e -> {Popup.supprimerTache(l, t, model);});

        VBox enfants = new VBox();
        if (hasChildren) {
            CompositeTache ct = (CompositeTache) t;
            for (Tache child : ct.getTaches()) enfants.getChildren().add(creerTache(child, null,espace + 1));
            fleche.setOnAction(e -> toggle(enfants, fleche));
        }

        VBox bloc = new VBox();
        bloc.setTranslateX(espace * 10);
        bloc.getChildren().add(new HBox(5, fleche, check, badge, new Label(t.getTitre()), edit, deleteIcon));
        if (hasChildren) bloc.getChildren().add(enfants);
        return bloc;
    }

    // Méthodes Utilitaires
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