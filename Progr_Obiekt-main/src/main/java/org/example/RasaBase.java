package org.example;
public abstract class RasaBase {
    protected String nazwa;
    protected int id;
    protected int piechota;
    protected int lucznicy;
    protected int produkcja;

    public RasaBase(String nazwa, int id, int piechota, int lucznicy, int produkcja) {
        this.nazwa = nazwa;
        this.id = id;
        this.piechota = piechota;
        this.lucznicy = lucznicy;
        this.produkcja = produkcja;
    }

    public abstract int sila();

    public int getId() { return id; }
    public String getNazwa() { return nazwa; }
    public int getPiechota() { return piechota; }
    public int getLucznicy() { return lucznicy; }

    public void dodajJednostki(int piech, int lucz) {
        this.piechota += piech;
        this.lucznicy += lucz;
    }

    public void setJednostki(int piech, int lucz) {
        this.piechota = piech;
        this.lucznicy = lucz;
    }

    public int getProdukcja() { return produkcja; }
}
