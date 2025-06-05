package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SymulacjaUI extends Application {

    private GridPane grid = new GridPane();
    private Symulacja symulacja;
    private int rozmiar;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        TextField liczbaRasField = new TextField();
        TextField jednostkiField = new TextField();
        TextField rozmiarField = new TextField();
        Button startButton = new Button("Start");

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
                    if (symulacja.czyKoniec()) timer.cancel();
                }
            }, 1000, 1000);
        });

        HBox controls = new HBox(10, liczbaRasField, jednostkiField, rozmiarField, startButton);
        root.getChildren().addAll(controls, grid);

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Symulacja Ras");
        primaryStage.show();
    }

    private void drawMap() {
        grid.getChildren().clear();
        Kratka[][] mapa = symulacja.getMapa();
        Color[] kolory = {Color.GREEN, Color.GRAY, Color.RED, Color.BLUE};

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                Rectangle rect = new Rectangle(20, 20);
                int id = mapa[i][j].getOwnerRasaId();
                rect.setFill(id >= 0 && id < kolory.length ? kolory[id] : Color.WHITE);
                grid.add(rect, j, i);
            }
        }
    }
}
