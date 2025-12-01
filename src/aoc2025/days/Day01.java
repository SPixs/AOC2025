package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Advent of Code 2025 - Day 1: Secret Entrance
 * https://adventofcode.com/2025/day/1
 *
 * Puzzle: Un dial circulaire (0-99) qu'on tourne avec des commandes L/R.
 * - Part 1: Compter combien de fois le dial s'arrête sur 0 après une rotation.
 * - Part 2: Compter combien de fois le dial PASSE par 0 (pendant + après rotation).
 */
public class Day01 {

    private static final int DIAL_SIZE = 100;  // Dial de 0 à 99
    private static final int START_POS = 50;   // Position initiale

    public static void main(String[] args) {
        List<String> lines = InputReader.readDay(1);

        // ===== PART 1 =====
        long startTime = System.nanoTime();
        long result1 = solvePart1(lines);
        long part1Time = System.nanoTime() - startTime;
        System.out.println("Result part 1 : " + result1 + " in " +
            TimeUnit.NANOSECONDS.toMillis(part1Time) + "ms");

        // ===== PART 2 =====
        startTime = System.nanoTime();
        long result2 = solvePart2(lines);
        long part2Time = System.nanoTime() - startTime;
        System.out.println("Result part 2 : " + result2 + " in " +
            TimeUnit.NANOSECONDS.toMillis(part2Time) + "ms");
    }

    /**
     * Part 1: Compter les fois où le dial S'ARRETE sur 0.
     */
    private static long solvePart1(List<String> lines) {
        int pos = START_POS;
        int count = 0;

        for (String command : lines) {
            // Parser la commande (ex: "L68" -> -68, "R48" -> +48)
            int steps = parseCommand(command);

            // Appliquer la rotation (modulo circulaire)
            pos = MathUtils.mod(pos + steps, DIAL_SIZE);

            // Compter si on s'arrête sur 0
            if (pos == 0) {
                count++;
            }
        }

        return count;
    }

    /**
     * Part 2: Compter les fois où le dial PASSE par 0 (y compris pendant la rotation).
     */
    private static long solvePart2(List<String> lines) {
        int pos = START_POS;
        int count = 0;

        for (String command : lines) {
            int steps = parseCommand(command);

            // Position linéaire (sans modulo) pour calculer les passages
            int newPos = pos + steps;

            // Compter les tours complets (chaque tour passe par 0 une fois)
            while (newPos - pos >= DIAL_SIZE) {
                count++;
                newPos -= DIAL_SIZE;
            }
            while (pos - newPos >= DIAL_SIZE) {
                count++;
                newPos += DIAL_SIZE;
            }

            // Passage par 0 dans la rotation partielle restante
            if ((newPos % DIAL_SIZE) == 0 || newPos > DIAL_SIZE) {
                count++;
            }
            if (pos > 0 && newPos < 0) {
                count++;
            }

            // Normaliser la position finale
            pos = MathUtils.mod(newPos, DIAL_SIZE);
        }

        return count;
    }

    /**
     * Parse une commande de rotation.
     * @param command Format "L68" ou "R48"
     * @return Delta signé (négatif pour L, positif pour R)
     */
    private static int parseCommand(String command) {
        int steps = InputReader.extractInt(command);
        boolean isRight = Character.toUpperCase(command.charAt(0)) == 'R';
        return isRight ? steps : -steps;
    }
}
