package ru.zhuvikin.auth.matrix;

import org.junit.Test;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import static org.junit.Assert.assertEquals;

public class MatrixAdditionTest {

    @Test
    public void testAddition1() {
        System.out.println("testAddition1");

        Matrix matrix1 = new Modulo2Matrix(3, 3).set(0, 1).set(0, 0).set(2, 2);
        System.out.println("matrix1: " + matrix1);

        Matrix matrix2 = new Modulo2Matrix(3, 3).set(0, 0);
        System.out.println("matrix2: " + matrix2);

        Matrix expected = new Modulo2Matrix(3, 3).set(0, 1).set(2, 2);
        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.add(matrix2));
    }

    @Test
    public void testAddition2() {
        System.out.println("testAddition2");

        Matrix matrix1 = new Modulo2Matrix(2, 2).set(0, 0);
        System.out.println("matrix1: " + matrix1);

        Matrix matrix2 = new Modulo2Matrix(2, 2).set(1, 1);
        System.out.println("matrix2: " + matrix2);

        Matrix expected = new Modulo2Matrix(2, 2).set(0, 0).set(1, 1);
        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.add(matrix2));
    }

    @Test
    public void testSubtraction() {
        Matrix matrix1 = new Modulo2Matrix(1000, 3).set(99, 1).set(100, 1).set(101, 1).set(999, 2);

        Matrix matrix2 = new Modulo2Matrix(1000, 3).set(99, 1).set(100, 2).set(101, 2).set(0, 0);

        Matrix expected = new Modulo2Matrix(1000, 3).set(100, 1).set(101, 1).set(999, 2).set(100, 2).set(101, 2).set(0, 0);

        assertEquals(expected, matrix1.subtract(matrix2));
    }

}
