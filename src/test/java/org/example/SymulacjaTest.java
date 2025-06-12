package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.*;

import java.util.*;

class SymulacjaTest {

    @Test
    void testElfySila() {
        Elfy elfy = new Elfy(0, 10, 5, 3);
        double expected = (10 * 0.3) * (5 * 2.1);
        assertEquals(expected, elfy.sila(), 0.001);
    }

    @Test
    void testKrasnoludySila() {
        Krasnoludy krasnoludy = new Krasnoludy(1, 12, 4, 4);
        double expected = (1.2 * 12) * 4;
        assertEquals(expected, krasnoludy.sila(), 0.001);
    }

    @Test
    void testLudzieSila() {
        Ludzie ludzie = new Ludzie(2, 8, 6, 3);
        double expected = (2 * 8) * 6;
        assertEquals(expected, ludzie.sila(), 0.001);
    }

    @Test
    void testOrkowieSila() {
        Orkowie orkowie = new Orkowie(3, 7, 3, 2);
        double expected = (3 * 7) * 3;
        assertEquals(expected, orkowie.sila(), 0.001);
    }

    @Test
    void testSymulacjaCzyKoniecJednaRasa() {
        List<RasaBase> rasy = new ArrayList<>();
        rasy.add(new Elfy(0, 10, 5, 3));
        Symulacja sym = new Symulacja(5, 0, rasy, 0);
        sym.inicjalizuj();
        assertTrue(sym.czyKoniec());
    }

    @Test
    void testKratkaPrzeszkoda() {
        Kratka kratka = new Kratka(1, 2);
        assertFalse(kratka.isPrzeszkoda());
        kratka.setPrzeszkoda(true);
        assertTrue(kratka.isPrzeszkoda());
    }

    @Test
    void testGeneratorLiczbZakres() {
        for (int i = 0; i < 100; i++) {
            double val = GeneratorLiczb.generate(10, 20);
            assertTrue(val >= 10 && val <= 20);
        }
    }
}