package aoc2025.utils;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe utilitaire pour les coordonnées 3D.
 * Utile pour les puzzles volumétriques (ex: cubes de lave, tetris 3D).
 */
public class Point3D implements Comparable<Point3D> {
    public final int x;
    public final int y;
    public final int z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Opérations de base
    public Point3D add(Point3D other) {
        return new Point3D(x + other.x, y + other.y, z + other.z);
    }

    public Point3D add(int dx, int dy, int dz) {
        return new Point3D(x + dx, y + dy, z + dz);
    }

    public Point3D subtract(Point3D other) {
        return new Point3D(x - other.x, y - other.y, z - other.z);
    }

    public Point3D multiply(int scalar) {
        return new Point3D(x * scalar, y * scalar, z * scalar);
    }

    // Distance
    public int manhattanDistance(Point3D other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
    }

    public int manhattanDistance() {
        return Math.abs(x) + Math.abs(y) + Math.abs(z);
    }

    public double euclideanDistance(Point3D other) {
        long dx = x - other.x;
        long dy = y - other.y;
        long dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // Voisins (6 directions cardinales)
    public List<Point3D> getNeighbors6() {
        return List.of(
            new Point3D(x + 1, y, z),
            new Point3D(x - 1, y, z),
            new Point3D(x, y + 1, z),
            new Point3D(x, y - 1, z),
            new Point3D(x, y, z + 1),
            new Point3D(x, y, z - 1)
        );
    }

    // Voisins (26 directions incluant diagonales)
    public List<Point3D> getNeighbors26() {
        List<Point3D> neighbors = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx != 0 || dy != 0 || dz != 0) {
                        neighbors.add(new Point3D(x + dx, y + dy, z + dz));
                    }
                }
            }
        }
        return neighbors;
    }

    // Bounds checking
    public boolean isInBounds(int width, int height, int depth) {
        return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth;
    }

    // Static factories
    public static Point3D of(int x, int y, int z) {
        return new Point3D(x, y, z);
    }

    public static Point3D parse(String s) {
        String[] parts = s.split("[,\\s]+");
        return new Point3D(
            Integer.parseInt(parts[0].trim()),
            Integer.parseInt(parts[1].trim()),
            Integer.parseInt(parts[2].trim())
        );
    }

    public static Point3D ORIGIN = new Point3D(0, 0, 0);

    // Conversion
    public Point2D toPoint2D() {
        return new Point2D(x, y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point3D other = (Point3D) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int compareTo(Point3D other) {
        int cmp = Integer.compare(z, other.z);
        if (cmp != 0) return cmp;
        cmp = Integer.compare(y, other.y);
        return cmp != 0 ? cmp : Integer.compare(x, other.x);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
}
