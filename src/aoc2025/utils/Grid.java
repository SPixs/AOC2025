package aoc2025.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Classe utilitaire pour manipuler des grilles 2D.
 * Gère parsing, accès, recherche et transformations.
 */
public class Grid<T> {
    private final Object[][] data;
    public final int width;
    public final int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new Object[height][width];
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        return (T) data[y][x];
    }

    @SuppressWarnings("unchecked")
    public T get(Point2D p) {
        return (T) data[p.y][p.x];
    }

    public void set(int x, int y, T value) {
        data[y][x] = value;
    }

    public void set(Point2D p, T value) {
        data[p.y][p.x] = value;
    }

    @SuppressWarnings("unchecked")
    public T getOrDefault(int x, int y, T defaultValue) {
        if (!isInBounds(x, y)) return defaultValue;
        return (T) data[y][x];
    }

    @SuppressWarnings("unchecked")
    public T getOrDefault(Point2D p, T defaultValue) {
        if (!isInBounds(p)) return defaultValue;
        return (T) data[p.y][p.x];
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isInBounds(Point2D p) {
        return isInBounds(p.x, p.y);
    }

    // Recherche
    public Optional<Point2D> find(T value) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Objects.equals(data[y][x], value)) {
                    return Optional.of(new Point2D(x, y));
                }
            }
        }
        return Optional.empty();
    }

    public List<Point2D> findAll(T value) {
        List<Point2D> result = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Objects.equals(data[y][x], value)) {
                    result.add(new Point2D(x, y));
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Point2D> findAll(Predicate<T> predicate) {
        List<Point2D> result = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (predicate.test((T) data[y][x])) {
                    result.add(new Point2D(x, y));
                }
            }
        }
        return result;
    }

    // Itération
    public Stream<Point2D> allPoints() {
        List<Point2D> points = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                points.add(new Point2D(x, y));
            }
        }
        return points.stream();
    }

    // Voisins valides
    public List<Point2D> getNeighbors4(Point2D p) {
        return p.getNeighbors4().stream()
            .filter(this::isInBounds)
            .toList();
    }

    public List<Point2D> getNeighbors8(Point2D p) {
        return p.getNeighbors8().stream()
            .filter(this::isInBounds)
            .toList();
    }

    // Copie
    public Grid<T> copy() {
        Grid<T> copy = new Grid<>(width, height);
        for (int y = 0; y < height; y++) {
            System.arraycopy(data[y], 0, copy.data[y], 0, width);
        }
        return copy;
    }

    // Comptage
    public long count(T value) {
        long count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Objects.equals(data[y][x], value)) count++;
            }
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    public long count(Predicate<T> predicate) {
        long count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (predicate.test((T) data[y][x])) count++;
            }
        }
        return count;
    }

    // Affichage
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(data[y][x]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // ========== STATIC FACTORIES ==========

    // Parser depuis List<String> en Grid<Character>
    public static Grid<Character> fromLines(List<String> lines) {
        int height = lines.size();
        int width = lines.isEmpty() ? 0 : lines.get(0).length();
        Grid<Character> grid = new Grid<>(width, height);
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                grid.set(x, y, line.charAt(x));
            }
        }
        return grid;
    }

    // Parser avec conversion custom
    public static <T> Grid<T> fromLines(List<String> lines, Function<Character, T> mapper) {
        int height = lines.size();
        int width = lines.isEmpty() ? 0 : lines.get(0).length();
        Grid<T> grid = new Grid<>(width, height);
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                grid.set(x, y, mapper.apply(line.charAt(x)));
            }
        }
        return grid;
    }

    // Parser en Grid<Integer> (chiffres 0-9)
    public static Grid<Integer> fromDigits(List<String> lines) {
        return fromLines(lines, c -> c - '0');
    }
}
