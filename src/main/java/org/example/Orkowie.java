package org.example;

// Klasa Orkowie dziedziczy po klasie bazowej RasaBase i reprezentuje rasę orków
public class Orkowie extends RasaBase {
    public Orkowie(int id, int piechota, int lucznicy, int produkcja) {
        super("Orkowie", id, piechota, lucznicy, produkcja);
    }

    // Przesłonięta metoda obliczająca siłę rasy orków
    @Override
    public int sila() {
        return (piechota - lucznicy) * (piechota - lucznicy);
    }
}
