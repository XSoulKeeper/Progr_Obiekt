package org.example;

// Klasa Krasnoludy dziedziczy po klasie bazowej RasaBase i reprezentuje rasę krasnoludów
public class Krasnoludy extends RasaBase {
    public Krasnoludy(int id, int piechota, int lucznicy, int produkcja) {
        super("Krasnoludy", id, piechota, lucznicy, produkcja);
    }

    // Przesłonięta metoda obliczająca siłę rasy krasnoludów
    @Override
    public int sila() {
        return piechota * piechota * lucznicy;
    }
}
