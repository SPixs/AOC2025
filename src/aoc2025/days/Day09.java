package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Advent of Code 2025 - Day 9: Movie Theater
 * https://adventofcode.com/2025/day/9
 *
 * Puzzle: Trouver le plus grand rectangle ayant deux tuiles rouges comme coins opposés.
 * L'aire = |x2-x1| * |y2-y1| pour deux coins (x1,y1) et (x2,y2).
 */
public class Day09 {

    public static void main(String[] args) {
        List<String> lines = InputReader.readDay(9);

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
        List<Point2D> redTiles = lines.stream()
            .map(Point2D::parse)
            .toList();

        long maxArea = 0;
        for (int i = 0; i < redTiles.size(); i++) {
            for (int j = i + 1; j < redTiles.size(); j++) {
                long area = (long)(Math.abs(redTiles.get(i).x - redTiles.get(j).x) + 1)
                        * (Math.abs(redTiles.get(i).y - redTiles.get(j).y) + 1);
                maxArea = Math.max(maxArea, area);
            }
        }
        return maxArea;
    }

    private static long solvePart2(List<String> lines) {
        List<Point2D> redTiles = lines.stream()
            .map(Point2D::parse)
            .toList();

        List<Segment> polygonEdges = new ArrayList<>();
        for (int i = 0; i < redTiles.size(); i++) {
            Point2D from = redTiles.get(i);
            Point2D to = redTiles.get((i + 1) % redTiles.size());
            polygonEdges.add(new Segment(from, to));
        }

        long maxArea = 0;

        for (int i = 0; i < redTiles.size(); i++) {
            for (int j = i + 1; j < redTiles.size(); j++) {
                Point2D a = redTiles.get(i);
                Point2D b = redTiles.get(j);

                // Vérifier les 4 coins
                boolean cornersInside = isInsidePolygon(a, polygonEdges)
                    && isInsidePolygon(b, polygonEdges)
                    && isInsidePolygon(new Point2D(a.x, b.y), polygonEdges)
                    && isInsidePolygon(new Point2D(b.x, a.y), polygonEdges);

                if (cornersInside) {
                    int minX = Math.min(a.x, b.x);
                    int maxX = Math.max(a.x, b.x);
                    int minY = Math.min(a.y, b.y);
                    int maxY = Math.max(a.y, b.y);

                    // Vérifier qu'aucun segment ne traverse strictement l'intérieur
                    boolean hasSegmentInside = polygonEdges.stream()
                        .anyMatch(seg -> segmentStrictlyInsideRect(seg, minX, maxX, minY, maxY));

                    if (!hasSegmentInside) {
                        long area = (long)(maxX - minX + 1) * (maxY - minY + 1);
                        maxArea = Math.max(maxArea, area);
                    }
                }
            }
        }
        return maxArea;
    }

    private static boolean isInsidePolygon(Point2D p, List<Segment> edges) {
        // D'abord vérifier si le point est sur un bord
        for (Segment seg : edges) {
            if (seg.isVertical() && seg.from.x == p.x && seg.rangeY().contains(p.y)) {
                return true;
            }
            if (seg.isHorizontal() && seg.from.y == p.y && seg.rangeX().contains(p.x)) {
                return true;
            }
        }

        // Raycasting pour les points strictement à l'intérieur
        int intersections = 0;
        for (Segment seg : edges) {
            if (seg.isVertical() && seg.from.x > p.x
                && p.y > seg.minY() && p.y <= seg.maxY()) {
                intersections++;
            }
        }
        return intersections % 2 == 1;
    }

    private static boolean segmentStrictlyInsideRect(Segment seg, int minX, int maxX, int minY, int maxY) {
        // Rectangle trop petit pour avoir un intérieur strict
        if (maxX - minX < 2 || maxY - minY < 2) {
            return false;
        }

        // Intervalles stricts (exclusifs des bords)
        Range innerX = new Range(minX + 1, maxX - 1);
        Range innerY = new Range(minY + 1, maxY - 1);

        if (seg.isVertical() && innerX.contains(seg.from.x)) {
            return new Range(seg.minY(), seg.maxY()).overlaps(innerY);
        }

        if (seg.isHorizontal() && innerY.contains(seg.from.y)) {
            return new Range(seg.minX(), seg.maxX()).overlaps(innerX);
        }

        return false;
    }

    record Segment(Point2D from, Point2D to) {
        boolean isHorizontal() { return from.y == to.y; }
        boolean isVertical() { return from.x == to.x; }
        int minX() { return Math.min(from.x, to.x); }
        int maxX() { return Math.max(from.x, to.x); }
        int minY() { return Math.min(from.y, to.y); }
        int maxY() { return Math.max(from.y, to.y); }
        Range rangeX() { return new Range(minX(), maxX()); }
        Range rangeY() { return new Range(minY(), maxY()); }
    }
}
