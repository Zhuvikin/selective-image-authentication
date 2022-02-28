package ru.zhuvikin.auth.matrix;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixLUDecompositionTest {

    private static final Matrix SOURCE = new Modulo2Matrix(7, 6);
    private static final Matrix EXPECTED_LEFT = new Modulo2Matrix(6, 6);
    private static final Matrix EXPECTED_UPPER = new Modulo2Matrix(7, 6);

    @BeforeClass
    public static void init() {

        /*   0 1 2 3 4 5 6
            --------------
         0 | 0 0 0 1 0 1 0
         1 | 0 1 0 0 0 0 1
         2 | 1 0 0 0 0 0 0
         3 | 0 1 1 0 0 0 0
         4 | 1 0 1 0 0 0 0
         5 | 0 0 0 0 0 0 1 */

        SOURCE.set(3, 0);
        SOURCE.set(5, 0);
        SOURCE.set(6, 1);
        SOURCE.set(1, 1);
        SOURCE.set(0, 2);
        SOURCE.set(1, 3);
        SOURCE.set(2, 3);
        SOURCE.set(2, 4);
        SOURCE.set(0, 4);
        SOURCE.set(6, 5);

        /*   0 1 2 3 4 5
            -------------
         0 | 0 0 0 1 0 0 |
         1 | 0 1 0 0 0 0 |
         2 | 1 0 0 0 0 0 |
         3 | 0 1 1 0 0 0 |
         4 | 1 0 1 0 1 0 |
         5 | 0 0 0 0 1 0 | */

        EXPECTED_LEFT.set(3, 0);
        EXPECTED_LEFT.set(1, 1);
        EXPECTED_LEFT.set(0, 2);
        EXPECTED_LEFT.set(1, 3);
        EXPECTED_LEFT.set(2, 3);
        EXPECTED_LEFT.set(0, 4);
        EXPECTED_LEFT.set(2, 4);
        EXPECTED_LEFT.set(4, 4);
        EXPECTED_LEFT.set(4, 5);

        /*    0 1 2 3 4 5 6
             ---------------
          0 | 1 0 0 0 0 0 0 |
          1 | 0 1 0 0 0 0 1 |
          2 | 0 0 1 0 0 0 1 |
          3 | 0 0 0 1 0 1 0 |
          4 | 0 0 0 0 0 0 1 |
          5 | 0 0 0 0 0 0 0 | */

        EXPECTED_UPPER.set(0, 0);
        EXPECTED_UPPER.set(1, 1);
        EXPECTED_UPPER.set(6, 1);
        EXPECTED_UPPER.set(2, 2);
        EXPECTED_UPPER.set(6, 2);
        EXPECTED_UPPER.set(3, 3);
        EXPECTED_UPPER.set(5, 3);
        EXPECTED_UPPER.set(6, 4);
    }

    @Test
    public void testDecomposition() {
        System.out.println("matrix: " + SOURCE);

        LUDecomposition decomposition = SOURCE.decompose();

        Matrix left = decomposition.getLeft();
        System.out.println("left: " + left);
        assertEquals(EXPECTED_LEFT, left);

        Matrix upper = decomposition.getUpper();
        System.out.println("upper: " + upper);
        assertEquals(EXPECTED_UPPER, upper);

        System.out.println("left multiplied by upper: " + left.multiply(upper));
    }

}
