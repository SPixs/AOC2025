package aoc2025.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.BiFunction;

/**
 * Classe utilitaire pour les algorithmes de graphes.
 * Dijkstra, BFS, DFS, A* pour les puzzles de pathfinding.
 */
public class Graph {

    // ========== DIJKSTRA ==========

    /**
     * Dijkstra générique avec état custom.
     * @param start État initial
     * @param isEnd Prédicat pour détecter l'état final
     * @param neighbors Fonction retournant les voisins avec leur coût
     * @return Distance minimale ou -1 si pas de chemin
     */
    public static <T> long dijkstra(
            T start,
            java.util.function.Predicate<T> isEnd,
            Function<T, List<Edge<T>>> neighbors) {

        Map<T, Long> distances = new HashMap<>();
        PriorityQueue<State<T>> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.cost));

        queue.offer(new State<>(start, 0));
        distances.put(start, 0L);

        while (!queue.isEmpty()) {
            State<T> current = queue.poll();

            if (isEnd.test(current.node)) {
                return current.cost;
            }

            if (current.cost > distances.getOrDefault(current.node, Long.MAX_VALUE)) {
                continue;
            }

            for (Edge<T> edge : neighbors.apply(current.node)) {
                long newDist = current.cost + edge.cost;
                if (newDist < distances.getOrDefault(edge.to, Long.MAX_VALUE)) {
                    distances.put(edge.to, newDist);
                    queue.offer(new State<>(edge.to, newDist));
                }
            }
        }

        return -1; // Pas de chemin
    }

    /**
     * Dijkstra retournant le chemin complet.
     */
    public static <T> DijkstraResult<T> dijkstraWithPath(
            T start,
            java.util.function.Predicate<T> isEnd,
            Function<T, List<Edge<T>>> neighbors) {

        Map<T, Long> distances = new HashMap<>();
        Map<T, T> predecessors = new HashMap<>();
        PriorityQueue<State<T>> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.cost));

        queue.offer(new State<>(start, 0));
        distances.put(start, 0L);

        T endNode = null;

        while (!queue.isEmpty()) {
            State<T> current = queue.poll();

            if (isEnd.test(current.node)) {
                endNode = current.node;
                break;
            }

            if (current.cost > distances.getOrDefault(current.node, Long.MAX_VALUE)) {
                continue;
            }

            for (Edge<T> edge : neighbors.apply(current.node)) {
                long newDist = current.cost + edge.cost;
                if (newDist < distances.getOrDefault(edge.to, Long.MAX_VALUE)) {
                    distances.put(edge.to, newDist);
                    predecessors.put(edge.to, current.node);
                    queue.offer(new State<>(edge.to, newDist));
                }
            }
        }

        if (endNode == null) {
            return new DijkstraResult<>(-1, List.of());
        }

        // Reconstruire le chemin
        List<T> path = new ArrayList<>();
        T current = endNode;
        while (current != null) {
            path.add(current);
            current = predecessors.get(current);
        }
        Collections.reverse(path);

        return new DijkstraResult<>(distances.get(endNode), path);
    }

    // ========== BFS ==========

    /**
     * BFS pour trouver le plus court chemin (coût uniforme = 1).
     */
    public static <T> long bfs(
            T start,
            java.util.function.Predicate<T> isEnd,
            Function<T, List<T>> neighbors) {

        Set<T> visited = new HashSet<>();
        Queue<State<T>> queue = new LinkedList<>();

        queue.offer(new State<>(start, 0));
        visited.add(start);

        while (!queue.isEmpty()) {
            State<T> current = queue.poll();

            if (isEnd.test(current.node)) {
                return current.cost;
            }

            for (T neighbor : neighbors.apply(current.node)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(new State<>(neighbor, current.cost + 1));
                }
            }
        }

        return -1;
    }

    /**
     * BFS retournant tous les états atteignables avec leur distance.
     */
    public static <T> Map<T, Long> bfsAll(T start, Function<T, List<T>> neighbors) {
        Map<T, Long> distances = new HashMap<>();
        Queue<State<T>> queue = new LinkedList<>();

        queue.offer(new State<>(start, 0));
        distances.put(start, 0L);

        while (!queue.isEmpty()) {
            State<T> current = queue.poll();

            for (T neighbor : neighbors.apply(current.node)) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, current.cost + 1);
                    queue.offer(new State<>(neighbor, current.cost + 1));
                }
            }
        }

        return distances;
    }

    // ========== DFS ==========

    /**
     * DFS récursif avec tracking des visités.
     */
    public static <T> Set<T> dfsReachable(T start, Function<T, List<T>> neighbors) {
        Set<T> visited = new HashSet<>();
        dfsRecursive(start, neighbors, visited);
        return visited;
    }

    private static <T> void dfsRecursive(T node, Function<T, List<T>> neighbors, Set<T> visited) {
        if (visited.contains(node)) return;
        visited.add(node);
        for (T neighbor : neighbors.apply(node)) {
            dfsRecursive(neighbor, neighbors, visited);
        }
    }

    /**
     * DFS itératif avec stack.
     */
    public static <T> Set<T> dfsIterative(T start, Function<T, List<T>> neighbors) {
        Set<T> visited = new HashSet<>();
        Deque<T> stack = new ArrayDeque<>();

        stack.push(start);

        while (!stack.isEmpty()) {
            T current = stack.pop();
            if (visited.contains(current)) continue;
            visited.add(current);

            for (T neighbor : neighbors.apply(current)) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }

        return visited;
    }

    // ========== A* ==========

    /**
     * A* avec heuristique.
     */
    public static <T> long aStar(
            T start,
            java.util.function.Predicate<T> isEnd,
            Function<T, List<Edge<T>>> neighbors,
            Function<T, Long> heuristic) {

        Map<T, Long> gScore = new HashMap<>();
        PriorityQueue<State<T>> queue = new PriorityQueue<>(
            Comparator.comparingLong(s -> s.cost + heuristic.apply(s.node))
        );

        queue.offer(new State<>(start, 0));
        gScore.put(start, 0L);

        while (!queue.isEmpty()) {
            State<T> current = queue.poll();

            if (isEnd.test(current.node)) {
                return current.cost;
            }

            if (current.cost > gScore.getOrDefault(current.node, Long.MAX_VALUE)) {
                continue;
            }

            for (Edge<T> edge : neighbors.apply(current.node)) {
                long tentative = current.cost + edge.cost;
                if (tentative < gScore.getOrDefault(edge.to, Long.MAX_VALUE)) {
                    gScore.put(edge.to, tentative);
                    queue.offer(new State<>(edge.to, tentative));
                }
            }
        }

        return -1;
    }

    // ========== FLOOD FILL ==========

    /**
     * Flood fill pour trouver une région connectée.
     */
    public static <T> Set<Point2D> floodFill(
            Grid<T> grid,
            Point2D start,
            java.util.function.Predicate<T> canFill) {

        Set<Point2D> filled = new HashSet<>();
        Queue<Point2D> queue = new LinkedList<>();

        if (!canFill.test(grid.get(start))) return filled;

        queue.offer(start);
        filled.add(start);

        while (!queue.isEmpty()) {
            Point2D current = queue.poll();
            for (Point2D neighbor : grid.getNeighbors4(current)) {
                if (!filled.contains(neighbor) && canFill.test(grid.get(neighbor))) {
                    filled.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return filled;
    }

    // ========== HELPER CLASSES ==========

    public record Edge<T>(T to, long cost) {
        public static <T> Edge<T> of(T to, long cost) {
            return new Edge<>(to, cost);
        }

        public static <T> Edge<T> of(T to) {
            return new Edge<>(to, 1);
        }
    }

    public record State<T>(T node, long cost) {}

    public record DijkstraResult<T>(long distance, List<T> path) {
        public boolean found() {
            return distance >= 0;
        }
    }
}
