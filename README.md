# Symulacja Podboju Terytoriów

Ten projekt to prosta symulacja podboju terytoriów, w której różne rasy walczą o dominację na mapie. Aplikacja posiada graficzny interfejs użytkownika stworzony za pomocą JavaFX.

## Spis Treści
- [Wymagania](#wymagania)
- [Uruchamianie Aplikacji](#uruchamianie-aplikacji)
  - [Z Pliku Wykonywalnego (.exe)](#z-pliku-wykonywalnego-exe)
  - [Z Kodu Źródłowego](#z-kodu-źródłowego)
- [Opis Gry](#opis-gry)
- [Struktura Projektu](#struktura-projektu)
- [Użyte Technologie](#użyte-technologie)
- [Autor](#autor)

## Wymagania

Aby uruchomić aplikację z kodu źródłowego, potrzebujesz:
* **Java Development Kit (JDK) 21.0.0 lub nowszy:** Projekt korzysta z JavaFX, które jest modularne od JDK 11.
* **Gradle:** Jeśli będziesz budować projekt samodzielnie.


## Uruchamianie Aplikacji

Istnieją dwie główne metody uruchomienia aplikacji:

### Z Pliku Wykonywalnego (.exe)

1. Skorzystaj z linku: [POBIERZ MNIE](https://drive.google.com/file/d/1yIXS2YPXuMVvq5VMFxKHGuXVCzlIloy_/view?usp=drive_link)
2. Pobierz zawartość z Dysku Google
3. Rozpakuj zawartość w głównym folderze (`Progr_Obiekt-1.03`)
4. Dwukrotnie kliknij plik `Symulacja.exe`.

Aplikacja powinna się uruchomić.

### Z Kodu Źródłowego

Aby uruchomić aplikację z kodu źródłowego:

1.  **Sklonuj lub pobierz repozytorium:**
2.  **Otwórz projekt za pomocą wybranego IDE (w projekcie był używany IntelliJ IDEA)**
3.  **Wykonaj operację "Build & Run" na klasie Main.java**

Aplikacja powinna się uruchomić

## Opis

Symulacja rozgrywa się na kwadratowej mapie, gdzie różne rasy walczą o kontrolę nad terytoriami.

**Główne elementy:**
* **Rasy:** Dostępne są różne rasy (np. Elfy, Orkowie), każda z własnymi unikalnymi modyfikatorami siły.
* **Mapa:** Składa się z kratek, które mogą być albo pustymi terytoriami do podbicia, albo przeszkodami (nie do zdobycia).
* **Podbój Terytoriów:** Rasy rozprzestrzeniają się po mapie, podbijając sąsiednie, niezajęte kratki.
* **Walka:** Gdy dwie rasy spotkają się na tej samej kratce (lub jedna rasa próbuje podbić kratkę zajętą przez inną), dochodzi do walki. Siła rasy jest obliczana na podstawie jednostek (piechota, łucznicy) oraz losowych i specyficznych dla rasy modyfikatorów.
* **Modyfikatory:** Co turę na mapie pojawia się losowy modyfikator (np. Światło, Mrok, Złoto, Król, Smok), który wpływa na siłę ras.
* **Zwycięzca:** Symulacja trwa, dopóki nie zostanie wyłoniony zwycięzca (rasa, która podbije najwięcej terytoriów lub ma największą siłę), lub gdy wszystkie rasy zginą.

**Interfejs Użytkownika:**
Aplikacja posiada graficzny interfejs, który wizualizuje mapę, statystyki ras, oraz logi z przebiegu symulacji.

## Struktura Projektu

Główne klasy i ich odpowiedzialności:

* `Main.java`: Główna klasa uruchamiająca aplikację JavaFX.
* `SymulacjaUI.java`: Obsługuje interfejs graficzny użytkownika (JavaFX), rysowanie mapy, wyświetlanie statystyk i logów.
* `Symulacja.java`: Klasa odpowiedzialna za logikę symulacji (zarządzanie turami, podboje, walki, modyfikatory).
* `RasaBase.java`: Abstrakcyjna klasa bazowa dla wszystkich ras, definiuje wspólne właściwości i metody (np. `sila()`, `silaZBonusem()`).
* `Elfy.java`, `Orkowie.java`: Konkretne implementacje ras, nadpisujące metody obliczania siły z uwzględnieniem specyficznych dla rasy bonusów/debuffów od modyfikatorów.
* `Kratka.java`: Reprezentuje pojedyncze pole na mapie, przechowuje informację o właścicielu i czy jest przeszkodą.
* `Przeszkoda.java`: Specjalna klasa `Kratka` reprezentująca niezdobywalną przeszkodę.
* `Modyfikator.java`: Zarządza losowymi modyfikatorami wpływającymi na siłę ras w danej turze.
* `GeneratorLiczb.java`: Klasa pomocnicza do generowania liczb losowych.

## Użyte Technologie

* **Java 11+**
* **JavaFX:** Do stworzenia graficznego interfejsu użytkownika.
* **Launch4j:** Do generowania pliku wykonywalnego `.exe` (opcjonalnie, do dystrybucji).

## Autor

* **Jakub Jasiński, Daniel Salasa, Bartosz Cynk**
* **Kontakt do lidera - jakdotjas@gmail.com lub 284695@student.pwr.edu.pl**
