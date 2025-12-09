package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Advent of Code 2025 - Day 4: Printing Department
 * https://adventofcode.com/2025/day/4
 *
 * Puzzle: Grille avec des rouleaux de papier (@).
 * Un forklift peut accéder à un rouleau si < 4 voisins (8-dir) sont aussi des rouleaux.
 */
public class Day04 {

    public static void main(String[] args) {
        // Lecture de l'input
        List<String> lines = InputReader.readDay(4);
        // String content = InputReader.readAll("input/input_day04.txt");

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
        Grid<Character> grid = Grid.fromLines(lines);
        return findRemovableRolls(grid).size();
    }

    private static List<Point2D> findRemovableRolls(Grid<Character> grid) {
        return grid.findAll('@').stream()
            .filter(r -> countAdjacentRolls(grid, r) < 4)
            .toList();
    }

    private static long countAdjacentRolls(Grid<Character> grid, Point2D pos) {
        return pos.getNeighbors8().stream()
            .filter(n -> grid.getOrDefault(n, '.') == '@')
            .count();
    }

    private static long solvePart2(List<String> lines) {
        Grid<Character> grid = Grid.fromLines(lines);

        long total = 0;
        List<Point2D> removable;
        while (!(removable = findRemovableRolls(grid)).isEmpty()) {
            total += removable.size();
            removable.forEach(p -> grid.set(p, '.'));
        }
        return total;
    }

}
