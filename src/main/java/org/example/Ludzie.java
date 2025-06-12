package org.example;

// Klasa Ludzie dziedziczy po klasie bazowej RasaBase i reprezentuje rasę ludzi
public class Ludzie extends RasaBase {
    public Ludzie(int id, int piechota, int lucznicy, int produkcja) {
        super("Ludzie", id, piechota, lucznicy, produkcja);
    }

    @Override
    public double sila() { // Bazowa siła
        return (2*piechota)*(lucznicy); // Twoja oryginalna definicja siły
    }

    @Override
    public double silaZBonusem() {
        // Losowa liczba od 1 do 3
        double buff = 1, debuff = 1;
        double bonus = GeneratorLiczb.generate(100, 300)/100;

        if (Symulacja.getModyfikator().isSwiatlo()) {buff *= 1.2;}
        else if (Symulacja.getModyfikator().isMrok()) {debuff *= 0.7;}
        else if (Symulacja.getModyfikator().isZlota()) {buff *= 1.5;}
        else if (Symulacja.getModyfikator().isKrol()) {buff *= 2;}
        else if (Symulacja.getModyfikator().isSmok()) {debuff *= 0.6;}

        return sila() * bonus * buff * debuff;
    }
}