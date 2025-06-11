package org.example;

/**
 * Abstrakcyjna klasa bazowa reprezentująca rasę w symulacji.
 * Definiuje podstawowe właściwości i zachowania wspólne dla wszystkich ras.
 */
public abstract class RasaBase {
    protected String nazwa;      // Nazwa rasy
    protected int id;            // Unikalny identyfikator rasy
    protected int piechota;      // Liczba jednostek piechoty
    protected int lucznicy;      // Liczba jednostek łuczników
    protected int produkcja;     // Liczba nowych jednostek produkowanych w każdej turze


    /**
     * Konstruktor inicjalizujący podstawowe parametry rasy.
     *
     * @param nazwa     Nazwa rasy
     * @param id        Unikalny identyfikator
     * @param piechota  Początkowa liczba piechoty
     * @param lucznicy  Początkowa liczba łuczników
     * @param produkcja Liczba jednostek produkowanych w turze
     */
    public RasaBase(String nazwa, int id, int piechota, int lucznicy, int produkcja) {
        this.nazwa = nazwa;
        this.id = id;
        this.piechota = piechota;
        this.lucznicy = lucznicy;
        this.produkcja = produkcja;
    }

    /**
     * Abstrakcyjna metoda obliczająca siłę rasy.
     * Każda konkretna rasa musi dostarczyć swoją implementację.
     *
     * @return Wartość siły rasy
     */
    public abstract int sila();

    // Poniżej podstawowe metody dostępowe (gettery) i modyfikujące (settery)
    public int getId() { return id; }
    public String getNazwa() { return nazwa; }
    public int getPiechota() { return piechota; }
    public int getLucznicy() { return lucznicy; }

    /**
     * Dodaje jednostki do stanu rasy.
     *
     * @param piech Liczba piechoty do dodania
     * @param lucz  Liczba łuczników do dodania
     */
    public void dodajJednostki(int piech, int lucz) {
        this.piechota += piech;
        this.lucznicy += lucz;
    }

    /**
     * Ustawia nowe wartości dla jednostek rasy.
     *
     * @param piech Nowa liczba piechoty
     * @param lucz  Nowa liczba łuczników
     */
    public void setJednostki(int piech, int lucz) {
        this.piechota = piech;
        this.lucznicy = lucz;
    }

    /**
     * @return Liczba jednostek produkowanych w każdej turze
     */
    public int getProdukcja() { return produkcja; }
}
