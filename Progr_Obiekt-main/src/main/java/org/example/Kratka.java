package org.example;

public class Kratka {
    private int x, y;
    private int ownerRasaId = -1;
    private boolean przeszkoda = false;

    public Kratka(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getOwnerRasaId() {
        return ownerRasaId;
    }

    public void setOwnerRasaId(int id) {
        this.ownerRasaId = id;
    }

    public boolean isPrzeszkoda() {
        return przeszkoda;
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPrzeszkoda(boolean przeszkoda){
        this.przeszkoda = przeszkoda;

    }
}
