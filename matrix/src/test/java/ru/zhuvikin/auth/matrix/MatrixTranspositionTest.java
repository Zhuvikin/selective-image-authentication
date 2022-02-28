package ru.zhuvikin.auth.matrix;

import org.junit.Test;
import ru.zhuvikin.auth.matrix.modulo2.Modulo2Matrix;

import static org.junit.Assert.assertEquals;

public class MatrixTranspositionTest {

    @Test
    public void testTransposition1() {
        Modulo2Matrix matrix = new Modulo2Matrix(2, 2);
        matrix.set(0, 0);
        matrix.set(1, 1);

        assertEquals(matrix, matrix.transpose());
    }

    @Test
    public void testTransposition2() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(2, 2);
        matrix1.set(0, 0);
        matrix1.set(0, 1);

        Modulo2Matrix matrix2 = new Modulo2Matrix(2, 2);
        matrix2.set(0, 0);
        matrix2.set(1, 0);

        assertEquals(matrix2, matrix1.transpose());
    }

    @Test
    public void testTransposition3() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(4, 3);
        matrix1.set(0, 0);
        matrix1.set(2, 0);
        matrix1.set(3, 0);
        matrix1.set(2, 2);

        Modulo2Matrix matrix2 = new Modulo2Matrix(3, 4);
        matrix2.set(0, 0);
        matrix2.set(0, 2);
        matrix2.set(0, 3);
        matrix2.set(2, 2);

        assertEquals(matrix2, matrix1.transpose());
    }

}