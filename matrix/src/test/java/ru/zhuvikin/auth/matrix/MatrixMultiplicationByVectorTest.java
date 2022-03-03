package ru.zhuvikin.auth.matrix;

import org.junit.Assert;
import org.junit.Test;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.Vector;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

public class MatrixMultiplicationByVectorTest {

    @Test
    public void testMultiply() {
        // 0000000
        // 0001010
        // 0000000
        // 1100001
        // 0000000
        Matrix matrix = new Modulo2Matrix(7, 5).set(3, 1).set(5, 1).set(0, 3).set(1, 3).set(6, 3);

        // 1001010
        Vector vector = new Vector(7, true).set(0).set(3).set(5);

        Vector result = matrix.multiply(vector);

        // 00010
        Vector expected = new Vector(5, true).set(3);

        Assert.assertEquals(expected, result);
    }

}
