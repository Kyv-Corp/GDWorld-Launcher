package pro.gravit.launcher.gui.components;

import java.util.*;

/** Small deterministic game rules, independent of JavaFX and the downloader. */
final class LoadingGameModels {
    record Cell(int x, int y) {}

    static int chooseDifferent(Random random, int previous, int count) {
        if (count < 2) return 0;
        if (previous < 0 || previous >= count) return random.nextInt(count);
        int next = random.nextInt(count - 1);
        return next >= previous ? next + 1 : next;
    }

    static final class Snake {
        final int size;
        final Random random;
        final List<Cell> body = new ArrayList<>();
        Cell food;
        int dx = 1, dy = 0, nextDx = 1, nextDy = 0, score;
        boolean over, won;

        Snake(int size, Random random) {
            this.size = size; this.random = random;
            for (int x = 5; x >= 2; x--) body.add(new Cell(x, size / 2));
            placeFood();
        }
        void turn(int x, int y) {
            if (Math.abs(x) + Math.abs(y) != 1 || x == -dx && y == -dy) return;
            nextDx = x; nextDy = y;
        }
        void step() {
            if (over) return;
            dx = nextDx; dy = nextDy;
            Cell head = body.getFirst(), next = new Cell(head.x + dx, head.y + dy);
            boolean grow = next.equals(food);
            int occupied = body.size() - (grow ? 0 : 1);
            if (next.x < 0 || next.y < 0 || next.x >= size || next.y >= size || body.subList(0, occupied).contains(next)) {
                over = true; return;
            }
            body.addFirst(next);
            if (grow) { score++; placeFood(); }
            else body.removeLast();
        }
        private void placeFood() {
            List<Cell> free = new ArrayList<>();
            for (int y = 0; y < size; y++) for (int x = 0; x < size; x++) {
                Cell cell = new Cell(x, y); if (!body.contains(cell)) free.add(cell);
            }
            if (free.isEmpty()) { food = null; over = won = true; }
            else food = free.get(random.nextInt(free.size()));
        }
    }

    static final class Mines {
        final int size, bombs;
        final Random random;
        final boolean[][] mines, opened, flags;
        boolean generated, over, won;
        int openCount, flagCount;

        Mines(int size, int bombs, Random random) {
            if (size < 4 || bombs < 1 || bombs > size * size - 9) throw new IllegalArgumentException("Mine field size");
            this.size = size; this.bombs = bombs; this.random = random;
            mines = new boolean[size][size]; opened = new boolean[size][size]; flags = new boolean[size][size];
        }
        boolean inside(int x, int y) { return x >= 0 && y >= 0 && x < size && y < size; }
        private void generate(int sx, int sy) {
            List<Cell> choices = new ArrayList<>();
            for (int y = 0; y < size; y++) for (int x = 0; x < size; x++)
                if (Math.abs(x - sx) > 1 || Math.abs(y - sy) > 1) choices.add(new Cell(x, y));
            Collections.shuffle(choices, random);
            for (int i = 0; i < bombs; i++) { Cell c = choices.get(i); mines[c.y][c.x] = true; }
            generated = true;
        }
        int around(int x, int y) {
            int count = 0;
            for (int yy = y - 1; yy <= y + 1; yy++) for (int xx = x - 1; xx <= x + 1; xx++)
                if (inside(xx, yy) && mines[yy][xx]) count++;
            return count;
        }
        void flag(int x, int y) {
            if (!inside(x, y) || over || opened[y][x] || !flags[y][x] && flagCount >= bombs) return;
            flags[y][x] = !flags[y][x]; flagCount += flags[y][x] ? 1 : -1;
        }
        void reveal(int x, int y) {
            if (!inside(x, y) || over || flags[y][x] || opened[y][x]) return;
            if (!generated) generate(x, y);
            if (mines[y][x]) { opened[y][x] = true; over = true; return; }
            ArrayDeque<Cell> queue = new ArrayDeque<>(); queue.add(new Cell(x, y));
            while (!queue.isEmpty()) {
                Cell c = queue.removeFirst();
                if (!inside(c.x, c.y) || opened[c.y][c.x] || flags[c.y][c.x] || mines[c.y][c.x]) continue;
                opened[c.y][c.x] = true; openCount++;
                if (around(c.x, c.y) == 0)
                    for (int yy = c.y - 1; yy <= c.y + 1; yy++) for (int xx = c.x - 1; xx <= c.x + 1; xx++)
                        if (inside(xx, yy) && !opened[yy][xx]) queue.addLast(new Cell(xx, yy));
            }
            if (openCount == size * size - bombs) over = won = true;
        }
    }

    static final class Memory {
        final int[] cards = new int[12];
        final boolean[] matched = new boolean[12];
        int first = -1, second = -1, pairs, moves;
        double remaining;

        Memory(Random random) {
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < 6; i++) { values.add(i); values.add(i); }
            Collections.shuffle(values, random);
            for (int i = 0; i < cards.length; i++) cards[i] = values.get(i);
        }
        void reveal(int index) {
            if (index < 0 || index >= cards.length || matched[index] || index == first || second >= 0 || won()) return;
            if (first < 0) { first = index; return; }
            second = index; moves++;
            if (cards[first] == cards[second]) {
                matched[first] = matched[second] = true; pairs++; first = second = -1;
            } else remaining = 700;
        }
        boolean tick(double ms) {
            if (second < 0) return false;
            remaining -= ms;
            if (remaining > 0) return false;
            first = second = -1; return true;
        }
        boolean faceUp(int index) { return matched[index] || first == index || second == index; }
        boolean won() { return pairs == 6; }
    }
}
