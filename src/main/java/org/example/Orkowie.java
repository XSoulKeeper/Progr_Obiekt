package org.example;

public class Orkowie extends RasaBase {
    public Orkowie(int id, int piechota, int lucznicy, int produkcja) {
        super("Orkowie", id, piechota, lucznicy, produkcja);
    }

    @Override
    public double sila() { // Bazowa siła
        return (3*piechota) * lucznicy; // Twoja oryginalna definicja siły
    }

    @Override
    public double silaZBonusem() {
        // Losowa liczba od 1 do 3
        double buff = 1, debuff = 1;
        double bonus = GeneratorLiczb.generate(100, 300)/100;

        if (Symulacja.getModyfikator().isSwiatlo()) {debuff *= 0.3;}
        else if (Symulacja.getModyfikator().isMrok()) {buff *= 3.5;}
        else if (Symulacja.getModyfikator().isZlota()) {buff *= 0.8;}
        else if (Symulacja.getModyfikator().isKrol()) {debuff *= 0.9;}
        else if (Symulacja.getModyfikator().isSmok()) {buff *= 2;}

        return sila() * bonus * buff * debuff;
    }
}