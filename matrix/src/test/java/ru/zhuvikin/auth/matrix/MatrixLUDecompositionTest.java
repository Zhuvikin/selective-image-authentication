package ru.zhuvikin.auth.matrix;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixLUDecompositionTest {

    @Test
    public void testDecomposition() {

        /*   0 1 2 3 4 5 6
            --------------
         0 | 0 0 0 1 0 1 0
         1 | 0 1 0 0 0 0 1
         2 | 1 0 0 0 0 0 0
         3 | 0 1 1 0 0 0 0
         4 | 1 0 1 0 0 0 0
         5 | 0 0 0 0 0 0 1 */
        Matrix matrix = new Modulo2Matrix(7, 6).set(3, 0).set(5, 0).set(6, 1).set(1, 1).set(0, 2).set(1, 3).set(2, 3).set(2, 4).set(0, 4).set(6, 5);

        System.out.println("matrix: " + matrix);

        LUDecomposition decomposition = matrix.decompose();

         /*   0 1 2 3 4 5
            -------------
         0 | 0 0 0 1 0 0 |
         1 | 0 1 0 0 0 0 |
         2 | 1 0 0 0 0 0 |
         3 | 0 1 1 0 0 0 |
         4 | 1 0 1 0 1 0 |
         5 | 0 0 0 0 1 0 | */
        Matrix expectedLeft = new Modulo2Matrix(6, 6).set(3, 0).set(1, 1).set(0, 2).set(1, 3).set(2, 3).set(0, 4).set(2, 4).set(4, 4).set(4, 5);

        Matrix left = decomposition.getLeft();
        System.out.println("left: " + left);
        assertEquals(expectedLeft, left);

        /*  0 1 2 3 4 5 6
             ---------------
          0 | 1 0 0 0 0 0 0 |
          1 | 0 1 0 0 0 0 1 |
          2 | 0 0 1 0 0 0 1 |
          3 | 0 0 0 1 0 1 0 |
          4 | 0 0 0 0 0 0 1 |
          5 | 0 0 0 0 0 0 0 | */
        Matrix expectedUpper = new Modulo2Matrix(7, 6).set(0, 0).set(6, 1).set(1, 1).set(2, 2).set(6, 2).set(3, 3).set(5, 3).set(6, 4);

        Matrix upper = decomposition.getUpper();
        System.out.println("upper: " + upper);
        assertEquals(expectedUpper, upper);

        System.out.println("left multiplied by upper: " + left.multiply(upper));
    }

}
