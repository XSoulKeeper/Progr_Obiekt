package org.example;

// Klasa Przeszkoda dziedziczy po klasie Kratka i reprezentuje niezdobywalną przeszkodę na planszy
public class Przeszkoda extends Kratka {
    public Przeszkoda(int x, int y) {
        super(x, y);


        setPrzeszkoda(true);
    }
    @Override
    public void setOwnerRasaId(int id) {
        // Blokujemy zmianę właściciela, bo przeszkoda jest niezdobywalna
    }
}