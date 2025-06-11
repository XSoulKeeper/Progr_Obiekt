package org.example;
public class Orkowie extends RasaBase {
    public Orkowie(int id, int piechota, int lucznicy, int produkcja) {
        super("Orkowie", id, piechota, lucznicy, produkcja);
    }

    @Override
    public int sila() {
        return (piechota - lucznicy) * (piechota - lucznicy);
    }
}
