package ru.zhuvikin.auth.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MatrixEqualityTest {

    @Test
    public void testEquality1() {
        Matrix matrix1 = new Modulo2Matrix(3, 2).set(2, 1).set(0, 0);

        Matrix matrix2 = new Modulo2Matrix(3, 2).set(2, 1).set(0, 0);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testEquality2() {
        Matrix matrix1 = new Modulo2Matrix(1, 100).set(0, 99).set(0, 5).set(0, 73);

        Matrix matrix2 = new Modulo2Matrix(1, 100).set(0, 99).set(0, 5).set(0, 73);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testEquality3() {
        Matrix matrix1 = new Modulo2Matrix(0, 0);

        Matrix matrix2 = new Modulo2Matrix(0, 0);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testEquality4() {
        Matrix matrix1 = new Modulo2Matrix(1, 1).set(0, 0);

        Matrix matrix2 = new Modulo2Matrix(1, 1).set(0, 0);

        assertEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality1() {
        Matrix matrix1 = new Modulo2Matrix(3, 2).set(2, 1).set(0, 1);

        Matrix matrix2 = new Modulo2Matrix(3, 2).set(2, 1).set(0, 0);

        assertNotEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality2() {
        Matrix matrix1 = new Modulo2Matrix(1, 100).set(0, 99).set(0, 5).set(0, 72);

        Matrix matrix2 = new Modulo2Matrix(1, 100).set(0, 99).set(0, 5).set(0, 73);

        assertNotEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality3() {
        Matrix matrix1 = new Modulo2Matrix(0, 0);

        Matrix matrix2 = new Modulo2Matrix(0, 1);

        assertNotEquals(matrix1, matrix2);
    }

    @Test
    public void testInequality4() {
        Matrix matrix1 = new Modulo2Matrix(1, 1).set(0, 0);

        Matrix matrix2 = new Modulo2Matrix(1, 1);

        assertNotEquals(matrix1, matrix2);
    }

}
