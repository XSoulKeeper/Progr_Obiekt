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
* **Java Development Kit (JDK) 11 lub nowszy:** Projekt korzysta z JavaFX, które jest modularne od JDK 11.
* **Maven lub Gradle (opcjonalnie, do zarządzania zależnościami):** Jeśli będziesz budować projekt samodzielnie.

Do uruchomienia pliku `.exe` potrzebujesz jedynie środowiska **Java Runtime Environment (JRE)** zgodnego z wersją JDK, na której aplikacja została skompilowana.

## Uruchamianie Aplikacji

Istnieją dwie główne metody uruchomienia aplikacji:

### Z Pliku Wykonywalnego (.exe)

Jeśli posiadasz wygenerowany plik `.exe` (np. za pomocą Launch4j), po prostu uruchom go:

1.  Przejdź do katalogu, w którym znajduje się plik `Symulacja.exe` (lub inna nazwa, którą nadałeś).
2.  Dwukrotnie kliknij plik `Symulacja.exe`.

Aplikacja powinna się uruchomić.

### Z Kodu Źródłowego

Aby uruchomić aplikację z kodu źródłowego:

1.  **Sklonuj lub pobierz repozytorium:**
    ```bash
    git clone <adres_twojego_repozytorium>
    cd <nazwa_folderu_projektu>
    ```
    (Zastąp `<adres_twojego_repozytorium>` i `<nazwa_folderu_projektu>` odpowiednimi wartościami.)

2.  **Skonfiguruj środowisko (jeśli używasz IDE):**
    Jeśli używasz IntelliJ IDEA, Eclipse lub podobnego IDE:
    * Otwórz projekt w swoim IDE.
    * Upewnij się, że masz skonfigurowany moduł JavaFX. Może być konieczne dodanie zależności JavaFX do Twojego projektu (np. przez Maven/Gradle lub jako moduły JVM).

3.  **Zbuduj projekt:**

    * **Maven:** Jeśli używasz Mavena, możesz zbudować projekt za pomocą komendy:
        ```bash
        mvn clean install
        ```
    * **Bez narzędzia do budowania (czysta Java):** Jeśli nie używasz Mavena/Gradle, musisz ręcznie skompilować pliki `.java` i uwzględnić zależności JavaFX. Jest to bardziej złożone.

4.  **Uruchom aplikację:**

    * **Z poziomu IDE:** Uruchom klasę `Main.java` jako aplikację Java.
    * **Z linii komend (po zbudowaniu Jarem z zależnościami):**
        Jeśli zbudowałeś plik `.jar` (np. `your-project.jar`) zawierający wszystkie zależności, możesz go uruchomić komendą:
        ```bash
        java -jar your-project.jar
        ```
        Jeśli zależności JavaFX nie są spakowane w Jarze, musisz je dodać do classpath:
        ```bash
        java --module-path <ścieżka_do_javafx_libs> --add-modules javafx.controls,javafx.fxml -jar your-project.jar
        ```
        Zastąp `<ścieżka_do_javafx_libs>` ścieżką do katalogu `lib` Twojej instalacji JavaFX SDK.

## Opis Gry

Symulacja rozgrywa się na kwadratowej mapie, gdzie różne rasy walczą o kontrolę nad terytoriami.

**Główne elementy:**
* **Rasy:** Dostępne są różne rasy (np. Ludzie, Orkowie), każda z własnymi unikalnymi modyfikatorami siły.
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
* `Ludzie.java`, `Orkowie.java`: Konkretne implementacje ras, nadpisujące metody obliczania siły z uwzględnieniem specyficznych dla rasy bonusów/debuffów od modyfikatorów.
* `Kratka.java`: Reprezentuje pojedyncze pole na mapie, przechowuje informację o właścicielu i czy jest przeszkodą.
* `Przeszkoda.java`: Specjalna klasa `Kratka` reprezentująca niezdobywalną przeszkodę.
* `Modyfikator.java`: Zarządza losowymi modyfikatorami wpływającymi na siłę ras w danej turze.
* `GeneratorLiczb.java`: Klasa pomocnicza do generowania liczb losowych.

## Użyte Technologie

* **Java 11+**
* **JavaFX:** Do stworzenia graficznego interfejsu użytkownika.
* **Launch4j:** Do generowania pliku wykonywalnego `.exe` (opcjonalnie, do dystrybucji).

## Autor

[Twoje Imię/Nazwa Użytkownika]
[Opcjonalnie: Twój GitHub, LinkedIn lub inna forma kontaktu]
