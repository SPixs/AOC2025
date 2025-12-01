# Advent of Code 2025

Solutions Java pour [Advent of Code 2025](https://adventofcode.com/2025).

## Structure du projet

```
AOC2025/
├── src/
│   └── aoc2025/
│       ├── days/          # Solutions par jour (Day01.java, Day02.java, ...)
│       └── utils/         # Bibliothèque d'utilitaires réutilisables
├── input/                 # Fichiers input (input_day01.txt, ...)
├── templates/             # Template pour nouveau jour
├── newday.sh              # Script: créer nouveau jour
├── run.sh                 # Script: compiler et exécuter
└── README.md
```

## Quick Start

### 1. Créer les fichiers d'un nouveau jour

```bash
./newday.sh 1
```

Crée:
- `src/aoc2025/days/Day01.java` (depuis template)
- `input/input_day01.txt` (vide)

### 2. Copier l'input

Coller votre puzzle input dans `input/input_day01.txt`

### 3. Implémenter la solution

Éditer `src/aoc2025/days/Day01.java`:
- `solvePart1()` pour la partie 1
- `solvePart2()` pour la partie 2

### 4. Exécuter

```bash
./run.sh 1
```

## Bibliothèque d'utilitaires

### Point2D / Point3D
Coordonnées 2D/3D avec opérations complètes.

```java
Point2D p = new Point2D(3, 4);
Point2D q = p.add(1, 2);              // (4, 6)
int dist = p.manhattanDistance(q);    // 3
List<Point2D> neighbors = p.getNeighbors4();  // 4 voisins cardinaux
Point2D moved = p.move(Direction.UP); // (3, 3)
```

### Direction
Enum pour les 4 directions cardinales.

```java
Direction dir = Direction.UP;
Direction right = dir.turnRight();    // RIGHT
Direction opposite = dir.opposite();  // DOWN
Point2D next = dir.move(p);           // Déplace p dans la direction
Direction parsed = Direction.fromChar('^');  // UP
```

### Grid<T>
Grille 2D générique avec parsing et recherche.

```java
// Depuis lignes de texte
Grid<Character> grid = Grid.fromLines(lines);
Grid<Integer> digits = Grid.fromDigits(lines);

// Accès
char c = grid.get(x, y);
grid.set(x, y, '#');

// Recherche
Point2D start = grid.find('S').orElseThrow();
List<Point2D> walls = grid.findAll('#');

// Navigation
List<Point2D> neighbors = grid.getNeighbors4(p);  // Voisins valides dans bounds
```

### Graph (Dijkstra, BFS, DFS, A*)
Algorithmes de graphes génériques.

```java
// Dijkstra avec état custom
long distance = Graph.dijkstra(
    startState,
    state -> state.equals(endState),           // Condition de fin
    state -> getNeighbors(state)               // Retourne List<Edge<State>>
);

// BFS (coût uniforme = 1)
long steps = Graph.bfs(
    start,
    pos -> pos.equals(end),
    pos -> pos.getNeighbors4().stream()
        .filter(grid::isInBounds)
        .toList()
);

// Flood fill
Set<Point2D> region = Graph.floodFill(grid, start, c -> c != '#');

// Tous les points atteignables avec distance
Map<Point2D, Long> distances = Graph.bfsAll(start, pos -> grid.getNeighbors4(pos));
```

### InputReader
Parsing des fichiers input.

```java
// Lecture
List<String> lines = InputReader.readDay(1);
String content = InputReader.readAll("input/input_day01.txt");

// Extraction de nombres
List<Integer> nums = InputReader.extractInts("x=42, y=-7");  // [42, -7]
int first = InputReader.extractInt(line);

// Parsing délimité
List<Integer> parts = InputReader.parseInts("1,2,3", ",");  // [1, 2, 3]

// Groupes séparés par lignes vides
List<List<String>> groups = InputReader.splitByEmptyLines(lines);

// Regex
InputReader.match(line, "(\\w+) (\\d+)").ifPresent(groups -> {
    String name = groups.get(0);
    int value = Integer.parseInt(groups.get(1));
});

// Grilles
char[][] charGrid = InputReader.parseCharGrid(lines);
int[][] digitGrid = InputReader.parseDigitGrid(lines);
```

### MathUtils
Fonctions mathématiques courantes.

```java
long g = MathUtils.gcd(48, 18);       // 6
long l = MathUtils.lcm(4, 6);         // 12
long l = MathUtils.lcm(4L, 6L, 8L);   // 24

// Modulo positif (jamais négatif)
int m = MathUtils.mod(-3, 5);         // 2 (pas -3)

// Exponentiation modulaire
long p = MathUtils.modPow(2, 10, 1000);  // 24

// Système linéaire 2x2 (règle de Cramer)
long[] xy = MathUtils.solveLinear2x2(a1, b1, c1, a2, b2, c2);

// Chinese Remainder Theorem
long x = MathUtils.crt(remainders, moduli);

// Combinatoire
long c = MathUtils.binomial(10, 3);   // 120
```

### Range
Manipulation d'intervalles.

```java
Range r1 = new Range(10, 20);         // [10..20] inclusif
Range r2 = Range.ofLength(10, 5);     // [10..14]

r1.contains(15);                      // true
r1.overlaps(r2);                      // true
r1.intersection(r2);                  // Optional<[10..14]>
r1.subtract(r2);                      // [[15..20]]
r1.shift(100);                        // [110..120]

// Fusion de ranges
List<Range> merged = Range.merge(ranges);
long coverage = Range.totalCoverage(ranges);
```

### Memo
Mémoïsation pour récursion.

```java
// Mémoïser une fonction
Function<Integer, Long> fib = Memo.memoize(n ->
    n <= 1 ? n : fib.apply(n-1) + fib.apply(n-2)
);

// Cache manuel
Memo.MemoCache<State, Long> cache = new Memo.MemoCache<>();
long result = cache.get(state, () -> expensiveComputation(state));
```

### StringUtils
Manipulation de strings.

```java
long count = StringUtils.count("hello", 'l');     // 2
String rev = StringUtils.reverse("hello");        // "olleh"
boolean unique = StringUtils.allUnique("abc");    // true
List<String> chunks = StringUtils.chunk("abcdef", 2);  // ["ab", "cd", "ef"]
List<String> windows = StringUtils.windows("abcd", 2); // ["ab", "bc", "cd"]
int dist = StringUtils.hammingDistance("abc", "axc");  // 1
```

## Patterns courants

### Grille avec pathfinding

```java
Grid<Character> grid = Grid.fromLines(lines);
Point2D start = grid.find('S').orElseThrow();
Point2D end = grid.find('E').orElseThrow();

long distance = Graph.bfs(
    start,
    p -> p.equals(end),
    p -> grid.getNeighbors4(p).stream()
        .filter(n -> grid.get(n) != '#')
        .toList()
);
```

### Dijkstra avec état complexe

```java
record State(Point2D pos, Direction dir) {}

long cost = Graph.dijkstra(
    new State(start, Direction.RIGHT),
    s -> s.pos().equals(end),
    s -> {
        List<Graph.Edge<State>> edges = new ArrayList<>();
        // Avancer
        Point2D next = s.dir().move(s.pos());
        if (grid.isInBounds(next) && grid.get(next) != '#') {
            edges.add(Graph.Edge.of(new State(next, s.dir()), 1));
        }
        // Tourner
        edges.add(Graph.Edge.of(new State(s.pos(), s.dir().turnLeft()), 1000));
        edges.add(Graph.Edge.of(new State(s.pos(), s.dir().turnRight()), 1000));
        return edges;
    }
);
```

### Récursion mémoïsée

```java
Map<State, Long> memo = new HashMap<>();

long solve(State state) {
    if (memo.containsKey(state)) return memo.get(state);

    // Calcul...
    long result = /* ... */;

    memo.put(state, result);
    return result;
}
```

## Exécution depuis Eclipse

1. Importer le projet: File → Import → Existing Projects into Workspace
2. Sélectionner le dossier `AOC2025`
3. Run As → Java Application sur le fichier `DayXX.java`

## Exécution en ligne de commande

```bash
# Compiler et exécuter
./run.sh 1

# Ou manuellement
cd src
javac aoc2025/utils/*.java aoc2025/days/Day01.java
cd ..
java -cp src aoc2025.days.Day01
```

## Tips

1. **Toujours lire l'énoncé attentivement** - les edge cases sont souvent dans les détails
2. **Tester avec l'exemple** avant l'input réel
3. **Part 2 change souvent l'échelle** - anticiper les besoins de performance
4. **Garder Part 1 fonctionnelle** quand vous travaillez sur Part 2
5. **Les utilitaires sont là pour aider** - ne pas réinventer la roue

## Ressources

- [Advent of Code 2025](https://adventofcode.com/2025)
- [Reddit r/adventofcode](https://www.reddit.com/r/adventofcode/)
- [AOC Subreddit Solutions Megathread](https://www.reddit.com/r/adventofcode/wiki/solution_megathreads)
