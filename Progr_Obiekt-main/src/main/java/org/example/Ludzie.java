package org.example;
public class Ludzie extends RasaBase {
    public Ludzie(int id, int piechota, int lucznicy, int produkcja) {
        super("Ludzie", id, piechota, lucznicy, produkcja);
    }

    @Override
    public int sila() {
        return piechota * lucznicy;
    }
}
