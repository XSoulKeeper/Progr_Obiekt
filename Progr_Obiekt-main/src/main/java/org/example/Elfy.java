package org.example;

public class Elfy extends RasaBase {
    public Elfy(int id, int piechota, int lucznicy, int produkcja) {
        super("Elfy", id, piechota, lucznicy, produkcja);
    }

    @Override
    public int sila() {
        return piechota * lucznicy * lucznicy;
    }
}
