package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Advent of Code 2025 - Day 6: Trash Compactor
 * https://adventofcode.com/2025/day/6
 *
 * Puzzle: Feuille de calcul cephalopode avec problèmes en colonnes.
 * Chaque colonne = un problème avec des nombres et un opérateur (+/*) en bas.
 * Calculer la somme de tous les résultats.
 */
public class Day06 {

    public static void main(String[] args) {
        List<String> lines = InputReader.readDay(6);

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
        List<Problem> problems = parseProblems(lines);

        return problems.stream().mapToLong(p -> p.compute()).sum();
    }

    private static long solvePart2(List<String> lines) {
        List<Problem> problems = parseProblemsPart2(lines);

        return problems.stream().mapToLong(p -> p.compute()).sum();
    }

    private static List<Problem> parseProblems(List<String> lines) {
        lines = lines.stream().filter(l -> !l.isBlank()).toList();

        // Lignes de nombres
        List<List<Long>> numberRows = lines.subList(0, lines.size() - 1).stream()
            .map(InputReader::extractLongs)
            .toList();

        // Ligne d'opérateurs
        List<String> operators = InputReader.findAll(lines.get(lines.size() - 1), "[+*]");

        // Construire les problèmes par colonne
        List<Problem> problems = new ArrayList<>();
        for (int col = 0; col < operators.size(); col++) {
            int c = col;
            List<Long> numbers = numberRows.stream().map(row -> row.get(c)).toList();
            problems.add(new Problem(new ArrayList<Long>(numbers), operators.get(col).charAt(0)));
        }

        return problems;
    }
    
    private static List<Problem> parseProblemsPart2(List<String> lines) {
        lines = lines.stream().filter(l -> !l.isBlank()).toList();

        int maxWidth = lines.stream().mapToInt(String::length).max().orElse(0);
        List<String> numLines = lines.subList(0, lines.size() - 1);

        List<String> operators = InputReader.findAll(lines.get(lines.size() - 1), "[+*]");

        // Lire colonne par colonne, construire un nombre par colonne (chiffres verticaux)
        List<List<Long>> allNumbers = new ArrayList<>();
        List<Long> numbers = new ArrayList<>();

        for (int col = 0; col < maxWidth; col++) {
            // Lire cette colonne verticalement
            String numberAsString = "";
            for (String line : numLines) {
                if (col < line.length() && line.charAt(col) != ' ') {
                    numberAsString += line.charAt(col);
                }
            }

            if (!numberAsString.isEmpty()) {
                numbers.add(Long.parseLong(numberAsString));
            } else if (!numbers.isEmpty()) {
                allNumbers.add(numbers);
                numbers = new ArrayList<>();
            }
        }
        if (!numbers.isEmpty()) {
            allNumbers.add(numbers);
        }

        // Construire les problèmes
        List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < operators.size(); i++) {
            problems.add(new Problem(allNumbers.get(i), operators.get(i).charAt(0)));
        }

        return problems;
    }

    record Problem(List<Long> numbers, char operator) {
        long compute() {
            if (operator == '+') {
                return numbers.stream().mapToLong(Long::longValue).sum();
            } else {
                return numbers.stream().mapToLong(Long::longValue).reduce(1L, (a, b) -> a * b);
            }
        }
    }
}
