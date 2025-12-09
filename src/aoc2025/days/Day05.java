package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Advent of Code 2025 - Day 5: Cafeteria
 * https://adventofcode.com/2025/day/5
 *
 * Puzzle: Base de données d'ingrédients avec des ranges d'IDs frais.
 * Un ingrédient est frais s'il tombe dans au moins un range.
 */
public class Day05 {

    public static void main(String[] args) {
        List<String> lines = InputReader.readDay(5);

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
    	List<List<String>> groups = InputReader.splitByEmptyLines(lines);

        // Parser les ranges de fraîcheur (format "3-5")
        List<Range> freshRanges = groups.get(0).stream()
            .map(line -> {
                String[] parts = line.split("-");
                return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            })
            .toList();

        // Parser les IDs d'ingrédients disponibles
        List<Long> ingredientIds = groups.get(1).stream()
            .map(line -> InputReader.extractLongs(line).get(0))
            .toList();

        // Compter les ingrédients frais (dans au moins un range)
        return ingredientIds.stream().filter(id -> freshRanges.stream().anyMatch(r -> r.contains(id))).count();
    }

    private static long solvePart2(List<String> lines) {
        List<List<String>> groups = InputReader.splitByEmptyLines(lines);

        List<Range> freshRanges = groups.get(0).stream()
            .map(line -> {
                String[] parts = line.split("-");
                return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            })
            .toList();

        // Range.totalCoverage fait le merge + somme des longueurs
        return Range.totalCoverage(freshRanges);
    }
}
