package org.example;
import java.util.*;

public class Symulacja {
    private Kratka[][] mapa;
    private int rozmiar;
    private int aktualnaTura;
    private List<RasaBase> rasy;
    private Random random = new Random();

    public Symulacja(int rozmiar, int aktualnaTura, List<RasaBase> rasy) {
        this.rozmiar = rozmiar;
        this.aktualnaTura = aktualnaTura;
        this.rasy = rasy;
        this.mapa = new Kratka[rozmiar][rozmiar];
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                mapa[i][j] = new Kratka(i, j);
            }
        }
    }

    public void inicjalizuj() {
        // Rozdziel pierwsze kratki równo między rasy
        int startX = 0;
        int startY = 0;
        for (RasaBase rasa : rasy) {
            mapa[startX][startY].setOwnerRasaId(rasa.getId());
            startX++;
            startY++;
        }
    }

    public void tura() {
        aktualnaTura++;

        // Produkcja jednostek dla każdej rasy
        for (RasaBase rasa : rasy) {
            rasa.dodajJednostki(rasa.getProdukcja(), rasa.getProdukcja());
        }

        // Próby przejęcia sąsiednich kratek
        List<Kratka> doPrzejecia = new ArrayList<>();

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                Kratka kratka = mapa[i][j];
                int owner = kratka.getOwnerRasaId();

                if (owner != -1) {
                    RasaBase rasa = znajdzRase(owner);
                    List<Kratka> sasiednie = znajdzSasiednie(i, j);

                    for (Kratka sasiad : sasiednie) {
                        if (sasiad.getOwnerRasaId() != owner) {
                            RasaBase obca = znajdzRase(sasiad.getOwnerRasaId());
                            if (obca == null || rasa.sila() > obca.sila()) {
                                sasiad.setOwnerRasaId(owner);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean czyKoniec() {
        Set<Integer> unikalneRasy = new HashSet<>();
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (mapa[i][j].getOwnerRasaId() != -1) {
                    unikalneRasy.add(mapa[i][j].getOwnerRasaId());
                }
            }
        }
        return unikalneRasy.size() <= 1;
    }

    private RasaBase znajdzRase(int id) {
        for (RasaBase rasa : rasy) {
            if (rasa.getId() == id) return rasa;
        }
        return null;
    }

    private List<Kratka> znajdzSasiednie(int x, int y) {
        List<Kratka> lista = new ArrayList<>();
        int[][] kierunki = {{-1,0}, {1,0}, {0,-1}, {0,1}};

        for (int[] d : kierunki) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx >= 0 && nx < rozmiar && ny >= 0 && ny < rozmiar) {
                lista.add(mapa[nx][ny]);
            }
        }
        return lista;
    }

    public Kratka[][] getMapa() {
        return mapa;
    }
}
