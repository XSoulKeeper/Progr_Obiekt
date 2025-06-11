package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;

import java.util.*;
import java.util.List;

public class SymulacjaUI extends Application {

    private GridPane grid = new GridPane();
    private Symulacja symulacja;
    private int rozmiar;
    private static final int WINDOW_WIDTH = 1280; // Increased width to accommodate stats
    private static final int WINDOW_HEIGHT = 720;
    private Map<Integer, Label> occupiedSquaresLabels = new HashMap<>();
    private Map<Integer, Label> unitsLabels = new HashMap<>();
    private Map<Integer, Label> strengthLabels = new HashMap<>();
    private Map<Integer, ImageView> raceIcons = new HashMap<>();
    private Map<Integer, String> raceImagePaths = new HashMap<>();

    // Nowe stałe dla ścieżek ikon
    private static final String APP_ICON_PATH = "app_icon.jpg";
    private static final String ERROR_ICON_PATH = "error.jpg";
    private Label przeszkodyLabel = new Label("Przeszkoda:0");

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Ustawienie ikony aplikacji
        try {
            Image appIcon = new Image(APP_ICON_PATH);
            primaryStage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.err.println("Nie udało się załadować ikony aplikacji: " + APP_ICON_PATH + ". Upewnij się, że plik istnieje i jest dostępny.");
            // e.printStackTrace(); // Odkomentuj, jeśli chcesz zobaczyć pełny stos wywołań błędu
        }


        // Inicjalizacja mapy ścieżek do obrazów ras
        raceImagePaths.put(0, "elf.jpg");
        raceImagePaths.put(1, "krasnal.jpg");
        raceImagePaths.put(2, "ork.jpg");
        raceImagePaths.put(3, "rycerz.jpg");


        // --- Controls at the top ---
        TextField liczbaRasField = new TextField();
        TextField jednostkiField = new TextField();
        TextField rozmiarField = new TextField();
        TextField przeszkodyField = new TextField();
        Button startButton = new Button("Start");
        Button fullscreenButton = new Button("Tryb Pełnoekranowy");

        liczbaRasField.setPromptText("Liczba ras (2-4)");
        jednostkiField.setPromptText("Jednostki (np. 100)");
        rozmiarField.setPromptText("Rozmiar mapy (np. 10)");
        przeszkodyField.setPromptText(" Liczba przeszkód (np.8%)");

        HBox controls = new HBox(10, liczbaRasField, jednostkiField, rozmiarField, przeszkodyField, startButton, fullscreenButton);
        controls.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-background-color: #e0e0e0;");
        root.setTop(controls);

        // --- Map Container (Left/Center) ---
        StackPane mapContainer = new StackPane();
        mapContainer.getChildren().add(grid);
        mapContainer.setStyle("-fx-background-color: lightgray; -fx-border-color: gray; -fx-border-width: 1;");
        root.setCenter(mapContainer);

        // --- Race Statistics Panel (Right) ---
        VBox statsPanel = new VBox(15);
        statsPanel.setStyle("-fx-padding: 15; -fx-background-color: #d0d0d0; -fx-border-color: gray; -fx-border-width: 0 0 0 1;");
        statsPanel.setAlignment(Pos.TOP_LEFT);

        root.setRight(statsPanel);

