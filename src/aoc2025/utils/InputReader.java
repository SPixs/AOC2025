package aoc2025.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * Utilitaires de lecture et parsing des fichiers input.
 */
public class InputReader {

    // ========== FILE READING ==========

    /**
     * Lit toutes les lignes d'un fichier.
     */
    public static List<String> readLines(String filename) {
        try {
            return Files.readAllLines(Path.of(filename));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read file: " + filename, e);
        }
    }

    /**
     * Lit le fichier input du jour spécifié.
     */
    public static List<String> readDay(int day) {
        String filename = String.format("input/input_day%02d.txt", day);
        return readLines(filename);
    }

    /**
     * Lit tout le fichier comme une seule string.
     */
    public static String readAll(String filename) {
        try {
            return Files.readString(Path.of(filename));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read file: " + filename, e);
        }
    }

    /**
     * Lit le fichier et retourne un Stream de lignes.
     */
    public static Stream<String> streamLines(String filename) {
        try {
            return Files.lines(Path.of(filename));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read file: " + filename, e);
        }
    }

    // ========== SPLITTING ==========

    /**
     * Sépare l'input en groupes séparés par des lignes vides.
     */
    public static List<List<String>> splitByEmptyLines(List<String> lines) {
        List<List<String>> groups = new ArrayList<>();
        List<String> current = new ArrayList<>();

        for (String line : lines) {
            if (line.isEmpty()) {
                if (!current.isEmpty()) {
                    groups.add(current);
                    current = new ArrayList<>();
                }
            } else {
                current.add(line);
            }
        }
        if (!current.isEmpty()) {
            groups.add(current);
        }
        return groups;
    }

    /**
     * Sépare le contenu par une regex.
     */
    public static List<String> splitContent(String content, String regex) {
        return Arrays.asList(content.split(regex));
    }

    // ========== PARSING NUMBERS ==========

    /**
     * Extrait tous les entiers d'une ligne.
     */
    public static List<Integer> extractInts(String line) {
        List<Integer> numbers = new ArrayList<>();
        Matcher m = Pattern.compile("-?\\d+").matcher(line);
        while (m.find()) {
            numbers.add(Integer.parseInt(m.group()));
        }
        return numbers;
    }

    /**
     * Extrait tous les longs d'une ligne.
     */
    public static List<Long> extractLongs(String line) {
        List<Long> numbers = new ArrayList<>();
        Matcher m = Pattern.compile("-?\\d+").matcher(line);
        while (m.find()) {
            numbers.add(Long.parseLong(m.group()));
        }
        return numbers;
    }

    /**
     * Extrait le premier entier d'une ligne.
     */
    public static int extractInt(String line) {
        Matcher m = Pattern.compile("-?\\d+").matcher(line);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }
        throw new IllegalArgumentException("No integer found in: " + line);
    }

    /**
     * Parse une ligne d'entiers séparés par un délimiteur.
     */
    public static List<Integer> parseInts(String line, String delimiter) {
        return Arrays.stream(line.split(delimiter))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Integer::parseInt)
            .toList();
    }

    /**
     * Parse une ligne de longs séparés par un délimiteur.
     */
    public static List<Long> parseLongs(String line, String delimiter) {
        return Arrays.stream(line.split(delimiter))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .toList();
    }

    /**
     * Parse un tableau 2D d'entiers.
     */
    public static int[][] parse2DIntArray(List<String> lines, String delimiter) {
        return lines.stream()
            .map(line -> parseInts(line, delimiter).stream().mapToInt(i -> i).toArray())
            .toArray(int[][]::new);
    }

    // ========== REGEX PARSING ==========

    /**
     * Extrait des groupes via une regex.
     */
    public static Optional<List<String>> match(String line, String regex) {
        Matcher m = Pattern.compile(regex).matcher(line);
        if (m.matches()) {
            List<String> groups = new ArrayList<>();
            for (int i = 1; i <= m.groupCount(); i++) {
                groups.add(m.group(i));
            }
            return Optional.of(groups);
        }
        return Optional.empty();
    }

    /**
     * Trouve toutes les correspondances d'une regex.
     */
    public static List<String> findAll(String line, String regex) {
        List<String> matches = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(line);
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }

    /**
     * Trouve toutes les correspondances avec groupes.
     */
    public static List<List<String>> findAllGroups(String line, String regex) {
        List<List<String>> results = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(line);
        while (m.find()) {
            List<String> groups = new ArrayList<>();
            for (int i = 1; i <= m.groupCount(); i++) {
                groups.add(m.group(i));
            }
            results.add(groups);
        }
        return results;
    }

    // ========== GRID PARSING ==========

    /**
     * Parse une grille de caractères.
     */
    public static char[][] parseCharGrid(List<String> lines) {
        int height = lines.size();
        int width = lines.isEmpty() ? 0 : lines.get(0).length();
        char[][] grid = new char[height][width];
        for (int y = 0; y < height; y++) {
            grid[y] = lines.get(y).toCharArray();
        }
        return grid;
    }

    /**
     * Parse une grille de chiffres (0-9).
     */
    public static int[][] parseDigitGrid(List<String> lines) {
        int height = lines.size();
        int width = lines.isEmpty() ? 0 : lines.get(0).length();
        int[][] grid = new int[height][width];
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                grid[y][x] = line.charAt(x) - '0';
            }
        }
        return grid;
    }

    // ========== COORDINATE PARSING ==========

    /**
     * Parse une liste de coordonnées Point2D.
     */
    public static List<Point2D> parsePoints(List<String> lines) {
        return lines.stream()
            .map(Point2D::parse)
            .toList();
    }

    /**
     * Parse des coordonnées depuis regex custom.
     */
    public static List<Point2D> parsePoints(List<String> lines, String regex) {
        List<Point2D> points = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        for (String line : lines) {
            Matcher m = p.matcher(line);
            if (m.find()) {
                points.add(new Point2D(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2))
                ));
            }
        }
        return points;
    }
}
