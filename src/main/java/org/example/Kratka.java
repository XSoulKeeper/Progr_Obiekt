package org.example;


/**
 * Klasa reprezentująca pojedynczą kratkę na planszy symulacji.
 * Przechowuje informacje o położeniu, właścicielu i typie kratki.
 */
publi
public class Kratka {
    private int x, y;                    // Współrzędne kratki na planszy
    private int ownerRasaId = -1;        // ID rasy właściciela (-1 oznacza brak właściciela)
    private boolean przeszkoda = false;  // Flaga określająca czy kratka jest przeszkodą

    /**
     * Konstruktor tworzący nową kratkę.
     *
     * @param x Współrzędna X kratki
     * @param y Współrzędna Y kratki
     */
    public Kratka(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return ID rasy będącej właścicielem kratki (-1 jeśli brak właściciela)
     */
    public int getOwnerRasaId() {
        return ownerRasaId;
    }

    /**
     * Ustawia właściciela kratki.
     *
     * @param id ID rasy która ma zostać właścicielem
     */
    public void setOwnerRasaId(int id) {
        this.ownerRasaId = id;
    }

    /**
     * Sprawdza czy kratka jest przeszkodą.
     *
     * @return True jeśli kratka jest przeszkodą, False w przeciwnym wypadku
     */
    public boolean isPrzeszkoda() {
        return przeszkoda;
    }


    /**
     * @return Współrzędne X i Y kratki
     */
    public int getX() { return x; }
    public int getY() { return y; }

    /**
     * Ustawia czy kratka jest przeszkodą.
     *
     * @param przeszkoda True aby ustawić jako przeszkodę, False aby usunąć przeszkodę
     */
    public void setPrzeszkoda(boolean przeszkoda){
        this.przeszkoda = przeszkoda;

    }
}
