package ru.zhuvikin.auth.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MatrixEqualityTest {

    @Test
    public void testEquality1() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(3, 2);
        matrix1.set(2, 1);
        matrix1.set(0, 0);

        Modulo2Matrix matrix2 = new Modulo2Matrix(3, 2);
        matrix2.set(2, 1);
        matrix2.set(0, 0);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testEquality2() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(1, 100);
        matrix1.set(0, 99);
        matrix1.set(0, 5);
        matrix1.set(0, 73);

        Modulo2Matrix matrix2 = new Modulo2Matrix(1, 100);
        matrix2.set(0, 99);
        matrix2.set(0, 5);
        matrix2.set(0, 73);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testEquality3() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(0, 0);

        Modulo2Matrix matrix2 = new Modulo2Matrix(0, 0);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testEquality4() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(1, 1);
        matrix1.set(0, 0);

        Modulo2Matrix matrix2 = new Modulo2Matrix(1, 1);
        matrix2.set(0, 0);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality1() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(3, 2);
        matrix1.set(2, 1);
        matrix1.set(0, 1);

        Modulo2Matrix matrix2 = new Modulo2Matrix(3, 2);
        matrix2.set(2, 1);
        matrix2.set(0, 0);

        assertNotEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality2() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(1, 100);
        matrix1.set(0, 99);
        matrix1.set(0, 5);
        matrix1.set(0, 72);

        Modulo2Matrix matrix2 = new Modulo2Matrix(1, 100);
        matrix2.set(0, 99);
        matrix2.set(0, 5);
        matrix2.set(0, 73);

        assertNotEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality3() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(0, 0);

        Modulo2Matrix matrix2 = new Modulo2Matrix(0, 1);

        assertNotEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality4() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(1, 1);
        matrix1.set(0, 0);

        Modulo2Matrix matrix2 = new Modulo2Matrix(1, 1);

        assertNotEquals(matrix1, matrix2);
    }

}
