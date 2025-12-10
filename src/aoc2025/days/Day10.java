package aoc2025.days;

import aoc2025.utils.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

/**
 * Advent of Code 2025 - Day 10: Factory
 * Part 1: Toggle lights (XOR) - BFS on bitmask states
 * Part 2: Increment counters - RREF + free variable enumeration
 */
public class Day10 {

    public static void main(String[] args) {
        String inputFile = args.length > 0 && args[0].equals("example")
            ? "input/input_day10_example.txt"
            : "input/input_day10.txt";
        List<String> lines = InputReader.readLines(inputFile);

        long start = System.nanoTime();
        long result1 = solvePart1(lines);
        System.out.println("Result part 1 : " + result1 + " in " +
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");

        start = System.nanoTime();
        long result2 = solvePart2(lines);
        System.out.println("Result part 2 : " + result2 + " in " +
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    // ==================== PART 1 ====================

    private static long solvePart1(List<String> lines) {
        return lines.stream().mapToLong(line -> findMinPresses(Machine.parse(line))).sum();
    }

    private static int findMinPresses(Machine m) {
        Queue<int[]> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(new int[]{0, 0});
        visited.add(0);

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            if (cur[0] == m.target) return cur[1];
            for (int btn : m.buttons) {
                int next = cur[0] ^ btn;
                if (visited.add(next)) queue.add(new int[]{next, cur[1] + 1});
            }
        }
        return -1;
    }

    record Machine(int target, List<Integer> buttons) {
        static Machine parse(String line) {
            Matcher m = Pattern.compile("\\[([.#]+)\\]").matcher(line);
            m.find();
            String pattern = m.group(1);
            int target = 0;
            for (int i = 0; i < pattern.length(); i++)
                if (pattern.charAt(i) == '#') target |= (1 << i);

            List<Integer> buttons = new ArrayList<>();
            Matcher bm = Pattern.compile("\\(([0-9,]+)\\)").matcher(line);
            while (bm.find()) {
                int mask = 0;
                for (int idx : InputReader.extractInts(bm.group(1))) mask |= (1 << idx);
                buttons.add(mask);
            }
            return new Machine(target, buttons);
        }
    }

    // ==================== PART 2 ====================

    private static long solvePart2(List<String> lines) {
        return lines.stream().mapToLong(line -> solveJoltage(JoltageMachine.parse(line))).sum();
    }

    private static long solveJoltage(JoltageMachine m) {
        int nBtn = m.buttons.size(), nCtr = m.targets.length;

        // Build augmented matrix [A|b] and do RREF
        double[][] aug = new double[nCtr][nBtn + 1];
        for (int j = 0; j < nBtn; j++)
            for (int i : m.buttons.get(j)) aug[i][j] = 1;
        for (int i = 0; i < nCtr; i++) aug[i][nBtn] = m.targets[i];

        // RREF with pivot tracking
        int[] pivotCols = new int[nCtr];
        int rank = 0;
        for (int col = 0; col < nBtn && rank < nCtr; col++) {
            int pivot = -1;
            for (int row = rank; row < nCtr; row++)
                if (Math.abs(aug[row][col]) > 1e-9) { pivot = row; break; }
            if (pivot == -1) continue;

            double[] tmp = aug[rank]; aug[rank] = aug[pivot]; aug[pivot] = tmp;
            double s = aug[rank][col];
            for (int j = 0; j <= nBtn; j++) aug[rank][j] /= s;
            for (int row = 0; row < nCtr; row++)
                if (row != rank && Math.abs(aug[row][col]) > 1e-9) {
                    double f = aug[row][col];
                    for (int j = 0; j <= nBtn; j++) aug[row][j] -= f * aug[rank][j];
                }
            pivotCols[rank++] = col;
        }

        // Identify free columns
        boolean[] isPivot = new boolean[nBtn];
        for (int i = 0; i < rank; i++) isPivot[pivotCols[i]] = true;
        int[] freeCols = new int[nBtn - rank];
        int f = 0;
        for (int c = 0; c < nBtn; c++) if (!isPivot[c]) freeCols[f++] = c;

        int maxVal = 0;
        for (int t : m.targets) maxVal = Math.max(maxVal, t);

        return searchFreeVars(aug, pivotCols, freeCols, rank, nBtn, maxVal, m);
    }

    private static long searchFreeVars(double[][] aug, int[] pivotCols, int[] freeCols,
                                       int rank, int nBtn, int maxVal, JoltageMachine m) {
        int numFree = freeCols.length;
        if (numFree == 0) {
            long[] x = new long[nBtn];
            for (int i = 0; i < rank; i++) {
                x[pivotCols[i]] = Math.round(aug[i][nBtn]);
                if (x[pivotCols[i]] < 0) return -1;
            }
            return check(x, m) ? sum(x) : -1;
        }

        long best = Long.MAX_VALUE;
        int range = numFree == 1 ? maxVal : (numFree == 2 ? Math.min(maxVal, 150) : Math.min(maxVal, 60));
        int[] freeVals = new int[numFree];

        while (true) {
            long[] x = new long[nBtn];
            boolean valid = true;
            for (int j = 0; j < numFree; j++) x[freeCols[j]] = freeVals[j];
            for (int i = 0; i < rank && valid; i++) {
                double val = aug[i][nBtn];
                for (int j = 0; j < numFree; j++) val -= aug[i][freeCols[j]] * freeVals[j];
                x[pivotCols[i]] = Math.round(val);
                if (x[pivotCols[i]] < 0) valid = false;
            }

            if (valid && check(x, m)) {
                long s = sum(x);
                if (s < best) best = s;
            }

            int idx = 0;
            while (idx < numFree) {
                if (++freeVals[idx] <= range) break;
                freeVals[idx++] = 0;
            }
            if (idx == numFree) break;
        }
        return best == Long.MAX_VALUE ? -1 : best;
    }

    private static long sum(long[] x) {
        long s = 0; for (long v : x) s += v; return s;
    }

    private static boolean check(long[] x, JoltageMachine m) {
        long[] counters = new long[m.targets.length];
        for (int btn = 0; btn < x.length; btn++) {
            if (x[btn] < 0) return false;
            for (int idx : m.buttons.get(btn)) counters[idx] += x[btn];
        }
        for (int i = 0; i < counters.length; i++)
            if (counters[i] != m.targets[i]) return false;
        return true;
    }

    record JoltageMachine(int[] targets, List<List<Integer>> buttons) {
        static JoltageMachine parse(String line) {
            Matcher m = Pattern.compile("\\{([0-9,]+)\\}").matcher(line);
            m.find();
            int[] targets = InputReader.extractInts(m.group(1)).stream()
                .mapToInt(Integer::intValue).toArray();

            List<List<Integer>> buttons = new ArrayList<>();
            Matcher bm = Pattern.compile("\\(([0-9,]+)\\)").matcher(line);
            while (bm.find()) buttons.add(InputReader.extractInts(bm.group(1)));
            return new JoltageMachine(targets, buttons);
        }
    }
}
