package org.example;

// Klasa Elfy dziedziczy po klasie bazowej RasaBase i reprezentuje rasę elfów
public class Elfy extends RasaBase {
    public Elfy(int id, int piechota, int lucznicy, int produkcja) {
        super("Elfy", id, piechota, lucznicy, produkcja);
    }

    // Przesłonięta metoda obliczająca siłę rasy Elfów
    @Override
    public int sila() {
        return piechota * lucznicy * lucznicy;
    }
}
