

package org.example;
import java.util.*;

public class Symulacja {
    private Kratka[][] mapa;
    private int rozmiar;
    private int aktualnaTura;
    private List<RasaBase> rasy;
    private Random random = new Random();

    public Symulacja(int rozmiar, int aktualnaTura, List<RasaBase> rasy, int procentPrzeszkod) {
        this.rozmiar = rozmiar;
        this.aktualnaTura = aktualnaTura;
        this.rasy = rasy;
        this.mapa = new Kratka[rozmiar][rozmiar];
        Random random = new Random();

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (random.nextInt(100) < procentPrzeszkod) {
                    mapa[i][j] = new Przeszkoda(i, j);
                } else {
                    mapa[i][j] = new Kratka(i, j);
                }
            }
        }
    }

    public void inicjalizuj() {
        if (rasy.size() > 0) {
            int[] start1 = znajdzWolnaKratke(0, 0);
            mapa[start1[0]][start1[1]].setOwnerRasaId(rasy.get(0).getId());
        }
        if (rasy.size() > 1) {
            int[] start2 = znajdzWolnaKratke(0, rozmiar - 1);
            mapa[start2[0]][start2[1]].setOwnerRasaId(rasy.get(1).getId());
        }
        if (rasy.size() > 2) {
            int[] start3 = znajdzWolnaKratke(rozmiar - 1, 0);
            mapa[start3[0]][start3[1]].setOwnerRasaId(rasy.get(2).getId());
        }
        if (rasy.size() > 3) {
            int[] start4 = znajdzWolnaKratke(rozmiar - 1, rozmiar - 1);
            mapa[start4[0]][start4[1]].setOwnerRasaId(rasy.get(3).getId());
        }
    }

    private int[] znajdzWolnaKratke(int x, int y) {
        Queue<int[]> kolejka = new LinkedList<>();
        boolean[][] odwiedzone = new boolean[rozmiar][rozmiar];
        kolejka.add(new int[]{x, y});
        odwiedzone[x][y] = true;

        while (!kolejka.isEmpty()) {
            int[] aktualna = kolejka.poll();
            int cx = aktualna[0];
            int cy = aktualna[1];

            if (!(mapa[cx][cy] instanceof Przeszkoda)) {
                return new int[]{cx, cy};
            }

            int[][] kierunki = {{-1,0}, {1,0}, {0,-1}, {0,1}};
            for (int[] d : kierunki) {
                int nx = cx + d[0];
                int ny = cy + d[1];
                if (nx >= 0 && nx < rozmiar && ny >= 0 && ny < rozmiar && !odwiedzone[nx][ny]) {
                    odwiedzone[nx][ny] = true;
                    kolejka.add(new int[]{nx, ny});
                }
            }
        }

        return new int[]{x, y};
    }
    public int policzPrzeszkody(){
        int licznik = 0;
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (mapa[i][j] instanceof Przeszkoda) {
                    licznik++;
                }
            }
        }
        return licznik;
    }
    public void tura() {
        aktualnaTura++;

        for (RasaBase rasa : rasy) {
            rasa.dodajJednostki(rasa.getProdukcja(), rasa.getProdukcja());
        }

        Map<Integer, Integer> posiadaneKratkiNaStartTury = new HashMap<>();
        for (RasaBase rasa : rasy) {
            posiadaneKratkiNaStartTury.put(rasa.getId(), 0);
        }

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                int ownerId = mapa[i][j].getOwnerRasaId();
                if (ownerId != -1) {
                    posiadaneKratkiNaStartTury.put(ownerId, posiadaneKratkiNaStartTury.get(ownerId) + 1);
                }
            }
        }

        Map<Integer, Integer> nowoPrzejeteKratki = new HashMap<>();
        for (RasaBase rasa : rasy) {
            nowoPrzejeteKratki.put(rasa.getId(), 0);
        }

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
        if (!czyRasySaPolaczone()) {
            return true;
        }
        for (RasaBase rasa : rasy) {
            if (mozeSieRuszyc(rasa)) {
                return false;
            }
        }
        return true;
    }

    private boolean czyRasySaPolaczone() {
        Set<Integer> znalezioneRasy = new HashSet<>();
        boolean[][] odwiedzone = new boolean[rozmiar][rozmiar];

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (!odwiedzone[i][j] && !(mapa[i][j] instanceof Przeszkoda)) {
                    Set<Integer> rasyWTerytorium = new HashSet<>();
                    Queue<int[]> kolejka = new LinkedList<>();
                    kolejka.add(new int[]{i, j});
                    odwiedzone[i][j] = true;

                    while (!kolejka.isEmpty()) {
                        int[] pos = kolejka.poll();
                        int x = pos[0], y = pos[1];

                        int owner = mapa[x][y].getOwnerRasaId();
                        if (owner != -1) {
                            rasyWTerytorium.add(owner);
                        }

                        for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                            int nx = x + d[0], ny = y + d[1];
                            if (nx >= 0 && ny >= 0 && nx < rozmiar && ny < rozmiar &&
                                    !odwiedzone[nx][ny] && !(mapa[nx][ny] instanceof Przeszkoda)) {
                                odwiedzone[nx][ny] = true;
                                kolejka.add(new int[]{nx, ny});
                            }
                        }
                    }

                    if (rasyWTerytorium.size() > 1) {
                        return true; // te rasy mogą się spotkać
                    }

                    znalezioneRasy.addAll(rasyWTerytorium);
                }
            }
        }

        return false; // żadna grupa nie zawiera więcej niż jednej rasy
    }

    private void bfs(Kratka start, boolean[][] odwiedzone, Set<Integer> osiagnieteRasy) {
        Queue<Kratka> kolejka = new LinkedList<>();
        kolejka.add(start);
        odwiedzone[start.getX()][start.getY()] = true;
        osiagnieteRasy.add(start.getOwnerRasaId());

        while (!kolejka.isEmpty()) {
            Kratka curr = kolejka.poll();

            for (Kratka sasiad : znajdzSasiednie(curr.getX(), curr.getY())) {
                int x = sasiad.getX();
                int y = sasiad.getY();
                if (!odwiedzone[x][y] && !(sasiad instanceof Przeszkoda)) {
                    odwiedzone[x][y] = true;
                    kolejka.add(sasiad);
                    if (sasiad.getOwnerRasaId() != -1) {
                        osiagnieteRasy.add(sasiad.getOwnerRasaId());
                    }
                }
            }
        }
    }

    private boolean mozeSieRuszyc(RasaBase rasa) {
        int rasaId = rasa.getId();

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (mapa[i][j].getOwnerRasaId() == rasaId) {
                    List<Kratka> sasiednie = znajdzSasiednie(i, j);
                    for (Kratka sasiad : sasiednie) {
                        if (sasiad.getOwnerRasaId() != rasaId) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Set<Integer> getZwyciezcy() {
        Set<Integer> zwyciezcy = new HashSet<>();
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                int owner = mapa[i][j].getOwnerRasaId();
                if (owner != -1) {
                    zwyciezcy.add(owner);
                }
            }
        }
        return zwyciezcy;
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
