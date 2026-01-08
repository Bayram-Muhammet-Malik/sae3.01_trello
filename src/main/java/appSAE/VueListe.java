package appSAE;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VueListe extends ScrollPane implements Observateur {

    private final Model model;
    private final ComboBox<String> triBox;
    private final ComboBox<String> filtreBox;

    public VueListe(Model modele) {
        this.model = modele;
        this.setStyle("-fx-background-color: transparent;");
        this.setFitToWidth(true);

        triBox = new ComboBox<>();
        triBox.getItems().addAll("Titre A-Z", "Titre Z-A", "Priorité ↑", "Priorité ↓", "Durée ↑", "Durée ↓");
        triBox.setValue("Titre A→Z");

        filtreBox = new ComboBox<>();
        filtreBox.getItems().addAll("Tous", "Fait", "Non fait");
        filtreBox.setValue("Tous");

        triBox.valueProperty().addListener((obs, o, n) -> actualiser(model));
        filtreBox.valueProperty().addListener((obs, o, n) -> actualiser(model));
    }

    @Override
    public void actualiser(Sujet sujet) {
        VBox conteneurAgenda = new VBox(8);

        HBox barreTri = new HBox(10, new Label("Trier par :"), triBox, new Label("Filtrer :"), filtreBox);
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
        Button fleche = creerFleche("▾ " + DateTimeFormatter.ofPattern("EEEE").format(jour) + " - " + DateTimeFormatter.ofPattern("dd-MM-yyyy").format(jour));

        VBox contenu = new VBox(8);
        contenu.setStyle("-fx-background-color: white; -fx-border-radius: 6; -fx-padding: 5px");

        boolean auMoinsUne = false;

        for (Liste liste : model.getListes()) {

            List<Tache> tachesDuJour = model.getTachesFromListe(liste).stream().filter(t -> estActiveLeJour(t, jour)).filter(t -> switch (filtreBox.getValue()) {
                        case "Fait" -> t.estFait();
                        case "Non fait" -> !t.estFait();
                        default -> true;
                    }).toList();

            if (tachesDuJour.isEmpty()) continue;

            List<Tache> tachesTriees = new ArrayList<>(tachesDuJour);
            tachesTriees.sort(getComparator());

            VBox tachesListe = new VBox(3);
            for (Tache t : tachesTriees) tachesListe.getChildren().add(creerTache(t, liste, 0, jour));
            contenu.getChildren().add(tachesListe);
            auMoinsUne = true;
        }

        if (!auMoinsUne) contenu.getChildren().add(new Label("Aucune tâche"));

        fleche.setOnAction(e -> toggle(contenu, fleche));
        return new VBox(6, fleche, contenu);
    }

    private Comparator<Tache> getComparator() {
        return switch (triBox.getValue()) {
            case "Titre A→Z" -> Comparator.comparing(t -> t.getTitre().toLowerCase());
            case "Titre Z→A" -> Comparator.comparing((Tache t) -> t.getTitre().toLowerCase()).reversed();
            case "Priorité ↑" -> Comparator.comparing(Tache::getPriorite);
            case "Priorité ↓" -> Comparator.comparing(Tache::getPriorite).reversed();
            case "Durée ↑" -> Comparator.comparingInt(this::calculerDuree);
            case "Durée ↓" -> Comparator.comparingInt(this::calculerDuree).reversed();
            default -> Comparator.comparing(t -> t.getTitre().toLowerCase());
        };
    }

    private VBox creerTache(Tache t, Liste l, int niveau, LocalDate jourCourant) {
        boolean hasChildren = t instanceof CompositeTache ct && !ct.getTaches().isEmpty();

        Button fleche = creerFleche(hasChildren ? "▾" : "");
        fleche.setDisable(!hasChildren);
        fleche.setOpacity(hasChildren ? 1 : 0);

        CheckBox fait = new CheckBox();
        fait.setSelected(t.estFait());
        fait.selectedProperty().addListener((obs, oldVal, newVal) -> { model.setTacheFait(t, newVal); });

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
        edit.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-padding: 0 4; -fx-font-size: 11px;");
        edit.setOnAction(e -> Popup.ouvrirPopUpTache(l, t, null, model));

        ImageView deleteIcon = new ImageView("file:icons/trash-can-solid-full.png");
        deleteIcon.setFitWidth(15);
        deleteIcon.setFitHeight(15);
        deleteIcon.setPickOnBounds(true);
        Tooltip.install(deleteIcon, new Tooltip("Supprimer"));
        deleteIcon.setOnMouseClicked(e -> Popup.supprimerTache(l, t, model));

        HBox ligne = new HBox(5, fleche, fait, badge, new Label(t.getTitre()), edit, deleteIcon);

        // afficher la tâche préalable si présente
        VBox blocLigne = new VBox(2);
        blocLigne.getChildren().add(ligne);
        if (t.getPrerequise() != null) {
            Label prereqLabel = new Label("Après : " + t.getPrerequise().getTitre());
            prereqLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 10px;");
            blocLigne.getChildren().add(prereqLabel);
        }

        VBox enfants = new VBox(2);
        if (hasChildren) {
            CompositeTache ct = (CompositeTache) t;

            List<Tache> enfantsFiltres = ct.getTaches().stream().filter(child -> estActiveLeJour(child, jourCourant)).filter(child -> switch (filtreBox.getValue()) {
                        case "Fait" -> child.estFait();
                        case "Non fait" -> !child.estFait();
                        default -> true;
                    }).sorted(getComparator()).toList();

            for (Tache child : enfantsFiltres) enfants.getChildren().add(creerTache(child, null, niveau + 1, jourCourant));
            fleche.setOnAction(e -> toggle(enfants, fleche));
        }

        VBox bloc = new VBox(2, blocLigne);
        if (hasChildren) bloc.getChildren().add(enfants);

        if (niveau == 0) {
            bloc.setStyle("-fx-padding: 2 4;");
        } else {
            bloc.setStyle("-fx-padding: 2 4; -fx-border-color: #d4d4d4; -fx-border-width: 0 0 0 2; -fx-border-insets: 0 0 0 " + niveau * 5 + ";");
            bloc.setTranslateX(niveau * 5);
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

    private boolean estActiveLeJour(Tache t, LocalDate jour) {
        LocalDate d = t.getDebut() != null ? t.getDebut() : null;
        LocalDate f = t.getFin() != null ? t.getFin() : null;
        return d != null && !jour.isBefore(d) && (f == null || !jour.isAfter(f));
    }

    private int calculerDuree(Tache t) {
        if (t.getDebut() == null) return Integer.MAX_VALUE;
        if (t.getFin() == null) return 1;

        long d1 = t.getDebut().toEpochDay();
        long d2 = t.getFin().toEpochDay();
        return (int) Math.max((d2 - d1) + 1, 1);
    }
}