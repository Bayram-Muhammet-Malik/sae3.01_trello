package appSAE;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VueBureau extends HBox implements Observateur {

    private final Model model;

    public VueBureau(Model model) {
        this.model = model;
        this.setSpacing(5);
    }

    @Override
    public void actualiser(Sujet sujet) {
        this.getChildren().clear();

        for (Liste ls : model.getListes()) {
            this.getChildren().add(creerColonne(ls));
        }
    }

    private VBox creerColonne(Liste ls) {
        VBox colonne = new VBox();
        colonne.setSpacing(10);
        colonne.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 12px; -fx-background-radius: 8px;");
        colonne.setPrefWidth(220);

        Label titre = new Label(ls.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Button creerTacheBtn = new Button("+ Créer une tâche");
        creerTacheBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e5e7eb; -fx-background-radius: 8px;");
        creerTacheBtn.setOnAction(e -> ouvrirPopUpTache(ls)); // <-- popup tache


        colonne.getChildren().add(titre);

        for (Tache t : model.getTachesFromListe(ls)) {
            colonne.getChildren().add(creerTache(t));
        }

        colonne.getChildren().add(creerTacheBtn);

        return colonne;
    }

    private void ouvrirPopUpTache(Liste liste) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("créer une tâche");

        ButtonType btnCreer = new ButtonType("créer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType("annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCreer, btnAnnuler);

        // champs
        TextField champTitre = new TextField();
        champTitre.setPromptText("nom de la tâche");

        TextArea champDescription = new TextArea();
        champDescription.setPromptText("description");
        champDescription.setPrefRowCount(3);
        champDescription.setWrapText(true);

        DatePicker champDate = new DatePicker(LocalDate.now());

        ComboBox<Tache.Priorite> champPriorite = new ComboBox<>();
        champPriorite.getItems().addAll(Tache.Priorite.values());
        champPriorite.setValue(Tache.Priorite.NORMAL);

        // petit titre de popup
        Text titre = new Text("nouvelle tâche");
        titre.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("titre"), 0, 0);
        grid.add(champTitre, 1, 0);

        grid.add(new Label("description"), 0, 1);
        grid.add(champDescription, 1, 1);

        grid.add(new Label("date"), 0, 2);
        grid.add(champDate, 1, 2);

        grid.add(new Label("priorité"), 0, 3);
        grid.add(champPriorite, 1, 3);

        ColumnConstraints c0 = new ColumnConstraints();
        c0.setMinWidth(90);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        // “carte” blanche avec bord + arrondis
        VBox carte = new VBox(12, titre, grid);
        carte.setPadding(new Insets(14));
        carte.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 12;"
        );

        VBox root = new VBox(carte);
        root.setPadding(new Insets(14));
        root.setStyle("-fx-background-color: #f3f4f6;");

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(root);

        // style global du dialog
        pane.setStyle(
                "-fx-background-color: #f3f4f6;" +
                        "-fx-font-size: 13;"
        );

        // style des champs (propre)
        champTitre.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        champDescription.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        champDate.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        champPriorite.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        // style boutons (bleu / gris)
        Button bCreer = (Button) pane.lookupButton(btnCreer);
        Button bAnnuler = (Button) pane.lookupButton(btnAnnuler);

        bCreer.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 14;");
        bAnnuler.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 8; -fx-padding: 8 14;");

        // bloque “créer” si titre vide
        bCreer.setDisable(true);
        champTitre.textProperty().addListener((obs, oldV, newV) ->
                bCreer.setDisable(newV == null || newV.isBlank())
        );

        dialog.showAndWait().ifPresent(result -> {
            if (result != btnCreer) return;

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dateString = champDate.getValue().format(fmt);

            CompositeTache tache = new CompositeTache(
                    champTitre.getText().trim(),
                    champDescription.getText(),
                    dateString,
                    champPriorite.getValue()
            );

            liste.ajouterCarte(tache);
            model.notifierObservateur();
        });
    }



    private VBox creerTache(Tache tsk) {
        VBox tache = new VBox(6);
        tache.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-background-radius: 8px;");

        Label titre = new Label(tsk.getTitre());
        Label description = new Label(tsk.getDescription());

        Label etiquette;

        switch (tsk.getPriorite()) {
            case NORMAL -> {
                etiquette = new Label("standard");
                etiquette.setStyle("-fx-background-color: #93c47d; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 999;");
            }
            case SECONDAIRE -> {
                etiquette = new Label("important");
                etiquette.setStyle("-fx-background-color: #ff9900; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 999;");
            }
            case URGENT -> {
                etiquette = new Label("urgent");
                etiquette.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 999;");
            }
            default -> etiquette = new Label("");
        }

        tache.getChildren().addAll(titre, etiquette, description);
        return tache;
    }
}
