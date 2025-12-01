package aoc2025.utils;

import java.util.*;

/**
 * Classe pour manipuler des intervalles/ranges.
 * Utile pour les puzzles de mapping et intersection.
 */
public record Range(long start, long end) implements Comparable<Range> {

    /**
     * Crée un Range [start, end] (inclusif des deux côtés).
     */
    public Range {
        if (start > end) {
            throw new IllegalArgumentException("start > end: " + start + " > " + end);
        }
    }

    /**
     * Range exclusif: [start, end)
     */
    public static Range exclusive(long start, long end) {
        return new Range(start, end - 1);
    }

    /**
     * Range de longueur n commençant à start.
     */
    public static Range ofLength(long start, long length) {
        return new Range(start, start + length - 1);
    }

    // ========== PROPERTIES ==========

    public long length() {
        return end - start + 1;
    }

    public boolean isEmpty() {
        return start > end;
    }

    public boolean contains(long value) {
        return value >= start && value <= end;
    }

    public boolean contains(Range other) {
        return start <= other.start && end >= other.end;
    }

    // ========== INTERSECTION ==========

    public boolean overlaps(Range other) {
        return start <= other.end && end >= other.start;
    }

    public Optional<Range> intersection(Range other) {
        long newStart = Math.max(start, other.start);
        long newEnd = Math.min(end, other.end);
        if (newStart <= newEnd) {
            return Optional.of(new Range(newStart, newEnd));
        }
        return Optional.empty();
    }

    // ========== OPERATIONS ==========

    /**
     * Union de deux ranges (si adjacents ou overlapping).
     */
    public Optional<Range> union(Range other) {
        if (overlaps(other) || end + 1 == other.start || other.end + 1 == start) {
            return Optional.of(new Range(
                Math.min(start, other.start),
                Math.max(end, other.end)
            ));
        }
        return Optional.empty();
    }

    /**
     * Soustraction: this - other.
     * Retourne 0, 1 ou 2 ranges.
     */
    public List<Range> subtract(Range other) {
        if (!overlaps(other)) {
            return List.of(this);
        }

        List<Range> result = new ArrayList<>();

        // Partie avant other
        if (start < other.start) {
            result.add(new Range(start, other.start - 1));
        }

        // Partie après other
        if (end > other.end) {
            result.add(new Range(other.end + 1, end));
        }

        return result;
    }

    /**
     * Shift le range par un delta.
     */
    public Range shift(long delta) {
        return new Range(start + delta, end + delta);
    }

    /**
     * Split le range en parties à l'intérieur et à l'extérieur de 'other'.
     * Retourne [inside, outside...] où inside peut être null.
     */
    public RangeSplit splitBy(Range other) {
        Optional<Range> inside = intersection(other);
        List<Range> outside = subtract(other);
        return new RangeSplit(inside.orElse(null), outside);
    }

    // ========== COMPARAISON ==========

    @Override
    public int compareTo(Range other) {
        int cmp = Long.compare(start, other.start);
        return cmp != 0 ? cmp : Long.compare(end, other.end);
    }

    // ========== MERGE UTILITIES ==========

    /**
     * Fusionne une liste de ranges (combine les overlapping/adjacent).
     */
    public static List<Range> merge(List<Range> ranges) {
        if (ranges.isEmpty()) return List.of();

        List<Range> sorted = new ArrayList<>(ranges);
        sorted.sort(Comparator.naturalOrder());

        List<Range> merged = new ArrayList<>();
        Range current = sorted.get(0);

        for (int i = 1; i < sorted.size(); i++) {
            Range next = sorted.get(i);
            Optional<Range> union = current.union(next);
            if (union.isPresent()) {
                current = union.get();
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        return merged;
    }

    /**
     * Calcule la couverture totale (somme des longueurs après merge).
     */
    public static long totalCoverage(List<Range> ranges) {
        return merge(ranges).stream()
            .mapToLong(Range::length)
            .sum();
    }

    @Override
    public String toString() {
        return "[" + start + ".." + end + "]";
    }

    // ========== HELPER CLASSES ==========

    public record RangeSplit(Range inside, List<Range> outside) {
        public boolean hasInside() {
            return inside != null;
        }
    }
}
