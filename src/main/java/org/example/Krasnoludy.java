package org.example;

public class Krasnoludy extends RasaBase {
    public Krasnoludy(int id, int piechota, int lucznicy, int produkcja) {
        super("Krasnoludy", id, piechota, lucznicy, produkcja);
    }

    @Override
    public double sila() { // Bazowa siła
        return (1.2*piechota) * lucznicy; // Twoja oryginalna definicja siły
    }

    @Override
    public double silaZBonusem() {
        // Losowa liczba od 1 do 3
        double buff = 1, debuff = 1;
        double bonus = GeneratorLiczb.generate(100, 300)/100;

        if (Symulacja.getModyfikator().isSwiatlo()) {buff *= 0.9;}
        else if (Symulacja.getModyfikator().isMrok()) {debuff *= 1.3;}
        else if (Symulacja.getModyfikator().isZlota()) {buff *= 2.2;}
        else if (Symulacja.getModyfikator().isKrol()) {buff *= 1;}
        else if (Symulacja.getModyfikator().isSmok()) {debuff *= 1.5;}

        return sila() * bonus * buff * debuff;
    }
}