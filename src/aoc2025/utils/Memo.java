package aoc2025.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

/**
 * Utilitaires de mémoïsation pour les calculs récursifs.
 */
public class Memo {

    /**
     * Mémoïse une fonction à un argument.
     */
    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        Map<T, R> cache = new HashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }

    /**
     * Mémoïse une fonction à un argument (thread-safe).
     */
    public static <T, R> Function<T, R> memoizeConcurrent(Function<T, R> function) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }

    /**
     * Mémoïse une BiFunction.
     */
    public static <T, U, R> BiFunction<T, U, R> memoize(BiFunction<T, U, R> function) {
        Map<Pair<T, U>, R> cache = new HashMap<>();
        return (t, u) -> cache.computeIfAbsent(new Pair<>(t, u), p -> function.apply(p.first, p.second));
    }

    /**
     * Mémoïse une fonction récursive (passe le cache en paramètre).
     */
    public static <T, R> R computeWithMemo(T input, Map<T, R> cache, Function<T, R> compute) {
        if (cache.containsKey(input)) {
            return cache.get(input);
        }
        R result = compute.apply(input);
        cache.put(input, result);
        return result;
    }

    /**
     * Cache manuel réutilisable.
     */
    public static class MemoCache<K, V> {
        private final Map<K, V> cache = new HashMap<>();

        public V get(K key, Supplier<V> compute) {
            return cache.computeIfAbsent(key, k -> compute.get());
        }

        public V get(K key, Function<K, V> compute) {
            return cache.computeIfAbsent(key, compute);
        }

        public void put(K key, V value) {
            cache.put(key, value);
        }

        public boolean contains(K key) {
            return cache.containsKey(key);
        }

        public void clear() {
            cache.clear();
        }

        public int size() {
            return cache.size();
        }
    }

    /**
     * Paire générique pour clés de cache.
     */
    public record Pair<A, B>(A first, B second) {}

    /**
     * Triple générique pour clés de cache.
     */
    public record Triple<A, B, C>(A first, B second, C third) {}
}
