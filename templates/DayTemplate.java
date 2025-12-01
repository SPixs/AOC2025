package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Advent of Code 2025 - Day XX
 * https://adventofcode.com/2025/day/XX
 */
public class DayXX {

    public static void main(String[] args) {
        // Lecture de l'input
        List<String> lines = InputReader.readDay(XX);
        // String content = InputReader.readAll("input/input_dayXX.txt");

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

    private static long solvePart1(List<String> lines) {
        // TODO: Implémenter part 1
        return 0;
    }

    private static long solvePart2(List<String> lines) {
        // TODO: Implémenter part 2
        return 0;
    }

    // ===== HELPER METHODS =====

    // Exemples de parsing courants:
    //
    // Grid de caractères:
    //   Grid<Character> grid = Grid.fromLines(lines);
    //
    // Grid de chiffres:
    //   Grid<Integer> grid = Grid.fromDigits(lines);
    //
    // Extraction de nombres:
    //   List<Integer> nums = InputReader.extractInts(line);
    //
    // Groupes séparés par lignes vides:
    //   List<List<String>> groups = InputReader.splitByEmptyLines(lines);
    //
    // Parsing regex:
    //   InputReader.match(line, "(\\w+) (\\d+)").ifPresent(groups -> {...});
}
