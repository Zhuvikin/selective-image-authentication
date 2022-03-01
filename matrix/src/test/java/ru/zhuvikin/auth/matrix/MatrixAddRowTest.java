package ru.zhuvikin.auth.matrix;

import org.junit.Test;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import static org.junit.Assert.assertEquals;

public class MatrixAddRowTest {

    @Test
    public void addRowTest1() {
        Matrix matrix1 = new Modulo2Matrix(3, 2).set(0, 0);
        System.out.println("matrix1: " + matrix1);

        Matrix matrix2 = new Modulo2Matrix(3, 2).set(1, 1);
        System.out.println("matrix2: " + matrix2);

        Matrix result = matrix1.addRow(0, matrix2, 1);

        Matrix expected = new Modulo2Matrix(3, 2).set(0, 0).set(1, 0);
        assertEquals(expected, result);
    }

    @Test
    public void addRowTest2() {
        Matrix matrix1 = new Modulo2Matrix(2, 3).set(0, 1);

        Matrix matrix2 = new Modulo2Matrix(2, 3).set(1, 0).set(0, 0);

        Matrix result = matrix1.addRow(1, matrix2, 0);

        Matrix expected = new Modulo2Matrix(2, 3).set(1, 1);

        assertEquals(expected, result);
    }

}
