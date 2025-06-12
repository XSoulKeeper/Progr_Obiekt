package org.example;

/**
 * Abstrakcyjna klasa bazowa reprezentująca rasę w symulacji bitwy.
 * Zawiera podstawowe atrybuty i metody wspólne dla wszystkich ras.
 */
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

    // Zmieniamy to na konkretną metodę, która będzie obliczać bazową siłę
    public double sila() {
        // Domyślna implementacja siły, którą można nadpisać w poszczególnych klasach ras,
        // jeśli ich podstawowa siła jest inna niż standardowa suma jednostek.
        // Jednak teraz nie będzie abstrakcyjna i będzie reprezentować "czystą" siłę.
        return piechota + lucznicy; // Przykładowa bazowa siła
    }

    // Nowa abstrakcyjna metoda do obliczania siły z bonusem losowym
    public abstract double silaZBonusem();

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