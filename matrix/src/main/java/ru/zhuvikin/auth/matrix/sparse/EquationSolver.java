package ru.zhuvikin.auth.matrix.sparse;

import java.util.List;

public final class EquationSolver {

    // Ly = x
    public static Vector forwardSubstitution(GeneratorMatrixInfo generatorMatrix, Vector x) {
        Matrix L = generatorMatrix.getLuDecomposition().getLeft();
        List<Integer> rows = generatorMatrix.getRows();

        Vector y = new Vector(x.size(), true);

        int K, i, j, ii;
        boolean b, d;
        Element e;

        K = L.getWidth();

        // Make sure that L is lower-triangular, after row re-ordering.
        for (i = 0; i < K; i++) {
            ii = rows.get(i);
            e = L.lastInRow(ii);
            if (e.right() != null && e.getColumn() > i) {
                throw new RuntimeException("Matrix is not lower-triangular");
            }
        }

        // Solve system by forward substitution.
        for (i = 0; i < K; i++) {
            ii = rows.get(i);

            // Look at bits in this row, forming inner product with partial
            // solution, and seeing if the diagonal is 1. */
            d = false;
            b = false;

            for (e = L.firstInRow(ii); e.right() != null; e = e.right()) {
                j = e.getColumn();
                if (j == i) {
                    d = true;
                } else {
                    b = y.isSet(j) != b;
                }
            }

            // Check for no solution if the diagonal isn't 1.
            if (!d && b != x.isSet(ii)) {
                return y;
            }

            // Set bit of solution, zero if arbitrary.
            if (x.isSet(ii) != b) {
                y.set(i);
            } else {
                y.remove(i);
            }
        }
        return y;
    }

    // Uz = y
    public static Vector backwardSubstitution(GeneratorMatrixInfo generatorMatrix, Vector y) {
        Matrix U = generatorMatrix.getLuDecomposition().getUpper();
        List<Integer> columns = generatorMatrix.getColumns();

        Vector z = new Vector(U.getWidth(), true);

        int K, i, j, ii;
        boolean b, d;
        Element e;
        K = U.getHeight();

        // Make sure that U is upper-triangular, after column re-ordering.
        for (i = 0; i < K; i++) {
            ii = columns.get(i);
            e = U.lastInColumn(ii);
            if (e != null && e.bottom() != null && e.getRow() > i) {
                throw new RuntimeException("Matrix is not upper-triangular");
            }
        }

        // Solve system by backward substitution.
        for (i = K - 1; i >= 0; i--) {
            ii = columns.get(i);

            // Look at bits in this row, forming inner product with partial
            // solution, and seeing if the diagonal is 1.
            d = false;
            b = false;

            for (e = U.firstInRow(i); e != null && e.right() != null; e = e.right()) {
                j = e.getColumn();
                if (j == ii) {
                    d = true;
                } else {
                    b = z.isSet(j) != b;
                }
            }

            // Check for no solution if the diagonal isn't 1.
            if (!d && b != y.isSet(i)) {
                return z;
            }

            // Set bit of solution, zero if arbitrary.
            if (y.isSet(i) != b) {
                z.set(ii);
            } else {
                z.remove(ii);
            }
        }

        return z;
    }

}
