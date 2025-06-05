package org.example;

public class Kratka {
    private int x, y;
    private int ownerRasaId = -1;

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

    public int getX() { return x; }
    public int getY() { return y; }
}
