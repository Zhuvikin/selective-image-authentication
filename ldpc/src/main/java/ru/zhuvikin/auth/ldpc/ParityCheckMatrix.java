package ru.zhuvikin.auth.ldpc;

import ru.zhuvikin.auth.matrix.Element;
import ru.zhuvikin.auth.matrix.Matrix;
import ru.zhuvikin.auth.matrix.modulo2.Modulo2Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ParityCheckMatrix {

    private final static int MAX_ELIMINATING_PASSES = 10;
    private final static int CHECKS_IN_COLUMN = 3;

    public static Matrix generate(int length, int rank) {
        return generate(length, rank, new Random().nextLong());
    }

    public static Matrix generate(int length, int rank, Long seed) {
        Random random = new Random(seed);

        Matrix parityCheckMatrix = new Modulo2Matrix(length, rank);

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
                while (y < totalCheckBits && parityCheckMatrix.isSet(x, u.get(y))) {
                    y++;
                }
                if (y == totalCheckBits) {
                    throw new RuntimeException("Had to place checks in rows unevenly");
                } else {
                    do {
                        y = iteration + random.nextInt(totalCheckBits - iteration + 1);
                    } while (parityCheckMatrix.isSet(x, u.get(y - 1)));

                    columnIndicies.add(u.get(y - 1));
                    u.set(y - 1, u.get(iteration));
                    iteration++;
                }
            }
            for (Integer item : columnIndicies) {
                parityCheckMatrix.set(x, item);
            }
        }

        // Add extra bits to avoid rows with less than two checks.
        int needed = 2;
        final int[] added = {0};
        IntStream.range(0, rank).parallel().filter(y -> parityCheckMatrix.getRows().get(y).size() < needed)
                .forEach(y -> {
                    while (parityCheckMatrix.getRows().get(y).size() < needed) {
                        parityCheckMatrix.set(random.nextInt(rank), y);
                        added[0]++;
                    }
                });

        if (added[0] > 0) {
            System.err.println("Added " + added[0] + " extra bit-checks to make row counts at least two");
        }

        // Remove 4-length cycles
        for (int iter = 0; iter < MAX_ELIMINATING_PASSES; iter++) {
            final int[] found = {0};
            IntStream.range(0, length).forEach(x -> checkColumn(x, parityCheckMatrix, found, random, rank));
            System.out.println("found: " + found[0]);
            if (found[0] == 0) break;
        }

        return parityCheckMatrix;
    }

    private static void checkColumn(final int x, Matrix parityCheckMatrix, final int[] found, Random random, long rank) {
        for (Element firstEntry : parityCheckMatrix.columnEntries(x).values()) {
            for (Element secondEntry : parityCheckMatrix.rowEntries(firstEntry.getRow()).values()) {
                if (firstEntry.equals(secondEntry)) {
                    continue;
                }
                for (Element thirdEntry : parityCheckMatrix.columnEntries(secondEntry.getColumn()).values()) {
                    if (secondEntry.equals(thirdEntry)) {
                        continue;
                    }

                    Set<Integer> fourthRow = parityCheckMatrix.rowEntries(thirdEntry.getRow()).keySet();
                    if (fourthRow.contains(x)) {
                        int first = parityCheckMatrix.getColumns().get(x).get(thirdEntry.getRow()).getColumn();
                        int y = random.nextInt((int) rank);
                        parityCheckMatrix.remove(first, thirdEntry.getRow());
                        parityCheckMatrix.set(x, y);
                        found[0]++;
                        return;
                    }
                }
            }
        }
    }

}
