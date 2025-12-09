package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Advent of Code 2025 - Day 7: Laboratories
 * https://adventofcode.com/2025/day/7
 *
 * Puzzle: Faisceau tachyon qui descend depuis 'S'.
 * Les splitters (^) divisent le faisceau en deux (gauche et droite).
 * Compter le nombre de splits.
 */
public class Day07 {

    public static void main(String[] args) {
        List<String> lines = InputReader.readDay(7);

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

        // Trouver le point de départ 'S'
        Point2D start = grid.find('S').orElseThrow();

        // Trouver tous les splitters '^'
        List<Point2D> splitters = grid.findAll('^');

        Set<Point2D> beamPositions = new HashSet<Point2D>();
        beamPositions.add(start);
        
        int result = 0;
        for (int row=start.y+1;row<grid.height;row++) {
        	beamPositions = beamPositions.stream().map(b -> b.move(Direction.DOWN)).collect(Collectors.toSet());
        	 Set<Point2D> newBeamPositions = new HashSet<Point2D>();
        	for (Point2D beamPosition : beamPositions) {
				boolean split = splitters.stream().anyMatch(s -> s.compareTo(beamPosition) == 0);
				if (split) {
					result++;
					newBeamPositions.addAll(grid.getNeighbors8(beamPosition).stream().filter(p -> p.y == beamPosition.y).collect(Collectors.toSet()));
				}
				else {
					newBeamPositions.add(beamPosition);
				}
			}
        	beamPositions = newBeamPositions;
        }
        
        return result;
    }

    private static long solvePart2(List<String> lines) {
        Grid<Character> grid = Grid.fromLines(lines);

        Point2D start = grid.find('S').orElseThrow();
        Set<Point2D> splitters = new HashSet<>(grid.findAll('^'));
        Map<Point2D, Long> cache = new HashMap<>();

        return countTimelines(start, grid, splitters, cache);
    }

    private static long countTimelines(Point2D pos, Grid<Character> grid, Set<Point2D> splitters, Map<Point2D, Long> cache) {
        // Sortie de la grille = 1 timeline terminée
        if (!grid.isInBounds(pos)) {
            return 1;
        }

        // Cache hit
        if (cache.containsKey(pos)) {
            return cache.get(pos);
        }

        Point2D next = pos.move(Direction.DOWN);

        // Sortie par le bas = 1 timeline terminée
        if (!grid.isInBounds(next)) {
            return 1;
        }

        long result;
        if (splitters.contains(next)) {
            // Split : somme des timelines gauche + droite
            result = countTimelines(next.add(-1, 0), grid, splitters, cache)
                   + countTimelines(next.add(1, 0), grid, splitters, cache);
        } else {
            result = countTimelines(next, grid, splitters, cache);
        }

        cache.put(pos, result);
        return result;
    }
}
