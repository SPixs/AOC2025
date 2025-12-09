package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Advent of Code 2025 - Day 8: Playground
 * https://adventofcode.com/2025/day/8
 *
 * Puzzle: Boîtes de jonction en 3D à connecter par distance euclidienne croissante.
 * Après 1000 connexions, multiplier les tailles des 3 plus grands circuits.
 */
public class Day08 {

    public static void main(String[] args) {
        List<String> lines = InputReader.readDay(8);

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
        // Parser les positions des boites
        List<Point3D> boxes = lines.stream()
            .map(Point3D::parse)
            .toList();

        // Calculer toutes les paires avec leur distance euclidienne
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            for (int j = i + 1; j < boxes.size(); j++) {
                double dist =  boxes.get(i).euclideanDistance(boxes.get(j));
                edges.add(new Edge(boxes.get(i), boxes.get(j), dist));
            }
        }

        // Trier par distance croissante
        edges.sort(Comparator.comparingDouble(e -> e.distance));

        List<Set<Point3D>> circuits = new ArrayList<Set<Point3D>>();
        for (Edge edge : edges.subList(0, 10)) {
            Set<Point3D> circuitFrom = null;
            Set<Point3D> circuitTo = null;
            for (Set<Point3D> circuit : circuits) {
                if (circuit.contains(edge.from)) circuitFrom = circuit;
                if (circuit.contains(edge.to)) circuitTo = circuit;
            }

            if (circuitFrom == null && circuitTo == null) {
                // Nouveau circuit avec les deux points
                Set<Point3D> newCircuit = new HashSet<>();
                newCircuit.add(edge.from);
                newCircuit.add(edge.to);
                circuits.add(newCircuit);
            } else if (circuitFrom == null) {
                // from rejoint le circuit de to
                circuitTo.add(edge.from);
            } else if (circuitTo == null) {
                // to rejoint le circuit de from
                circuitFrom.add(edge.to);
            } else if (circuitFrom != circuitTo) {
                // Fusion de deux circuits différents
                circuitFrom.addAll(circuitTo);
                circuits.remove(circuitTo);
            }
            // Si circuitFrom == circuitTo, rien à faire (déjà connectés)
        }
        
		Set<Set<Point3D>> circuitsOfBoxesWithoutCircuit = boxes.stream().filter(b -> {
			for (Set<Point3D> circuit : circuits) {
				if (circuit.contains(b))
					return false;
			}
			return true;
		}).map(b -> {
			HashSet<Point3D> circuit = new HashSet<Point3D>();
			circuit.add(b);
			return circuit;
		}).collect(Collectors.toSet());
        
		circuits.addAll(circuitsOfBoxesWithoutCircuit);
		
        Collections.sort(circuits, (c1, c2) -> -Integer.compare(c1.size(), c2.size()));
        
        System.out.println("Nb circuits créés : " + circuits.size());
        circuits.forEach(c -> System.out.println("Taille circuit : " + c.size()));
        
        // TODO: Connecter les 1000 paires les plus proches et calculer le produit des 3 plus grands circuits
        return 0;
    }

    private static long solvePart2(List<String> lines) {
        List<Point3D> junctions = lines.stream()
            .map(Point3D::parse)
            .toList();

        // TODO: Implémenter part 2
        return 0;
    }

    record Edge(Point3D from, Point3D to, double distance) {}

//    static class UnionFind {
//        private int[] parent;
//        private int[] size;
//
//        UnionFind(int n) {
//            parent = new int[n];
//            size = new int[n];
//            for (int i = 0; i < n; i++) {
//                parent[i] = i;
//                size[i] = 1;
//            }
//        }
//
//        int find(int x) {
//            if (parent[x] != x) {
//                parent[x] = find(parent[x]);  // Path compression
//            }
//            return parent[x];
//        }
//
//        boolean union(int x, int y) {
//            int rootX = find(x);
//            int rootY = find(y);
//            if (rootX == rootY) return false;  // Déjà dans le même circuit
//
//            // Union by size
//            if (size[rootX] < size[rootY]) {
//                parent[rootX] = rootY;
//                size[rootY] += size[rootX];
//            } else {
//                parent[rootY] = rootX;
//                size[rootX] += size[rootY];
//            }
//            return true;
//        }
//
//        int getSize(int x) {
//            return size[find(x)];
//        }
//
//        List<Integer> getAllSizes() {
//            Map<Integer, Integer> roots = new HashMap<>();
//            for (int i = 0; i < parent.length; i++) {
//                int root = find(i);
//                roots.put(root, size[root]);
//            }
//            return new ArrayList<>(roots.values());
//        }
//    }
}
