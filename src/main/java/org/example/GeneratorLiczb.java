package org.example;

import java.util.Random;

public class GeneratorLiczb {
    private static final Random random = new Random();

    public static double generate(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimalna wartość nie może być większa niż maksymalna.");
        }
        return min + (max - min) * random.nextDouble();
    }

}