package org.example;
import java.util.*;
/**
 * Główna klasa odpowiedzialna za przeprowadzenie symulacji bitwy między rasami.
 * Zarządza mapą, rasami, przebiegiem tur i logiką gry.
 */
public class Symulacja {
    private Kratka[][] mapa;
    private int rozmiar;
    private int aktualnaTura;
    List<RasaBase> rasy;
    private Random random = new Random();
    public static Modyfikator modyfikator = new Modyfikator();
    private String ostatniLog = "";
    public String getOstatniLog() { return ostatniLog; }

    public Symulacja(int rozmiar, int aktualnaTura, List<RasaBase> rasy, int procentPrzeszkod) {
        this.rozmiar = rozmiar;
        this.aktualnaTura = aktualnaTura;
        this.rasy = rasy;
        this.mapa = new Kratka[rozmiar][rozmiar];
        Random random = new Random();

        // Inicjalizacja mapy - tworzenie przeszkód i pustych pól
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (random.nextInt(100) < procentPrzeszkod) {
                    mapa[i][j] = new Przeszkoda(i, j);  // Tworzenie przeszkody
                } else {
                    mapa[i][j] = new Kratka(i, j);      // Tworzenie pustej kratki
                }
            }
        }
    }

    /**
     * Natychmiastowo wyłania zwycięzcę na podstawie aktualnej siły ras
     * @return lista zawierająca najsilniejszą rasę (lub pustą listę jeśli brak ras)
     */
        public List<RasaBase> wylonZwyciezceTeraz() {
                // Znajdź rasę o największej sile (bazowej)
                RasaBase najsilniejsza = null;
                double maxSila = Double.NEGATIVE_INFINITY;
                for (RasaBase rasa : rasy) {
                    double sila = rasa.silaZBonusem();
                    if (sila > maxSila) {
                        maxSila = sila;
                        najsilniejsza = rasa;
                    }
                }
                if (najsilniejsza == null) return Collections.emptyList();

                // Ustaw właściciela wszystkich pól (nie-przeszkód) na najsilniejszą rasę
                for (int i = 0; i < rozmiar; i++) {
                    for (int j = 0; j < rozmiar; j++) {
                        if (!mapa[i][j].isPrzeszkoda()) {
                            mapa[i][j].setOwnerRasaId(najsilniejsza.getId());
                        }
                    }
                }
                // Zwróć zwycięzcę jako jednoelementową listę
                return Collections.singletonList(najsilniejsza);
            }

    /**
     * Inicjalizuje pozycje startowe dla wszystkich ras na mapie
     */
    public void inicjalizuj() {
        int[][] startPositions = {
                {0, 0},
                {rozmiar - 1, rozmiar - 1},
                {0, rozmiar - 1},
                {rozmiar - 1, 0}
        };

        for (int i = 0; i < rasy.size(); i++) {
            RasaBase rasa = rasy.get(i);
            int startX = startPositions[i][0];
            int startY = startPositions[i][1];

            int[] actualStartCoords = znajdzWolnaKratkeWokol(startX, startY, 3);
            if (actualStartCoords[0] != -1 && actualStartCoords[1] != -1) {
                mapa[actualStartCoords[0]][actualStartCoords[1]].setOwnerRasaId(rasa.getId());
                rasa.dodajJednostki(rasa.getProdukcja(), rasa.getProdukcja());
            } else {
                int[] fallbackCoords = znajdzWolnaKratkeGlobalnie();
                if (fallbackCoords[0] != -1 && fallbackCoords[1] != -1) {
                    mapa[fallbackCoords[0]][fallbackCoords[1]].setOwnerRasaId(rasa.getId());
                    rasa.dodajJednostki(rasa.getProdukcja(), rasa.getProdukcja());
                } else {
                    System.err.println("Błąd: Nie można znaleźć miejsca startowego dla rasy " + rasa.getNazwa());
                }
            }
        }
    }

    /**
     * Znajduje wolną kratkę wokół podanych współrzędnych w określonym promieniu
     * @param centerX - współrzędna X środka obszaru wyszukiwania
     * @param centerY - współrzędna Y środka obszaru wyszukiwania
     * @param radius - promień wyszukiwania
     * @return tablica [x, y] ze współrzędnymi wolnej kratki lub [-1, -1] jeśli nie znaleziono
     */
    private int[] znajdzWolnaKratkeWokol(int centerX, int centerY, int radius) {
        // Przeszukiwanie koncentrycznych okręgów wokół środka
        for (int r = 0; r <= radius; r++) {
            for (int i = centerX - r; i <= centerX + r; i++) {
                for (int j = centerY - r; j <= centerY + r; j++) {
                    // Sprawdzenie czy współrzędne są w granicach mapy
                    if (i >= 0 && i < rozmiar && j >= 0 && j < rozmiar) {
                        // Sprawdzenie czy kratka nie jest przeszkodą i nie ma właściciela
                        if (!mapa[i][j].isPrzeszkoda() && mapa[i][j].getOwnerRasaId() == -1) {
                            return new int[]{i, j};
                        }
                    }
                }
            }
        }
        return new int[]{-1, -1}; // Zwróć nieprawidłowe współrzędne jeśli nie znaleziono
    }
    /**
     * Przeszukuje całą mapę w poszukiwaniu wolnej kratki
     * @return tablica [x, y] ze współrzędnymi wolnej kratki lub [-1, -1] jeśli nie znaleziono
     */
    private int[] znajdzWolnaKratkeGlobalnie() {
        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (!mapa[i][j].isPrzeszkoda() && mapa[i][j].getOwnerRasaId() == -1) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * Zwraca referencję do mapy symulacji
     * @return dwuwymiarowa tablica kratek
     */
    public Kratka[][] getMapa() {
        return mapa;
    }
    /**
     * Przeprowadza jedną turę symulacji
     */
    public void tura() {
        aktualnaTura++;

        modyfikator.updateModyfikator();
        ostatniLog = getBuffDebuffLog();

        // Krok 1: Produkcja jednostek
        for (RasaBase rasa : rasy) {
            boolean hasUnits = (rasa.getPiechota() > 0 || rasa.getLucznicy() > 0);
            boolean hasTerritory = false;
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if (mapa[i][j].getOwnerRasaId() == rasa.getId() && !mapa[i][j].isPrzeszkoda()) {
                        hasTerritory = true;
                        break;
                    }
                }
                if (hasTerritory) break;
            }

            if (hasUnits || hasTerritory) {
                rasa.dodajJednostki(rasa.getProdukcja(), rasa.getProdukcja());
            }
        }

        // Krok 2: Podbój nowych kratek
        for (RasaBase rasa : rasy) {
            List<Kratka> posiadaneKratki = new ArrayList<>();
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if (mapa[i][j].getOwnerRasaId() == rasa.getId() && !mapa[i][j].isPrzeszkoda()) {
                        posiadaneKratki.add(mapa[i][j]);
                    }
                }
            }

            if (posiadaneKratki.isEmpty() && (rasa.getPiechota() == 0 && rasa.getLucznicy() == 0)) {
                // Ta rasa nie ma już ani terytorium, ani jednostek. Nie może działać.
                continue;
            }

            // Używamy silaZBonusem() do określenia liczby podbojów
            double podbojeWTurze = (rasa.silaZBonusem() / 1000) + 1;
            if (podbojeWTurze > rozmiar*3) podbojeWTurze = rozmiar;
            if (podbojeWTurze == 0) podbojeWTurze = 1;

            for (int k = 0; k < podbojeWTurze; k++) {
                if (posiadaneKratki.isEmpty()) break;
                Kratka losowaKratkaBazowa = posiadaneKratki.get(random.nextInt(posiadaneKratki.size()));
                List<Kratka> sasiedzi = znajdzSasiednie(losowaKratkaBazowa.getX(), losowaKratkaBazowa.getY());
                Collections.shuffle(sasiedzi);

                boolean conquered = false;
                for (Kratka sasiad : sasiedzi) {
                    if (sasiad.isPrzeszkoda()) {
                        continue;
                    }

                    int sasiadOwnerId = sasiad.getOwnerRasaId();
                    if (sasiadOwnerId == -1) { // Pusta kratka
                        sasiad.setOwnerRasaId(rasa.getId());
                        rasa.dodajJednostki(rasa.getProdukcja() / 2, rasa.getProdukcja() / 2); // Nagroda za podbój pustej
                        conquered = true;
                        break;
                    } else if (sasiadOwnerId != rasa.getId()) { // Kratka przeciwnika
                        RasaBase przeciwnik = znajdzRase(sasiadOwnerId);
                        if (przeciwnik != null) {
                            if (rasa.silaZBonusem() > przeciwnik.silaZBonusem()) {
                                sasiad.setOwnerRasaId(rasa.getId());
                                int utraconeJednostkiPiech = (int) (przeciwnik.getPiechota() * 0.2);
                                int utraconeJednostkiLucz = (int) (przeciwnik.getLucznicy() * 0.2);
                                przeciwnik.setJednostki(Math.max(0, przeciwnik.getPiechota() - utraconeJednostkiPiech), Math.max(0, przeciwnik.getLucznicy() - utraconeJednostkiLucz));

                                int rasaUtrataPiech = (int) (rasa.getPiechota() * 0.1);
                                int rasaUtrataLucz = (int) (rasa.getLucznicy() * 0.1);
                                // Upewnij się, że nie tracimy więcej jednostek, niż mamy
                                rasaUtrataPiech = Math.min(rasaUtrataPiech, rasa.getPiechota());
                                rasaUtrataLucz = Math.min(rasaUtrataLucz, rasa.getLucznicy());

                                // Ustaw nowe ilości jednostek dla rasy atakującej
                                rasa.setJednostki(Math.max(0, rasa.getPiechota() - rasaUtrataPiech), Math.max(0, rasa.getLucznicy() - rasaUtrataLucz));

                                conquered = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Sprawdza czy symulacja powinna się zakończyć
     * @return true jeśli symulacja powinna się zakończyć, false w przeciwnym przypadku
     */
    public boolean czyKoniec() {
        // Oblicz liczbę aktywnych ras
        List<RasaBase> aktywneRasy = new ArrayList<>();
        for (RasaBase rasa : rasy) {
            boolean hasTerritory = false;
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if (mapa[i][j].getOwnerRasaId() == rasa.getId() && !mapa[i][j].isPrzeszkoda()) {
                        hasTerritory = true;
                        break;
                    }
                }
                if (hasTerritory) break;
            }
            boolean hasUnits = (rasa.getPiechota() > 0 || rasa.getLucznicy() > 0);

            if (hasTerritory || hasUnits) {
                aktywneRasy.add(rasa);
            }
        }

        // Warunek 1: Pozostała jedna aktywna rasa
        // Jeśli tylko jedna rasa pozostała na mapie z terytorium lub jednostkami, to ona wygrywa.
        if (aktywneRasy.size() == 1) {
            return true;
        }

        // Warunek 2: Żadna rasa nie jest aktywna (wszystkie wyginęły)
        // Jeśli żadna rasa nie ma terytorium ani jednostek, symulacja się kończy.
        if (aktywneRasy.isEmpty()) {
            return true;
        }

        // Warunek 3: Wszystkie aktywne rasy są "utknięte" (nie mogą się rozszerzać ani walczyć)
        // To jest przypadek, gdy walka do ostatniego żywego nie jest możliwa,
        // bo rasy są oddzielone przeszkodami i nie mogą się do siebie dostać.
        boolean wszystkieAktywneRasyUtkniete = true;
        for (RasaBase rasa : aktywneRasy) {
            if (canRasaExpand(rasa.getId())) {
                wszystkieAktywneRasyUtkniete = false; // Jeśli jakakolwiek rasa może się ruszyć, symulacja trwa
                break;
            }
        }

        return wszystkieAktywneRasyUtkniete;
    }

    /**
     * Generuje komunikat o aktualnym modyfikatorze
     * @return sformatowany komunikat dla aktualnej tury
     */
    public String getBuffDebuffLog() {
        StringBuilder sb = new StringBuilder("Tura " + aktualnaTura + ": ");
        if (getModyfikator().isSwiatlo()) sb.append("Światło rozjaśnia świat, wielbiciele nocy chowają się w cień.");
        else if (getModyfikator().isMrok()) sb.append("Mrok zapada, wędrujący w cienach zyskują przewagę.");
        else if (getModyfikator().isZlota()) sb.append("Złoto pobłyskuje, chciwcy rosną w siłę.");
        else if (getModyfikator().isKrol()) sb.append("Królestwo rośnie w siłę, CHWALMY KRÓLA!");
        else if (getModyfikator().isSmok()) sb.append("Smok sieje spustoszenie, pożoga ogrania świat.");
        else sb.append("Brak modyfikatorów.");
        return sb.toString();
    }

    // Metoda canRasaExpand() musi zostać zaktualizowana, aby odzwierciedlała nową logikę.
    private boolean canRasaExpand(int rasaId) {
        RasaBase checkingRasa = znajdzRase(rasaId);
        if (checkingRasa == null) return false;

        // Jeśli rasa nie ma jednostek, nie może aktywnie podbijać ani walczyć
        if (checkingRasa.getPiechota() <= 0 && checkingRasa.getLucznicy() <= 0) {
            return false;
        }

        for (int i = 0; i < rozmiar; i++) {
            for (int j = 0; j < rozmiar; j++) {
                if (mapa[i][j].getOwnerRasaId() == rasaId && !mapa[i][j].isPrzeszkoda()) {
                    List<Kratka> sasiednie = znajdzSasiednie(i, j);
                    for (Kratka sasiad : sasiednie) {
                        if (sasiad.isPrzeszkoda()) {
                            continue;
                        }
                        if (sasiad.getOwnerRasaId() == -1) { // Może podbić pustą kratkę
                            return true;
                        } else if (sasiad.getOwnerRasaId() != rasaId) { // Może zaatakować wroga
                            RasaBase defendingRasa = znajdzRase(sasiad.getOwnerRasaId());
                            if (defendingRasa != null) {
                                // Sprawdź, czy rasa jest w stanie pokonać obrońcę
                                if (checkingRasa.silaZBonusem() > defendingRasa.silaZBonusem()) {
                                    return true; // Może rozszerzyć się poprzez walkę
                                }
                                // Jeśli siła atakującego jest mniejsza lub równa, nie może podbić tej konkretnej kratki.
                                // Ale to nie oznacza, że nie może się rozszerzać w ogóle.
                                // Trzeba sprawdzić, czy może podbić jakąkolwiek inną kratkę.
                            }
                        }
                    }
                }
            }
        }
        return false; // Rasa nie może się rozszerzać ani walczyć
    }


    // Metoda getZwyciezcy() również wymaga aktualizacji, aby odzwierciedlać nową logikę zakończenia.
    // Zwycięzcą będzie jedyna aktywna rasa. Jeśli jest remis, to wszystkie rasy z remisu.
    public List<RasaBase> getZwyciezcy() {
        List<RasaBase> winners = new ArrayList<>();
        List<RasaBase> activeRaces = new ArrayList<>();

        // Zbieranie aktywnych ras (mających terytorium LUB jednostki)
        for (RasaBase rasa : rasy) {
            boolean hasTerritory = false;
            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    if (mapa[i][j].getOwnerRasaId() == rasa.getId() && !mapa[i][j].isPrzeszkoda()) {
                        hasTerritory = true;
                        break;
                    }
                }
                if (hasTerritory) break;
            }
            boolean hasUnits = (rasa.getPiechota() > 0 || rasa.getLucznicy() > 0);
            if (hasTerritory || hasUnits) {
                activeRaces.add(rasa);
            }
        }

        // Jeśli jest tylko jedna aktywna rasa, ona jest zwycięzcą
        if (activeRaces.size() == 1) {
            winners.add(activeRaces.get(0));
            return winners;
        }

        // Jeśli więcej niż jedna rasa nadal jest aktywna, ale wszystkie są "utknięte"
        // (nie mogą się rozszerzać, ale nie wyginęły), wtedy zwycięzcą jest ta, która ma najwięcej terytorium.
        // Jeśli jest remis w terytorium, to ta z największą bazową siłą (sila()).
        if (!activeRaces.isEmpty() && czyKoniec()) { // Upewniamy się, że to jest warunek zakończenia przez "utknięcie"
            int maxOccupied = -1;
            Map<Integer, Integer> occupiedCount = new HashMap<>();
            for (RasaBase r : activeRaces) {
                occupiedCount.put(r.getId(), 0);
            }

            for (int i = 0; i < rozmiar; i++) {
                for (int j = 0; j < rozmiar; j++) {
                    int ownerId = mapa[i][j].getOwnerRasaId();
                    if (ownerId != -1 && occupiedCount.containsKey(ownerId)) { // Upewnij się, że ownerId jest wśród aktywnych ras
                        occupiedCount.put(ownerId, occupiedCount.get(ownerId) + 1);
                    }
                }
            }

            List<RasaBase> potentialWinnersByTerritory = new ArrayList<>();
            for (RasaBase r : activeRaces) {
                int currentOccupied = occupiedCount.getOrDefault(r.getId(), 0);
                if (currentOccupied > maxOccupied) {
                    maxOccupied = currentOccupied;
                    potentialWinnersByTerritory.clear();
                    potentialWinnersByTerritory.add(r);
                } else if (currentOccupied == maxOccupied && currentOccupied > 0) {
                    potentialWinnersByTerritory.add(r);
                }
            }

            if (potentialWinnersByTerritory.size() == 1) {
                winners.addAll(potentialWinnersByTerritory);
            } else { // Remis w terytorium, sprawdzamy siłę bazową
                double maxStrength = -1;
                for (RasaBase pw : potentialWinnersByTerritory) {
                    double currentStrength = pw.sila(); // Używamy sila() bo to statystyka wyświetlana
                    if (currentStrength > maxStrength) {
                        maxStrength = currentStrength;
                        winners.clear();
                        winners.add(pw);
                    } else if (currentStrength == maxStrength) {
                        winners.add(pw);
                    }
                }
            }
        }
        // Jeśli wszystkie rasy wyginęły (activeRaces jest puste), to winners pozostanie puste.
        // To jest poprawna interpretacja "braku zwycięzców".
        return winners;
    }

    /**
     * Znajduje rasę o podanym identyfikatorze
     * @param id identyfikator rasy do znalezienia
     * @return obiekt RasaBase lub null jeśli nie znaleziono
     */
    private RasaBase znajdzRase(int id) {
        for (RasaBase rasa : rasy) {
            if (rasa.getId() == id) return rasa;
        }
        return null;
    }

    /**
     * Znajduje sąsiednie kratki dla podanych współrzędnych
     * @param x współrzędna X kratki
     * @param y współrzędna Y kratki
     * @return lista sąsiednich kratek (pomija kratki poza mapą)
     */
    private List<Kratka> znajdzSasiednie(int x, int y) {
        List<Kratka> lista = new ArrayList<>();
        int[][] kierunki = {{-1,0}, {1,0}, {0,-1}, {0,1}};

        for (int[] kierunek : kierunki) {
            int nx = x + kierunek[0];
            int ny = y + kierunek[1];

            if (nx >= 0 && nx < rozmiar && ny >= 0 && ny < rozmiar) {
                lista.add(mapa[nx][ny]);
            }
        }
        return lista;
    }

    /**
     * Zwraca globalny modyfikator symulacji
     * @return instancja modyfikatora
     */
    public static Modyfikator getModyfikator() {
        return modyfikator;
    }
}