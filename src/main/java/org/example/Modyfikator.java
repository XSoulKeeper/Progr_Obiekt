package org.example;
/**
 * Klasa reprezentująca modyfikatory wpływające na przebieg bitwy między rasami.
 * Modyfikatory są aktywowane losowo na podstawie wygenerowanej liczby procentowej.
 */
public class Modyfikator {
    // Flagi poszczególnych modyfikatorów
    private boolean swiatlo;
    private boolean mrok;
    private boolean zloto;
    private boolean krol;
    private boolean smok;

    /**
     * Aktualizuje aktywny modyfikator na podstawie losowej liczby.
     * Resetuje wszystkie modyfikatory, a następnie aktywuje jeden z nich
     * w zależności od podzielności wygenerowanej liczby.
     */
    public void updateModyfikator() {

        this.swiatlo = false;
        this.mrok = false;
        this.zloto = false;
        this.krol = false;
        this.smok = false;

        int procent = (int)GeneratorLiczb.generate(0,100);

        if (procent % 2 == 0) swiatlo=true;
        else if (procent % 3 == 0) mrok=true;
        else if (procent % 5 == 0) zloto=true;
        else if (procent % 7 == 0) krol=true;
        else if (procent % 11 == 0 || procent % 13 ==0) smok=true;
    }
    /**
     * Zwraca nazwę aktualnie aktywnego modyfikatora jako String.
     * @return Nazwa aktywnego modyfikatora lub "Brak" jeśli żaden nie jest aktywny
     */
    public String aktywnyModyfikator() {
        if (swiatlo) return "Światło";
        if (mrok) return "Mrok";
        if (zloto) return "Złoto";
        if (krol) return "Król";
        if (smok) return "Smok";
        return "Brak";
    }

    public boolean isSwiatlo() { return swiatlo; }
    public boolean isMrok() { return mrok; }
    public boolean isZlota() { return zloto; }
    public boolean isKrol() { return krol; }
    public boolean isSmok() { return smok; }

}
