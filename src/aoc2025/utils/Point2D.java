package aoc2025.utils;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe utilitaire pour les coordonnées 2D.
 * Réutilisable pour tous les puzzles basés sur des grilles.
 */
public class Point2D implements Comparable<Point2D> {
    public final int x;
    public final int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Opérations de base
    public Point2D add(Point2D other) {
        return new Point2D(x + other.x, y + other.y);
    }

    public Point2D add(int dx, int dy) {
        return new Point2D(x + dx, y + dy);
    }

    public Point2D subtract(Point2D other) {
        return new Point2D(x - other.x, y - other.y);
    }

    public Point2D multiply(int scalar) {
        return new Point2D(x * scalar, y * scalar);
    }

    // Distance
    public int manhattanDistance(Point2D other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    public int manhattanDistance() {
        return Math.abs(x) + Math.abs(y);
    }

    public double euclideanDistance(Point2D other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    // Voisins
    public List<Point2D> getNeighbors4() {
        return List.of(
            new Point2D(x, y - 1),  // UP
            new Point2D(x + 1, y),  // RIGHT
            new Point2D(x, y + 1),  // DOWN
            new Point2D(x - 1, y)   // LEFT
        );
    }

    public List<Point2D> getNeighbors8() {
        List<Point2D> neighbors = new ArrayList<>(getNeighbors4());
        neighbors.add(new Point2D(x - 1, y - 1)); // UP-LEFT
        neighbors.add(new Point2D(x + 1, y - 1)); // UP-RIGHT
        neighbors.add(new Point2D(x - 1, y + 1)); // DOWN-LEFT
        neighbors.add(new Point2D(x + 1, y + 1)); // DOWN-RIGHT
        return neighbors;
    }

    // Directions
    public Point2D move(Direction dir) {
        return add(dir.dx, dir.dy);
    }

    public Point2D move(Direction dir, int steps) {
        return add(dir.dx * steps, dir.dy * steps);
    }

    // Bounds checking
    public boolean isInBounds(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isInBounds(int minX, int maxX, int minY, int maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    // Rotation (autour de l'origine)
    public Point2D rotateRight90() {
        return new Point2D(-y, x);
    }

    public Point2D rotateLeft90() {
        return new Point2D(y, -x);
    }

    public Point2D rotate180() {
        return new Point2D(-x, -y);
    }

    // Static factories
    public static Point2D of(int x, int y) {
        return new Point2D(x, y);
    }

    public static Point2D parse(String s) {
        String[] parts = s.split("[,\\s]+");
        return new Point2D(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
    }

    public static Point2D ORIGIN = new Point2D(0, 0);

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point2D other = (Point2D) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int compareTo(Point2D other) {
        int cmp = Integer.compare(y, other.y);
        return cmp != 0 ? cmp : Integer.compare(x, other.x);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