        startButton.setOnAction(e -> {
            int liczbaRas;
            int jednostki;
            int rozmiarMapy;
            int przeszkody;// Zmieniona nazwa zmiennej, aby uniknąć konfliktu z polem klasy 'rozmiar'

            try {
                liczbaRas = Integer.parseInt(liczbaRasField.getText());
                jednostki = Integer.parseInt(jednostkiField.getText());
                rozmiarMapy = Integer.parseInt(rozmiarField.getText());
                przeszkody = Integer.parseInt(przeszkodyField.getText());

                // Walidacja danych wejściowych
                if (liczbaRas < 2 || liczbaRas > 4 || jednostki <= 0 || rozmiarMapy <= 0) {
                    showErrorAlert("Nieprawidłowe dane wejściowe!",
                            "Liczba ras musi być od 2 do 4.\n" +
                                    "Liczba jednostek i rozmiar mapy muszą być większe od 0.");
                    return; // Zakończ działanie, jeśli dane są nieprawidłowe
                }

                // Ustaw rozmiar pola klasy po walidacji
                this.rozmiar = rozmiarMapy;

            } catch (NumberFormatException ex) {
                showErrorAlert("Błąd formatu danych!", "Wprowadź poprawne liczby dla liczby ras, jednostek i rozmiaru mapy.");
                return; // Zakończ działanie, jeśli wystąpi błąd parsowania
            }

            List<RasaBase> rasy = new ArrayList<>();
            // Clear previous stats elements
            occupiedSquaresLabels.clear();
            unitsLabels.clear();
            strengthLabels.clear();
            raceIcons.clear();
            statsPanel.getChildren().clear(); // Clear existing stats from the panel
            statsPanel.getChildren().add(przeszkodyLabel);
            // Initialize races and their UI components
            if (liczbaRas >= 1) {
                Elfy elfy = new Elfy(0, jednostki, jednostki / 2, 3);
                rasy.add(elfy);
                addRaceStatsToPanel(statsPanel, elfy, "elf.jpg");
            }
            if (liczbaRas >= 2) {
                Krasnoludy krasnoludy = new Krasnoludy(1, jednostki, jednostki / 3, 4);
                rasy.add(krasnoludy);
                addRaceStatsToPanel(statsPanel, krasnoludy, "krasnal.jpg");
            }
            if (liczbaRas >= 3) {
                Orkowie orkowie = new Orkowie(2, jednostki, jednostki / 4, 2);
                rasy.add(orkowie);
                addRaceStatsToPanel(statsPanel, orkowie, "ork.jpg");
            }
            if (liczbaRas >= 4) {
                Ludzie ludzie = new Ludzie(3, jednostki, jednostki / 3, 3);
                rasy.add(ludzie);
                addRaceStatsToPanel(statsPanel, ludzie, "rycerz.jpg");
            }

            symulacja = new Symulacja(rozmiar, 0, rasy, przeszkody); // Użyj 'this.rozmiar'
            symulacja.inicjalizuj();
            drawMap();
            updateRaceStatistics(rasy); // Initial update of statistics
            int przeszkodyCoun = 0;
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if(symulacja.getMapa()[i][j].isPrzeszkoda()){
                        przeszkodyCoun++;
                    }
                }
            }przeszkodyLabel.setText("Przeszkody"+przeszkodyCoun);
            // Zatrzymaj poprzedni timer, jeśli istnieje, aby uniknąć wielu symulacji jednocześnie
            if (currentTimer != null) {
                currentTimer.cancel();
            }
            currentTimer = new Timer(); // Utwórz nowy timer
            currentTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    symulacja.tura();
                    javafx.application.Platform.runLater(() -> {
                        drawMap();
                        updateRaceStatistics(rasy); // Update statistics every turn
                    });
                    if (symulacja.czyKoniec()) {
                        currentTimer.cancel(); // Anuluj timer
                        javafx.application.Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Koniec Symulacji");
                            alert.setHeaderText(null);

                            // Find the winning race
                            int winningRaceId = -1;
                            for (int i = 0; i < rozmiar; i++) { // Użyj 'this.rozmiar'
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

                            Set<Integer> zwyciezcyIds = symulacja.getZwyciezcy();
                            StringBuilder wynik = new StringBuilder();
                            for (int id : zwyciezcyIds) {
                                for (RasaBase rasa : rasy) {
                                    if (rasa.getId() == id) {
                                        wynik.append(rasa.getNazwa()).append(", ");
                                    }
                                }
                            }
                            if (wynik.length() > 0) {
                                wynik.setLength(wynik.length() - 2); // usuń ostatni przecinek i spację
                            }

                            alert.setContentText("Symulacja zakończona! Zwycięzcy: " + (wynik.length() > 0 ? wynik.toString() : "Brak zwycięzców"));

                            // Dodaj ikonę zwycięzcy (pierwszej rasy z listy zwycięzców)
                            if (!zwyciezcyIds.isEmpty()) {
                                int firstWinnerId = zwyciezcyIds.iterator().next();
                                String imagePath = raceImagePaths.get(firstWinnerId);
                                if (imagePath != null) {
                                    try {
                                        Image winnerImage = new Image(imagePath, 80, 80, true, true);
                                        ImageView winnerImageView = new ImageView(winnerImage);
                                        alert.setGraphic(winnerImageView);
                                    } catch (Exception ex) {
                                        System.err.println("Nie udało się załadować ikony zwycięzcy: " + imagePath);
                                    }
                                }
                            }

                            alert.showAndWait();
                        });
                    }
                }
            }, 1000, 1000);
        });

        fullscreenButton.setOnAction(e -> {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
        });

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Symulacja Ras");
        primaryStage.show();
    }

    private Timer currentTimer; // Dodane pole do przechowywania aktualnego timera

    private void addRaceStatsToPanel(VBox statsPanel, RasaBase rasa, String imageFileName) {
        HBox raceBox = new HBox(10);
        raceBox.setAlignment(Pos.CENTER_LEFT);

        try {
            Image image = new Image(imageFileName, 50, 50, true, true);
            ImageView imageView = new ImageView(image);
            raceIcons.put(rasa.getId(), imageView);
            raceBox.getChildren().add(imageView);
        } catch (Exception e) {
            System.err.println("Nie udało się załadować ikony rasy: " + imageFileName);
            // e.printStackTrace();
        }

        VBox infoBox = new VBox(2);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(rasa.getNazwa());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label occupiedLabel = new Label("Zajęte kratki: 0");
        occupiedSquaresLabels.put(rasa.getId(), occupiedLabel);

        Label unitsLabel = new Label("Jednostki: Piechota: " + rasa.getPiechota() + ", Łucznicy: " + rasa.getLucznicy());
        unitsLabels.put(rasa.getId(), unitsLabel);

        Label strengthLabel = new Label("Siła: " + rasa.sila());
        strengthLabels.put(rasa.getId(), strengthLabel);

        infoBox.getChildren().addAll(nameLabel, occupiedLabel, unitsLabel, strengthLabel);
        raceBox.getChildren().add(infoBox); // Dodaj infoBox do raceBox
        statsPanel.getChildren().add(raceBox);
    }

    private void updateRaceStatistics(List<RasaBase> rasy) {
        // Calculate occupied squares
        Map<Integer, Integer> occupiedCount = new HashMap<>();
        for (RasaBase rasa : rasy) {
            occupiedCount.put(rasa.getId(), 0);
        }

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                int ownerId = symulacja.getMapa()[i][j].getOwnerRasaId();
                if (ownerId != -1) {
                    occupiedCount.put(ownerId, occupiedCount.get(ownerId) + 1);
                }
            }
        }

        // Update labels
        for (RasaBase rasa : rasy) {
            if (occupiedSquaresLabels.containsKey(rasa.getId())) {
                occupiedSquaresLabels.get(rasa.getId()).setText("Zajęte kratki: " + occupiedCount.get(rasa.getId()));
            }
            if (unitsLabels.containsKey(rasa.getId())) {
                unitsLabels.get(rasa.getId()).setText("Jednostki: Piechota: " + rasa.getPiechota() + ", Łucznicy: " + rasa.getLucznicy());
            }
            if (strengthLabels.containsKey(rasa.getId())) {
                strengthLabels.get(rasa.getId()).setText("Siła: " + rasa.sila());
            }
        }
    }

    private void drawMap() {
        grid.getChildren().clear();
        Kratka[][] mapa = symulacja.getMapa();
        Color[] kolory = {Color.GREEN, Color.BLACK, Color.RED, Color.BLUE};

        double availableMapWidth = WINDOW_WIDTH * 0.7;
        double availableMapHeight = WINDOW_HEIGHT - 70;
        double cellSize = Math.min(availableMapHeight / rozmiar, availableMapWidth / rozmiar);

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                Rectangle rect = new Rectangle(cellSize, cellSize);
                Kratka kratka = mapa[i][j];

                if (kratka.isPrzeszkoda()) {
                    rect.setFill(Color.PURPLE); // <-- TU kolor przeszkody

                } else {
                    int id = kratka.getOwnerRasaId();
                    rect.setFill(id >= 0 && id < kolory.length ? kolory[id] : Color.WHITE);
                }

                grid.add(rect, j, i);
            }
        }
    }

    // Nowa metoda do wyświetlania alertów o błędach
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            Image errorIcon = new Image(ERROR_ICON_PATH, 64, 64, true, true); // Rozmiar ikony błędu
            ImageView errorImageView = new ImageView(errorIcon);
            alert.setGraphic(errorImageView);
        } catch (Exception ex) {
            System.err.println("Nie udało się załadować ikony błędu: " + ERROR_ICON_PATH);
            // ex.printStackTrace();
        }
        alert.showAndWait();
    }
}