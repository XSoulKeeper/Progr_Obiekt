package org.example;

// Klasa Elfy dziedziczy po klasie bazowej RasaBase i reprezentuje rasę elfów
public class Elfy extends RasaBase {
    public Elfy(int id, int piechota, int lucznicy, int produkcja) {
        super("Elfy", id, piechota, lucznicy, produkcja);
    }

    // Przesłonięta metoda obliczająca siłę rasy Elfów
    @Override
    public double sila() { // To jest teraz bazowa siła, bez modyfikatorów losowych
        return (piechota*0.3) * (lucznicy*2.1); // Twoja oryginalna definicja siły
    }

    @Override
    public double silaZBonusem() {
        // Losowa liczba od 1 do 3
        double buff = 1, debuff = 1;
        double bonus = GeneratorLiczb.generate(100, 300)/100;

        if (Symulacja.getModyfikator().isSwiatlo()) {buff *= 1.6;}
        else if (Symulacja.getModyfikator().isMrok()) {debuff *= 0.8;}
        else if (Symulacja.getModyfikator().isZlota()) {buff *= 1.3;}
        else if (Symulacja.getModyfikator().isKrol()) {buff *= 1.1;}
        else if (Symulacja.getModyfikator().isSmok()) {debuff *= 0.7;}

        return sila() * bonus * buff * debuff;
    }
}