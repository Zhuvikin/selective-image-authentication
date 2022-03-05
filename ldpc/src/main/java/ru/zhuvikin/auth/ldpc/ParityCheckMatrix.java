package ru.zhuvikin.auth.ldpc;

import ru.zhuvikin.auth.matrix.sparse.Element;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ParityCheckMatrix {

    private final static int MAX_ELIMINATING_PASSES = 10;
    private final static int CHECKS_IN_COLUMN = 3;

    static Matrix generate(int length, int rank) {
        return generate(length, rank, new Random().nextLong());
    }

    public static Matrix generate(int length, int rank, Long seed) {
        Random random = new Random(seed);

        Matrix result = new Modulo2Matrix(length, rank);

        int totalCheckBits = CHECKS_IN_COLUMN * length;

        List<Integer> u = IntStream.range(0, totalCheckBits)
                .map(i -> i % rank)
                .boxed()
                .collect(Collectors.toList());

        int iteration = 0;
        for (int x = 0; x < length; x++) {
            List<Integer> columnIndicies = new ArrayList<>();
            for (int found = 0; found < CHECKS_IN_COLUMN; found++) {
                int y = iteration;
                while (y < totalCheckBits && result.isSet(x, u.get(y))) {
                    y++;
                }
                if (y == totalCheckBits) {
                    throw new RuntimeException("Had to place checks in rows unevenly");
                } else {
                    do {
                        y = iteration + random.nextInt(totalCheckBits - iteration + 1);
                    } while (result.isSet(x, u.get(y - 1)));

                    columnIndicies.add(u.get(y - 1));
                    u.set(y - 1, u.get(iteration));
                    iteration++;
                }
            }
            for (Integer item : columnIndicies) {
                result.set(x, item);
            }
        }

        // Add extra bits to avoid rows with less than two checks.
        int needed = 2;
        final int[] added = {0};
        IntStream.range(0, rank).parallel().filter(y -> result.getRows().get(y).size() < needed)
                .forEach(y -> {
                    System.out.println("y = " + y);
                    while (result.getRows().get(y).size() < needed) {
                        result.set(random.nextInt(rank), y);
                        added[0]++;
                    }
                });

        if (added[0] > 0) {
            System.err.println("Added " + added[0] + " extra bit-checks to make row counts at least two");
        }

        // Remove 4-length cycles
        for (int iter = 0; iter < MAX_ELIMINATING_PASSES; iter++) {
            int found = 0;
            for (int x = 0; x < length; x++) {
                boolean d = false;
                for (Element e1 = result.firstInColumn(x); !d && e1.bottom() != null; e1 = e1.bottom()) {
                    for (Element e2 = result.firstInRow(e1.getRow()); !d && e2.right() != null; e2 = e2.right()) {
                        if (e1.equals(e2)) {
                            continue;
                        }
                        for (Element e3 = result.firstInColumn(e2.getColumn()); !d && e3.bottom() != null; e3 = e3.bottom()) {
                            if (e2.equals(e3)) {
                                continue;
                            }
                            for (Element e4 = result.firstInRow(e3.getRow()); !d && e4.right() != null; e4 = e4.right()) {
                                if (e4.getColumn() == x) {
                                    int y;
                                    do {
                                        y = random.nextInt(rank);
                                    } while (result.isSet(x, y));
                                    result.remove(e1.getColumn(), e1.getRow());
                                    result.set(x, y);
                                    found++;
                                    d = true;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("found 4-cycles: " + found);
            if (found == 0) break;
        }

        System.out.println("Parity-check matrix is generated");
        return result;
    }

}
