package aoc2025.utils;

import java.util.*;

/**
 * Utilitaires pour manipulation de strings.
 */
public class StringUtils {

    // ========== COUNTING ==========

    /**
     * Compte les occurrences d'un caractère.
     */
    public static long count(String s, char c) {
        return s.chars().filter(ch -> ch == c).count();
    }

    /**
     * Compte les occurrences d'une substring.
     */
    public static int count(String s, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = s.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * Compte les occurrences (avec overlap).
     */
    public static int countOverlapping(String s, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = s.indexOf(sub, idx)) != -1) {
            count++;
            idx++;
        }
        return count;
    }

    // ========== TRANSFORMATION ==========

    /**
     * Inverse une string.
     */
    public static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    /**
     * Rote une string de n positions vers la gauche.
     */
    public static String rotateLeft(String s, int n) {
        n = n % s.length();
        return s.substring(n) + s.substring(0, n);
    }

    /**
     * Rote une string de n positions vers la droite.
     */
    public static String rotateRight(String s, int n) {
        return rotateLeft(s, s.length() - (n % s.length()));
    }

    /**
     * Pad à gauche jusqu'à une longueur donnée.
     */
    public static String padLeft(String s, int length, char padChar) {
        if (s.length() >= length) return s;
        return String.valueOf(padChar).repeat(length - s.length()) + s;
    }

    /**
     * Pad à droite jusqu'à une longueur donnée.
     */
    public static String padRight(String s, int length, char padChar) {
        if (s.length() >= length) return s;
        return s + String.valueOf(padChar).repeat(length - s.length());
    }

    // ========== ANALYSIS ==========

    /**
     * Fréquence de chaque caractère.
     */
    public static Map<Character, Long> charFrequency(String s) {
        Map<Character, Long> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.merge(c, 1L, Long::sum);
        }
        return freq;
    }

    /**
     * Caractère le plus fréquent.
     */
    public static char mostCommon(String s) {
        return charFrequency(s).entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElseThrow();
    }

    /**
     * Caractère le moins fréquent.
     */
    public static char leastCommon(String s) {
        return charFrequency(s).entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElseThrow();
    }

    /**
     * Vérifie si tous les caractères sont uniques.
     */
    public static boolean allUnique(String s) {
        return s.chars().distinct().count() == s.length();
    }

    /**
     * Vérifie si c'est un palindrome.
     */
    public static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) return false;
        }
        return true;
    }

    // ========== SPLITTING ==========

    /**
     * Split en chunks de taille fixe.
     */
    public static List<String> chunk(String s, int size) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < s.length(); i += size) {
            chunks.add(s.substring(i, Math.min(i + size, s.length())));
        }
        return chunks;
    }

    /**
     * Sliding window sur une string.
     */
    public static List<String> windows(String s, int size) {
        List<String> windows = new ArrayList<>();
        for (int i = 0; i <= s.length() - size; i++) {
            windows.add(s.substring(i, i + size));
        }
        return windows;
    }

    // ========== CONVERSION ==========

    /**
     * String vers liste de caractères.
     */
    public static List<Character> toCharList(String s) {
        return s.chars().mapToObj(c -> (char) c).toList();
    }

    /**
     * Liste de caractères vers string.
     */
    public static String fromCharList(List<Character> chars) {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) sb.append(c);
        return sb.toString();
    }

    /**
     * String binaire vers long.
     */
    public static long binaryToLong(String binary) {
        return Long.parseLong(binary, 2);
    }

    /**
     * Long vers string binaire.
     */
    public static String toBinary(long value) {
        return Long.toBinaryString(value);
    }

    /**
     * Long vers string binaire avec padding.
     */
    public static String toBinary(long value, int bits) {
        return padLeft(Long.toBinaryString(value), bits, '0');
    }

    // ========== DISTANCE ==========

    /**
     * Distance de Hamming (nombre de positions différentes).
     */
    public static int hammingDistance(String a, String b) {
        if (a.length() != b.length()) {
            throw new IllegalArgumentException("Strings must have equal length");
        }
        int dist = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) dist++;
        }
        return dist;
    }

    /**
     * Distance de Levenshtein (edit distance).
     */
    public static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
}
