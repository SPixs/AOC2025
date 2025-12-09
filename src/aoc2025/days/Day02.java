package aoc2025.days;

import aoc2025.utils.*;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Advent of Code 2025 - Day 2
 * Gift Shop - Invalid Product IDs
 *
 * Part 1: Find IDs that are a sequence repeated exactly twice (e.g., 1212, 123123)
 * Part 2: Find IDs that are a sequence repeated at least twice (e.g., 121212, 111)
 */
public class Day02 {

    public static void main(String[] args) {
        List<String> lines = InputReader.readDay(2);

        // ===== PART 1 =====
        long startTime = System.nanoTime();
        BigInteger result1 = solvePart1(lines);
        long part1Time = System.nanoTime() - startTime;
        System.out.println("Result part 1 : " + result1 + " in " +
                TimeUnit.NANOSECONDS.toMillis(part1Time) + "ms");

        // ===== PART 2 =====
        startTime = System.nanoTime();
        BigInteger result2 = solvePart2(lines);
        long part2Time = System.nanoTime() - startTime;
        System.out.println("Result part 2 : " + result2 + " in " +
                TimeUnit.NANOSECONDS.toMillis(part2Time) + "ms");
    }

    private static BigInteger solvePart1(List<String> lines) {
        List<Range> ranges = parseRanges(lines.get(0));
        return ranges.stream()
                .map(Day02::sumInvalidIdsPart1)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    private static BigInteger solvePart2(List<String> lines) {
        List<Range> ranges = parseRanges(lines.get(0));
        return ranges.stream()
                .map(Day02::sumInvalidIdsPart2)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    /**
     * Parse les ranges depuis la ligne d'input.
     * Format: "11-22,95-115,998-1012,..."
     */
    private static List<Range> parseRanges(String line) {
        return Arrays.stream(line.split(","))
                .map(s -> {
                    String[] parts = s.split("-");
                    return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                })
                .toList();
    }

    /**
     * Part 1: Somme des IDs invalides (motif répété exactement 2 fois).
     * Ex: 1212 = "12" répété 2 fois
     */
    private static BigInteger sumInvalidIdsPart1(Range range) {
        BigInteger sum = BigInteger.ZERO;

        // On cherche uniquement les nombres avec un nombre pair de chiffres
        long start = nextNumberWithDigitCount(range.start(), 2);
        if (start > range.end()) return BigInteger.ZERO;

        int numDigits = countDigits(start);
        String startStr = String.valueOf(start);
        long pattern = Long.parseLong(startStr.substring(0, numDigits / 2));
        long id = buildRepeatedId(pattern, 2);

        // Avancer jusqu'au premier ID dans la range
        while (id < range.start()) {
            pattern++;
            id = buildRepeatedId(pattern, 2);
        }

        // Collecter tous les IDs invalides dans la range
        while (range.contains(id)) {
            sum = sum.add(BigInteger.valueOf(id));
            pattern++;
            id = buildRepeatedId(pattern, 2);
        }

        return sum;
    }

    /**
     * Part 2: Somme des IDs invalides (motif répété au moins 2 fois).
     * Ex: 121212 = "12" répété 3 fois, 111 = "1" répété 3 fois
     */
    private static BigInteger sumInvalidIdsPart2(Range range) {
        Set<BigInteger> invalidIds = new HashSet<>();
        int maxDigits = countDigits(range.end());

        // Tester toutes les tailles de répétition possibles (2, 3, 4, ...)
        for (int repetitions = 2; repetitions <= maxDigits; repetitions++) {
            long start = nextNumberWithDigitCount(range.start(), repetitions);
            if (start > range.end()) continue;

            int numDigits = countDigits(start);
            int patternLength = numDigits / repetitions;
            if (patternLength == 0) continue;

            String startStr = String.valueOf(start);
            long pattern = Long.parseLong(startStr.substring(0, patternLength));
            long id = buildRepeatedId(pattern, repetitions);

            // Avancer jusqu'au premier ID dans la range
            while (id < range.start()) {
                pattern++;
                id = buildRepeatedId(pattern, repetitions);
            }

            // Collecter tous les IDs invalides dans la range
            while (range.contains(id)) {
                invalidIds.add(BigInteger.valueOf(id));
                pattern++;
                id = buildRepeatedId(pattern, repetitions);
            }
        }

        return invalidIds.stream()
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    /**
     * Construit un ID en répétant un motif n fois.
     * Ex: buildRepeatedId(12, 3) -> 121212
     */
    private static long buildRepeatedId(long pattern, int repetitions) {
        String patternStr = String.valueOf(pattern);
        return Long.parseLong(patternStr.repeat(repetitions));
    }

    /**
     * Compte le nombre de chiffres d'un nombre.
     */
    private static int countDigits(long n) {
        if (n == 0) return 1;
        return (int) Math.log10(n) + 1;
    }

    /**
     * Trouve le prochain nombre >= x dont le nombre de chiffres est divisible par n.
     * Ex: nextNumberWithDigitCount(100, 2) -> 1000 (car 100 a 3 chiffres, 1000 en a 4)
     */
    private static long nextNumberWithDigitCount(long x, int divisor) {
        if (x == 0) return (long) Math.pow(10, divisor - 1);

        int numDigits = countDigits(x);
        if (numDigits % divisor == 0) {
            return x;
        }

        // Trouver le prochain multiple de divisor
        int targetDigits = numDigits + (divisor - numDigits % divisor);
        return (long) Math.pow(10, targetDigits - 1);
    }
}
