package aoc2025.utils;

import java.math.BigInteger;
import java.util.Collection;

/**
 * Utilitaires mathématiques pour AOC.
 * GCD, LCM, modular arithmetic, combinatorics.
 */
public class MathUtils {

    // ========== GCD / LCM ==========

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static long lcm(long a, long b) {
        return Math.abs(a / gcd(a, b) * b);
    }

    public static long gcd(long... values) {
        long result = values[0];
        for (int i = 1; i < values.length; i++) {
            result = gcd(result, values[i]);
        }
        return result;
    }

    public static long lcm(long... values) {
        long result = values[0];
        for (int i = 1; i < values.length; i++) {
            result = lcm(result, values[i]);
        }
        return result;
    }

    public static long lcm(Collection<Long> values) {
        return values.stream().reduce(1L, MathUtils::lcm);
    }

    // ========== MODULAR ARITHMETIC ==========

    /**
     * Modulo positif (contrairement à % qui peut être négatif).
     */
    public static long mod(long a, long m) {
        return ((a % m) + m) % m;
    }

    public static int mod(int a, int m) {
        return ((a % m) + m) % m;
    }

    /**
     * Exponentiation modulaire rapide: (base^exp) % mod
     */
    public static long modPow(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }
            exp >>= 1;
            base = (base * base) % mod;
        }
        return result;
    }

    /**
     * Inverse modulaire (pour mod premier).
     * a^(-1) mod p = a^(p-2) mod p (Fermat's little theorem)
     */
    public static long modInverse(long a, long p) {
        return modPow(a, p - 2, p);
    }

    /**
     * Extended Euclidean Algorithm.
     * Returns [gcd, x, y] where ax + by = gcd(a,b)
     */
    public static long[] extendedGcd(long a, long b) {
        if (b == 0) {
            return new long[]{a, 1, 0};
        }
        long[] result = extendedGcd(b, a % b);
        long gcd = result[0];
        long x = result[2];
        long y = result[1] - (a / b) * result[2];
        return new long[]{gcd, x, y};
    }

    // ========== CHINESE REMAINDER THEOREM ==========

    /**
     * Chinese Remainder Theorem pour 2 congruences.
     * Trouve x tel que x ≡ a1 (mod m1) et x ≡ a2 (mod m2)
     */
    public static long crt(long a1, long m1, long a2, long m2) {
        long[] ext = extendedGcd(m1, m2);
        long gcd = ext[0];
        if ((a2 - a1) % gcd != 0) {
            throw new IllegalArgumentException("No solution exists");
        }
        long lcm = m1 / gcd * m2;
        long x = mod(a1 + m1 * ((a2 - a1) / gcd * ext[1]), lcm);
        return x;
    }

    /**
     * CRT pour plusieurs congruences.
     */
    public static long crt(long[] remainders, long[] moduli) {
        long result = remainders[0];
        long mod = moduli[0];
        for (int i = 1; i < remainders.length; i++) {
            result = crt(result, mod, remainders[i], moduli[i]);
            mod = lcm(mod, moduli[i]);
        }
        return result;
    }

    // ========== COMBINATORICS ==========

    public static long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static BigInteger factorialBig(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    /**
     * Coefficient binomial C(n, k) = n! / (k! * (n-k)!)
     */
    public static long binomial(int n, int k) {
        if (k > n - k) k = n - k;
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }

    public static BigInteger binomialBig(int n, int k) {
        if (k > n - k) k = n - k;
        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < k; i++) {
            result = result.multiply(BigInteger.valueOf(n - i))
                          .divide(BigInteger.valueOf(i + 1));
        }
        return result;
    }

    // ========== LINEAR ALGEBRA ==========

    /**
     * Résout un système 2x2 par la règle de Cramer.
     * a1*x + b1*y = c1
     * a2*x + b2*y = c2
     * @return [x, y] ou null si pas de solution
     */
    public static long[] solveLinear2x2(long a1, long b1, long c1, long a2, long b2, long c2) {
        long det = a1 * b2 - a2 * b1;
        if (det == 0) return null;

        long detX = c1 * b2 - c2 * b1;
        long detY = a1 * c2 - a2 * c1;

        if (detX % det != 0 || detY % det != 0) {
            return null; // Pas de solution entière
        }

        return new long[]{detX / det, detY / det};
    }

    // ========== LINEAR PROGRAMMING (SIMPLEX) ==========

    /**
     * Résout un problème de programmation linéaire par la méthode du Simplex.
     * Minimise sum(x) sous les contraintes Ax = b, x >= 0.
     *
     * @param A matrice des contraintes (m lignes, n colonnes)
     * @param b vecteur des contraintes (m éléments)
     * @return solution optimale x (n éléments), ou null si pas de solution
     */
    public static double[] simplex(double[][] A, double[] b) {
        int m = A.length;      // contraintes
        int n = A[0].length;   // variables

        // Phase 1: Trouver une solution réalisable avec variables artificielles
        int totalVars = n + m;
        double[][] tableau = new double[m + 1][totalVars + 1];

        for (int i = 0; i < m; i++) {
            double sign = b[i] >= 0 ? 1 : -1;
            for (int j = 0; j < n; j++) {
                tableau[i][j] = sign * A[i][j];
            }
            tableau[i][n + i] = 1;
            tableau[i][totalVars] = sign * b[i];
        }

        // Objectif phase 1: minimiser sum des artificielles
        for (int i = 0; i < m; i++) {
            for (int j = 0; j <= totalVars; j++) {
                tableau[m][j] -= tableau[i][j];
            }
        }

        int[] basis = new int[m];
        for (int i = 0; i < m; i++) {
            basis[i] = n + i;
        }

        if (!runSimplex(tableau, basis, totalVars)) {
            return null;
        }

        // Vérifier infaisabilité en sommant les artificielles en base
        double artSum = 0;
        for (int i = 0; i < m; i++) {
            if (basis[i] >= n) {
                artSum += Math.abs(tableau[i][totalVars]);
            }
        }
        if (artSum > 1e-9) {
            return null; // Pas de solution réalisable (artificielles non nulles)
        }

        // Éliminer les artificielles de la base (même dégénérées)
        for (int i = 0; i < m; i++) {
            if (basis[i] >= n) {
                // Trouver une variable originale pour pivoter
                for (int j = 0; j < n; j++) {
                    if (Math.abs(tableau[i][j]) > 1e-9) {
                        // Pivot sur (i, j)
                        basis[i] = j;
                        double pivotVal = tableau[i][j];
                        for (int k = 0; k <= totalVars; k++) {
                            tableau[i][k] /= pivotVal;
                        }
                        for (int ii = 0; ii <= m; ii++) {
                            if (ii != i) {
                                double factor = tableau[ii][j];
                                for (int k = 0; k <= totalVars; k++) {
                                    tableau[ii][k] -= factor * tableau[i][k];
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        // Phase 2: Optimiser l'objectif original
        double[][] tableau2 = new double[m + 1][n + 1];
        for (int i = 0; i < m; i++) {
            System.arraycopy(tableau[i], 0, tableau2[i], 0, n);
            tableau2[i][n] = tableau[i][totalVars];
        }

        // Objectif: minimiser sum(x)
        for (int j = 0; j < n; j++) {
            tableau2[m][j] = 1.0;
        }

        // Éliminer les variables de base de l'objectif
        for (int i = 0; i < m; i++) {
            if (basis[i] < n) {
                double coef = tableau2[m][basis[i]];
                for (int j = 0; j <= n; j++) {
                    tableau2[m][j] -= coef * tableau2[i][j];
                }
            }
        }

        if (!runSimplex(tableau2, basis, n)) {
            return null;
        }

        double[] solution = new double[n];
        for (int i = 0; i < m; i++) {
            if (basis[i] < n) {
                solution[basis[i]] = tableau2[i][n];
            }
        }

        return solution;
    }

    /**
     * Simplex avec objectif personnalisé.
     * Minimise c'x sous les contraintes Ax = b, x >= 0.
     */
    public static double[] simplex(double[][] A, double[] b, double[] c) {
        int m = A.length;
        int n = A[0].length;

        int totalVars = n + m;
        double[][] tableau = new double[m + 1][totalVars + 1];

        for (int i = 0; i < m; i++) {
            double sign = b[i] >= 0 ? 1 : -1;
            for (int j = 0; j < n; j++) {
                tableau[i][j] = sign * A[i][j];
            }
            tableau[i][n + i] = 1;
            tableau[i][totalVars] = sign * b[i];
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j <= totalVars; j++) {
                tableau[m][j] -= tableau[i][j];
            }
        }

        int[] basis = new int[m];
        for (int i = 0; i < m; i++) {
            basis[i] = n + i;
        }

        if (!runSimplex(tableau, basis, totalVars)) {
            return null;
        }

        if (Math.abs(tableau[m][totalVars]) > 1e-9) {
            return null;
        }

        double[][] tableau2 = new double[m + 1][n + 1];
        for (int i = 0; i < m; i++) {
            System.arraycopy(tableau[i], 0, tableau2[i], 0, n);
            tableau2[i][n] = tableau[i][totalVars];
        }

        System.arraycopy(c, 0, tableau2[m], 0, n);

        for (int i = 0; i < m; i++) {
            if (basis[i] < n) {
                double coef = tableau2[m][basis[i]];
                for (int j = 0; j <= n; j++) {
                    tableau2[m][j] -= coef * tableau2[i][j];
                }
            }
        }

        if (!runSimplex(tableau2, basis, n)) {
            return null;
        }

        double[] solution = new double[n];
        for (int i = 0; i < m; i++) {
            if (basis[i] < n) {
                solution[basis[i]] = tableau2[i][n];
            }
        }

        return solution;
    }

    private static boolean runSimplex(double[][] tableau, int[] basis, int numVars) {
        int m = basis.length;
        int lastCol = tableau[0].length - 1;

        while (true) {
            int pivotCol = -1;
            double minVal = -1e-9;
            for (int j = 0; j < numVars; j++) {
                if (tableau[m][j] < minVal) {
                    minVal = tableau[m][j];
                    pivotCol = j;
                }
            }

            if (pivotCol == -1) {
                return true;
            }

            int pivotRow = -1;
            double minRatio = Double.MAX_VALUE;
            for (int i = 0; i < m; i++) {
                if (tableau[i][pivotCol] > 1e-9) {
                    double ratio = tableau[i][lastCol] / tableau[i][pivotCol];
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        pivotRow = i;
                    }
                }
            }

            if (pivotRow == -1) {
                return false;
            }

            basis[pivotRow] = pivotCol;
            double pivotVal = tableau[pivotRow][pivotCol];
            for (int j = 0; j <= lastCol; j++) {
                tableau[pivotRow][j] /= pivotVal;
            }

            for (int i = 0; i <= m; i++) {
                if (i != pivotRow) {
                    double factor = tableau[i][pivotCol];
                    for (int j = 0; j <= lastCol; j++) {
                        tableau[i][j] -= factor * tableau[pivotRow][j];
                    }
                }
            }
        }
    }

    // ========== NUMBER THEORY ==========

    public static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (long i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /**
     * Triangle number: 1 + 2 + ... + n = n*(n+1)/2
     */
    public static long triangleNumber(long n) {
        return n * (n + 1) / 2;
    }

    /**
     * Somme des carrés: 1² + 2² + ... + n²
     */
    public static long sumOfSquares(long n) {
        return n * (n + 1) * (2 * n + 1) / 6;
    }

    // ========== MISC ==========

    /**
     * Signe: -1, 0, ou 1
     */
    public static int sign(long n) {
        return Long.compare(n, 0);
    }

    public static int sign(int n) {
        return Integer.compare(n, 0);
    }

    /**
     * Clamp une valeur entre min et max
     */
    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
