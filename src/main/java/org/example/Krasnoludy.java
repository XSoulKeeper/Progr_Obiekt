package org.example;
public class Krasnoludy extends RasaBase {
    public Krasnoludy(int id, int piechota, int lucznicy, int produkcja) {
        super("Krasnoludy", id, piechota, lucznicy, produkcja);
    }

    @Override
    public int sila() {
        return piechota * piechota * lucznicy;
    }
}
