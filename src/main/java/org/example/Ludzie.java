package org.example;

// Klasa Ludzie dziedziczy po klasie bazowej RasaBase i reprezentuje rasę ludzi
public class Ludzie extends RasaBase {
    public Ludzie(int id, int piechota, int lucznicy, int produkcja) {
        super("Ludzie", id, piechota, lucznicy, produkcja);
    }

    // Przesłonięta metoda obliczająca siłę rasy ludzi
    @Override
    public int sila() {
        return piechota * lucznicy;
    }
}
