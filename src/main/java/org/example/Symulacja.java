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
        if (rasy.size() > 0) {
            mapa[0][0].setOwnerRasaId(rasy.get(0).getId());
        }
        if (rasy.size() > 1) {
            mapa[0][rozmiar - 1].setOwnerRasaId(rasy.get(1).getId());
        }
        if (rasy.size() > 2) {
            mapa[rozmiar - 1][0].setOwnerRasaId(rasy.get(2).getId());
        }
        if (rasy.size() > 3) {
            mapa[rozmiar - 1][rozmiar - 1].setOwnerRasaId(rasy.get(3).getId());
        }
    }

    public void tura() {
        aktualnaTura++;

        // Produkcja jednostek dla każdej rasy
        for (RasaBase rasa : rasy) {
            rasa.dodajJednostki(rasa.getProdukcja(), rasa.getProdukcja());
        }

        // Krok 1: Zliczanie kratek posiadanych przez każdą rasę na początku tury
        Map<Integer, Integer> posiadaneKratkiNaStartTury = new HashMap<>();
        for (RasaBase rasa : rasy) {
            posiadaneKratkiNaStartTury.put(rasa.getId(), 0); // Inicjalizacja licznika dla każdej rasy
        }

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                int ownerId = mapa[i][j].getOwnerRasaId();
                if (ownerId != -1) {
                    posiadaneKratkiNaStartTury.put(ownerId, posiadaneKratkiNaStartTury.get(ownerId) + 1);
                }
            }
        }

        // Krok 2: Śledzenie nowych kratek przejętych w bieżącej turze
        Map<Integer, Integer> nowoPrzejeteKratki = new HashMap<>();
        for (RasaBase rasa : rasy) {
            nowoPrzejeteKratki.put(rasa.getId(), 0); // Inicjalizacja licznika
        }

        // Próby przejęcia sąsiednich kratek
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                Kratka kratka = mapa[i][j];
                int owner = kratka.getOwnerRasaId();

                if (owner != -1) {
                    RasaBase rasa = znajdzRase(owner);
                    List<Kratka> sasiednie = znajdzSasiednie(i, j);

                    for (Kratka sasiad : sasiednie) {
                        // Sprawdzamy, czy sasiad należy do innej rasy
                        if (sasiad.getOwnerRasaId() != owner) {
                            RasaBase obca = znajdzRase(sasiad.getOwnerRasaId());
                            // Warunek do przejęcia
                            if (obca == null || rasa.sila() > obca.sila()) {
                                // Krok 3: Ograniczenie przejmowania nowych kratek
                                int limitPrzejec = posiadaneKratkiNaStartTury.get(owner) * 2;
                                if (nowoPrzejeteKratki.get(owner) < limitPrzejec) {
                                    sasiad.setOwnerRasaId(owner);
                                    nowoPrzejeteKratki.put(owner, nowoPrzejeteKratki.get(owner) + 1);
                                }
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