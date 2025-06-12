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
import java.io.FileWriter;
import java.io.IOException;
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
    private List<String> chatLogs = new ArrayList<>();
    private ListView<String> chatLogView = new ListView<>();

    // Nowe stałe dla ścieżek ikon
    private static final String APP_ICON_PATH = "app_icon.jpg";
    private static final String ERROR_ICON_PATH = "error.jpg";
    private Label przeszkodyLabel = new Label("Przeszkoda:0");

    @Override
    public void start(Stage primaryStage) {

        try (FileWriter fw = new FileWriter("src/main/resources/zwyciezcy.csv", false)) {
        } catch (IOException e) {
            System.err.println("Nie udało się wyczyścić pliku CSV: " + e.getMessage());
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Ustawienie ikony aplikacji
        try {
            Image appIcon = new Image(APP_ICON_PATH);
            primaryStage.getIcons().add(appIcon);
        }
        catch (Exception e) {
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
        Button wylonZwyciezceButton = new Button("Wyłon zwycięzcę teraz");
        Button rankingButton = new Button("Ranking");

        liczbaRasField.setPromptText("Liczba ras (2-4)");
        jednostkiField.setPromptText("Jednostki (np. 100)");
        rozmiarField.setPromptText("Rozmiar mapy (np. 10)");
        przeszkodyField.setPromptText(" Liczba przeszkód (np.8%)");


        HBox controls = new HBox(10, liczbaRasField, jednostkiField, rozmiarField, przeszkodyField, startButton, fullscreenButton, wylonZwyciezceButton, rankingButton);
        controls.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-background-color: #e0e0e0;");
        root.setTop(controls);

// --- Map Container (Left/Center) ---
        StackPane mapContainer = new StackPane();
        mapContainer.getChildren().add(grid);
        mapContainer.setStyle("-fx-background-color: lightgray; -fx-border-color: gray; -fx-border-width: 1;");

// --- Chat Log (Right of map) ---
        chatLogView.setPrefWidth(320);
        chatLogView.setPrefHeight(300);
        chatLogView.setStyle("-fx-background-color: #ffffff; -fx-border-color: gray; -fx-border-width: 1; -fx-font-size: 9px");

// --- HBox: mapa po lewej, logi po prawej ---
        HBox mapAndLogBox = new HBox(10, mapContainer, chatLogView);
        mapAndLogBox.setStyle("-fx-padding: 10;");

// --- VBox: całość (mapa+logi) + reszta ---
        VBox centerPanel = new VBox(10, mapAndLogBox);
        centerPanel.setStyle("-fx-padding: 2 10 10 10;");
        root.setCenter(centerPanel);

        // --- Race Statistics Panel (Right) ---
        VBox statsPanel = new VBox(15);
        statsPanel.setStyle("-fx-padding: 15; -fx-background-color: #d0d0d0; -fx-border-color: gray; -fx-border-width: 0 0 0 1;");
        statsPanel.setAlignment(Pos.TOP_LEFT);

        root.setRight(statsPanel);

        startButton.setOnAction(e -> {
            startButton.setDisable(true);
            int liczbaRas;
            int jednostki;
            int rozmiarMapy;
            int przeszkody; // Zmieniona nazwa zmiennej, aby uniknąć konfliktu z polem klasy 'rozmiar'

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
                showErrorAlert("Błąd formatu danych!", "Wprowadź poprawne liczby dla liczby ras, jednostek, rozmiaru mapy i przeszkód.");
                return; // Zakończ działanie, jeśli wystąpi błąd parsowania
            }

            List<RasaBase> rasy = new ArrayList<>();
            // Clear previous stats elements
            occupiedSquaresLabels.clear();
            unitsLabels.clear();
            strengthLabels.clear();
            raceIcons.clear();
            statsPanel.getChildren().clear(); // Clear existing stats from the panel
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

            addTerrainImageBelowStats(statsPanel);

            symulacja = new Symulacja(rozmiar, 0, rasy, przeszkody); // Użyj 'this.rozmiar'
            symulacja.inicjalizuj();
            drawMap();
            updateRaceStatistics(rasy); // Initial update of statistics
            int przeszkodyCoun = 0;
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if (symulacja.getMapa()[i][j].isPrzeszkoda()) {
                        przeszkodyCoun++;
                    }
                }
            }
            przeszkodyLabel.setText("Przeszkody: " + przeszkodyCoun); // Poprawiona etykieta

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
                        String log = symulacja.getOstatniLog();
                        chatLogs.add(0, log);
                        if (chatLogs.size() > 50) chatLogs.remove(chatLogs.size() - 1); // Usuń najstarszy z dołu
                        chatLogView.getItems().setAll(chatLogs);
                        chatLogView.scrollTo(0); // Przewiń do góry (najnowszy)
                    });
                    if (symulacja.czyKoniec()) {
                        currentTimer.cancel(); // Anuluj timer
                        javafx.application.Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Koniec Symulacji");
                            startButton.setDisable(false);
                            alert.setHeaderText(null);

                            // Calculate occupied squares for all races
                            Map<Integer, Integer> occupiedCount = new HashMap<>();
                            for (RasaBase r : rasy) {
                                occupiedCount.put(r.getId(), 0);
                            }

                            for (int i = 0; i < rozmiar; i++) {
                                for (int j = 0; j < rozmiar; j++) {
                                    int ownerId = symulacja.getMapa()[i][j].getOwnerRasaId();
                                    if (ownerId != -1) {
                                        occupiedCount.put(ownerId, occupiedCount.get(ownerId) + 1);
                                    }
                                }
                            }

                            // Determine winner(s) based on occupied squares, then strength
                            List<RasaBase> potentialWinners = new ArrayList<>();
                            int maxOccupied = -1;

                            for (RasaBase r : rasy) {
                                int currentOccupied = occupiedCount.getOrDefault(r.getId(), 0);
                                if (currentOccupied > maxOccupied) {
                                    maxOccupied = currentOccupied;
                                    potentialWinners.clear();
                                    potentialWinners.add(r);
                                } else if (currentOccupied == maxOccupied && currentOccupied > 0) { // Only add if they actually occupy squares
                                    potentialWinners.add(r);
                                }
                            }

                            List<RasaBase> finalWinners = new ArrayList<>();
                            if (potentialWinners.isEmpty()) {
                                // No winners if no squares are occupied
                                // This case means symulacja.czyKoniec() might be true but no race occupied squares
                                // (e.g. 100% obstacles, or all races wiped out before occupying anything)
                            } else if (potentialWinners.size() == 1) {
                                // Only one winner based on occupied squares
                                finalWinners.addAll(potentialWinners);
                            } else {
                                // Tie in occupied squares, now check strength
                                double maxStrength = -1;
                                for (RasaBase pw : potentialWinners) {
                                    double currentStrength = pw.silaZBonusem();
                                    if (currentStrength > maxStrength) {
                                        maxStrength = currentStrength;
                                        finalWinners.clear(); // Clear, as we found a new, higher strength
                                        finalWinners.add(pw);
                                    } else if (currentStrength == maxStrength) {
                                        finalWinners.add(pw); // Add, as it's a tie in strength
                                    }
                                }
                            }

                            StringBuilder wynik = new StringBuilder();
                            if (finalWinners.isEmpty()) {
                                wynik.append("Brak zwycięzców");
                            } else {
                                // Sortowanie zwycięzców alfabetycznie dla spójności wyświetlania
                                finalWinners.sort(Comparator.comparing(RasaBase::getNazwa));
                                for (RasaBase winnerRasa : finalWinners) {
                                    wynik.append(winnerRasa.getNazwa()).append(" (Zajęte: ").append(occupiedCount.get(winnerRasa.getId())).append(", Siła: ").append((int) Math.round(winnerRasa.silaZBonusem())).append("), ");
                                }
                                if (wynik.length() > 0) {
                                    wynik.setLength(wynik.length() - 2); // Usuń ostatni przecinek i spację
                                }

                                    for (RasaBase winnerRasa : finalWinners) {
                                        int zajeteKratki = occupiedCount.get(winnerRasa.getId());
                                        zapiszZwyciezceDoCSV(winnerRasa, zajeteKratki);
                                    }
                            }

                            alert.setContentText("Symulacja zakończona! Zwycięzcy: " + wynik.toString());

                            // Dodaj ikonę zwycięzcy (pierwszej rasy z listy zwycięzców)
                            if (!finalWinners.isEmpty()) {
                                int firstWinnerId = finalWinners.get(0).getId();
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

        rankingButton.setOnAction(e -> {
            try {
                RankingResult result = obliczRanking("src/main/resources/zwyciezcy.csv");
                if (result == null) {
                    showErrorAlert("Brak danych", "Brak danych w pliku zwyciezcy.csv.");
                    return;
                }
                // Zapisz do ranking.csv (reset pliku)
                try (FileWriter fw = new FileWriter("src/main/resources/ranking.csv", false)) {
                    fw.write(result.toCsvLine());
                } catch (IOException ex) {
                    System.err.println("Błąd zapisu do ranking.csv: " + ex.getMessage());
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ranking zwycięzców");
                alert.setHeaderText(result.nazwaRasy);
                alert.setContentText(
                        "Średnia siła: " + (int) Math.round(result.sredniaSila) + "\n" +
                                "Średnia zajętych kratek: " + (int) Math.round(result.srednieKratki) + "\n" +
                                "Średnia piechoty: " + (int) Math.round(result.sredniaPiechota) + "\n" +
                                "Średnia łuczników: " + (int) Math.round(result.sredniLucznicy) + "\n" +
                                "Liczba zwycięstw: " + result.liczbaWygranych
                );
                String imagePath = raceImagePaths.get(result.idRasy);
                if (imagePath != null) {
                    try {
                        Image winnerImage = new Image(imagePath, 120, 120, true, true);
                        ImageView winnerImageView = new ImageView(winnerImage);
                        winnerImageView.setPreserveRatio(true);
                        winnerImageView.setFitWidth(250);
                        winnerImageView.setFitHeight(250);

                        StackPane centeredGraphic = new StackPane(winnerImageView);
                        centeredGraphic.setPrefSize(110, 110);
                        centeredGraphic.setAlignment(Pos.CENTER);

                        alert.getDialogPane().setGraphic(centeredGraphic);
                    } catch (Exception ex) {
                        System.err.println("Nie udało się załadować ikony rasy: " + imagePath);
                    }
                }
                alert.showAndWait();
            } catch (Exception ex) {
                showErrorAlert("Błąd rankingu", ex.getMessage());
            }
        });

        wylonZwyciezceButton.setOnAction(e -> {
    if (symulacja != null) {
        symulacja.wylonZwyciezceTeraz();
    }
});



        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Symulacja Bitwy Śródziemia");
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

        // Zmiana tutaj: zaokrąglenie siły
        Label strengthLabel = new Label("Siła: " + (int) Math.round(rasa.silaZBonusem()));
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
            boolean pokonany = false;
            if (occupiedSquaresLabels.containsKey(rasa.getId())) {
                occupiedSquaresLabels.get(rasa.getId()).setText("Zajęte kratki: " + occupiedCount.get(rasa.getId()));
            }
            if (unitsLabels.containsKey(rasa.getId())) {
                if (occupiedCount.get(rasa.getId()) == 0) { // If no territory, set units to 0
                    unitsLabels.get(rasa.getId()).setText("Jednostki: Piechota: 0, Łucznicy: 0");
                    pokonany = true;
                } else {
                    unitsLabels.get(rasa.getId()).setText("Jednostki: Piechota: " + rasa.getPiechota() + ", Łucznicy: " + rasa.getLucznicy());
                }
            }
            if (strengthLabels.containsKey(rasa.getId())) {
                if (pokonany==true){strengthLabels.get(rasa.getId()).setText("Siła: 0");}
                else strengthLabels.get(rasa.getId()).setText("Siła: " + (int) Math.round(rasa.silaZBonusem())); // Zmiana tutaj: zaokrąglenie siły

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

    private void zapiszZwyciezceDoCSV(RasaBase zwyciezca, int zajeteKratki) {
        String fileName = "src/main/resources/zwyciezcy.csv";
        String linia = String.format("\"%s\",%d,%d,%d,%d,%d\n",
                zwyciezca.getNazwa(),
                zwyciezca.getId(),
                (int) zwyciezca.silaZBonusem(),
                zajeteKratki,
                zwyciezca.getPiechota(),
                zwyciezca.getLucznicy());
        try (FileWriter fw = new FileWriter(fileName, true)) { // true = dopisz do pliku
            fw.write(linia);
        } catch (IOException e) {
            System.err.println("Błąd zapisu do pliku CSV: " + e.getMessage());
        }
    }

    private static class RankingResult {
        String nazwaRasy;
        int idRasy;
        double sredniaSila;
        double srednieKratki;
        double sredniaPiechota;
        double sredniLucznicy;
        int liczbaWygranych;

        String toCsvLine() {
            return String.format("\"%s\";%d;%d;%d;%d;%d;%d\n",
                    nazwaRasy, idRasy,
                    (int) Math.round(sredniaSila),
                    (int) Math.round(srednieKratki),
                    (int) Math.round(sredniaPiechota),
                    (int) Math.round(sredniLucznicy),
                    liczbaWygranych);
        }
    }

    private RankingResult obliczRanking(String fileName) throws IOException {
        Map<Integer, List<double[]>> stats = new HashMap<>();
        Map<Integer, String> nazwy = new HashMap<>();
        Map<Integer, Integer> count = new HashMap<>();

        try (Scanner sc = new Scanner(new java.io.File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 6) continue;
                String nazwa = parts[0].replaceAll("\"", "");
                int id = Integer.parseInt(parts[1]);
                double sila = Double.parseDouble(parts[2]);
                double kratki = Double.parseDouble(parts[3]);
                double piechota = Double.parseDouble(parts[4]);
                double lucznicy = Double.parseDouble(parts[5]);
                stats.computeIfAbsent(id, k -> new ArrayList<>()).add(new double[]{sila, kratki, piechota, lucznicy});
                nazwy.put(id, nazwa);
                count.put(id, count.getOrDefault(id, 0) + 1);
            }
        }
        if (stats.isEmpty()) return null;
        // Znajdź najczęściej wygrywające id
        int bestId = count.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        List<double[]> lista = stats.get(bestId);
        double sumaSila = 0, sumaKratki = 0, sumaPiechota = 0, sumaLucznicy = 0;
        for (double[] arr : lista) {
            sumaSila += arr[0];
            sumaKratki += arr[1];
            sumaPiechota += arr[2];
            sumaLucznicy += arr[3];
        }
        int n = lista.size();
        RankingResult result = new RankingResult();
        result.nazwaRasy = nazwy.get(bestId);
        result.idRasy = bestId;
        result.sredniaSila = sumaSila / n;
        result.srednieKratki = sumaKratki / n;
        result.sredniaPiechota = sumaPiechota / n;
        result.sredniLucznicy = sumaLucznicy / n;
        result.liczbaWygranych = n;
        return result;
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

    private void addTerrainImageBelowStats(VBox statsPanel) {
        ImageView terenImageView = new ImageView();
        try {
            Image terenImage = new Image("teren.jpg", 60, 60, true, true);
            terenImageView.setImage(terenImage);
        } catch (Exception e) {
            System.err.println("Nie udało się załadować obrazka terenu: teren.jpg");
        }
        HBox przeszkodyHBox = new HBox(10, terenImageView, przeszkodyLabel); // obrazek po lewej, licznik po prawej
        przeszkodyHBox.setAlignment(Pos.CENTER_LEFT);
        statsPanel.getChildren().add(przeszkodyHBox);
    }
}