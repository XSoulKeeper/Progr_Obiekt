package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.List;

public class SymulacjaUI extends Application {

    private GridPane grid = new GridPane();
    private Symulacja symulacja;
    private int rozmiar;
    private static final int WINDOW_WIDTH = 1080; // Fixed window width
    private static final int WINDOW_HEIGHT = 720; // Fixed window height

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-alignment: center;"); // Center content vertically in VBox

        TextField liczbaRasField = new TextField();
        TextField jednostkiField = new TextField();
        TextField rozmiarField = new TextField();
        Button startButton = new Button("Start");
        Button fullscreenButton = new Button("Tryb Pełnoekranowy"); // New fullscreen button

        liczbaRasField.setPromptText("Liczba ras (2-4)");
        jednostkiField.setPromptText("Jednostki (np. 100)");
        rozmiarField.setPromptText("Rozmiar mapy (np. 10)");

        startButton.setOnAction(e -> {
            int liczbaRas = Integer.parseInt(liczbaRasField.getText());
            int jednostki = Integer.parseInt(jednostkiField.getText());
            rozmiar = Integer.parseInt(rozmiarField.getText());

            List<RasaBase> rasy = new ArrayList<>();
            if (liczbaRas >= 1) rasy.add(new Elfy(0, jednostki, jednostki / 2, 3));
            if (liczbaRas >= 2) rasy.add(new Krasnoludy(1, jednostki, jednostki / 3, 4));
            if (liczbaRas >= 3) rasy.add(new Orkowie(2, jednostki, jednostki / 4, 2));
            if (liczbaRas >= 4) rasy.add(new Ludzie(3, jednostki, jednostki / 3, 3));

            symulacja = new Symulacja(rozmiar, 0, rasy);
            symulacja.inicjalizuj();
            drawMap();

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    symulacja.tura();
                    javafx.application.Platform.runLater(() -> drawMap());
                    if (symulacja.czyKoniec()) {
                        timer.cancel();
                        javafx.application.Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Koniec Symulacji");
                            alert.setHeaderText(null);
                            // Find the winning race
                            int winningRaceId = -1;
                            for (int i = 0; i < rozmiar; i++) {
                                for (int j = 0; j < rozmiar; j++) {
                                    if (symulacja.getMapa()[i][j].getOwnerRasaId() != -1) {
                                        winningRaceId = symulacja.getMapa()[i][j].getOwnerRasaId();
                                        break;
                                    }
                                }
                                if (winningRaceId != -1) break;
                            }
                            RasaBase winner = null;
                            for (RasaBase rasa : rasy) {
                                if (rasa.getId() == winningRaceId) {
                                    winner = rasa;
                                    break;
                                }
                            }
                            if (winner != null) {
                                alert.setContentText("Symulacja zakończona! Zwyciężyła rasa: " + winner.getNazwa());
                            } else {
                                alert.setContentText("Symulacja zakończona! Brak zwycięzcy.");
                            }
                            alert.showAndWait();
                        });
                    }
                }
            }, 1000, 1000);
        });

        fullscreenButton.setOnAction(e -> {
            primaryStage.setFullScreen(!primaryStage.isFullScreen()); // Toggle fullscreen mode
        });

        HBox controls = new HBox(10, liczbaRasField, jednostkiField, rozmiarField, startButton, fullscreenButton);
        controls.setStyle("-fx-alignment: center;"); // Center controls horizontally

        StackPane mapContainer = new StackPane();
        mapContainer.getChildren().add(grid);
        mapContainer.setStyle("-fx-background-color: lightgray;"); // Optional: background for map container

        // Use VBox.setVgrow to make the map container grow and fill available space, pushing controls to top
        VBox.setVgrow(mapContainer, Priority.ALWAYS);
        root.getChildren().addAll(controls, mapContainer);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Symulacja Ras");
        primaryStage.show();
    }

    private void drawMap() {
        grid.getChildren().clear();
        Kratka[][] mapa = symulacja.getMapa();
        // Ensure consistent color mapping regardless of the number of races
        Color[] kolory = {Color.GREEN, Color.BLACK, Color.RED, Color.BLUE};

        // Calculate cell size based on the smaller dimension of the grid and the available space
        // This ensures the map always fits within the window and is square.
        // We subtract a bit from WINDOW_HEIGHT to account for the controls HBox.
        double availableMapHeight = WINDOW_HEIGHT - 70; // Approximate height for controls + padding
        double cellSize = Math.min(availableMapHeight / rozmiar, (double) WINDOW_WIDTH / rozmiar);

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                Rectangle rect = new Rectangle(cellSize, cellSize);
                int id = mapa[i][j].getOwnerRasaId();
                // Assign colors based on the race ID, defaulting to white for unowned or out-of-bound IDs
                rect.setFill(id >= 0 && id < kolory.length ? kolory[id] : Color.WHITE);
                grid.add(rect, j, i);
            }
        }
        // Center the grid within its allocated space
        grid.setHalignment(grid, javafx.geometry.HPos.CENTER);
        grid.setValignment(grid, javafx.geometry.VPos.CENTER);
    }
}