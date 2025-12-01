package aoc2025.utils;

/**
 * Enum pour les directions cardinales.
 * Pattern récurrent dans les puzzles de grilles/labyrinthes.
 */
public enum Direction {
    UP(0, -1, '^'),
    RIGHT(1, 0, '>'),
    DOWN(0, 1, 'v'),
    LEFT(-1, 0, '<');

    public final int dx;
    public final int dy;
    public final char symbol;

    Direction(int dx, int dy, char symbol) {
        this.dx = dx;
        this.dy = dy;
        this.symbol = symbol;
    }

    // Rotation horaire (90° droite)
    public Direction turnRight() {
        return values()[(ordinal() + 1) % 4];
    }

    // Rotation anti-horaire (90° gauche)
    public Direction turnLeft() {
        return values()[(ordinal() + 3) % 4];
    }

    // Demi-tour (180°)
    public Direction opposite() {
        return values()[(ordinal() + 2) % 4];
    }

    // Mouvement depuis un point
    public Point2D move(Point2D p) {
        return new Point2D(p.x + dx, p.y + dy);
    }

    public Point2D move(Point2D p, int steps) {
        return new Point2D(p.x + dx * steps, p.y + dy * steps);
    }

    // Parsing depuis caractère
    public static Direction fromChar(char c) {
        return switch (c) {
            case '^', 'U', 'N' -> UP;
            case '>', 'R', 'E' -> RIGHT;
            case 'v', 'D', 'S' -> DOWN;
            case '<', 'L', 'W' -> LEFT;
            default -> throw new IllegalArgumentException("Unknown direction: " + c);
        };
    }

    // Parsing depuis string
    public static Direction fromString(String s) {
        return switch (s.toUpperCase()) {
            case "UP", "U", "NORTH", "N" -> UP;
            case "RIGHT", "R", "EAST", "E" -> RIGHT;
            case "DOWN", "D", "SOUTH", "S" -> DOWN;
            case "LEFT", "L", "WEST", "W" -> LEFT;
            default -> throw new IllegalArgumentException("Unknown direction: " + s);
        };
    }

    // Vecteur comme Point2D
    public Point2D toVector() {
        return new Point2D(dx, dy);
    }

    // Est horizontal ?
    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    // Est vertical ?
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }
}
