package ru.zhuvikin.auth.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixAdditionTest {

    @Test
    public void testAddition1() {
        System.out.println("testAddition1");

        Modulo2Matrix matrix1 = new Modulo2Matrix(3, 3);
        matrix1.set(0, 1);
        matrix1.set(0, 0);
        matrix1.set(2, 2);
        System.out.println("matrix1: " + matrix1);

        Modulo2Matrix matrix2 = new Modulo2Matrix(3, 3);
        matrix2.set(0, 0);
        System.out.println("matrix2: " + matrix2);

        Modulo2Matrix expected = new Modulo2Matrix(3, 3);
        expected.set(0, 1);
        expected.set(2, 2);
        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.add(matrix2));
    }

    @Test
    public void testAddition2() {
        System.out.println("testAddition2");

        Modulo2Matrix matrix1 = new Modulo2Matrix(2, 2);
        matrix1.set(0, 0);
        System.out.println("matrix1: " + matrix1);

        Modulo2Matrix matrix2 = new Modulo2Matrix(2, 2);
        matrix2.set(1, 1);
        System.out.println("matrix2: " + matrix2);

        Modulo2Matrix expected = new Modulo2Matrix(2, 2);
        expected.set(0, 0);
        expected.set(1, 1);
        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.add(matrix2));
    }

    @Test
    public void testSubtraction() {
        Modulo2Matrix matrix1 = new Modulo2Matrix(1000, 3);
        matrix1.set(99, 1);
        matrix1.set(100, 1);
        matrix1.set(101, 1);
        matrix1.set(999, 2);

        Modulo2Matrix matrix2 = new Modulo2Matrix(1000, 3);
        matrix2.set(99, 1);
        matrix2.set(100, 2);
        matrix2.set(101, 2);
        matrix2.set(0, 0);

        Modulo2Matrix expected = new Modulo2Matrix(1000, 3);
        expected.set(100, 1);
        expected.set(101, 1);
        expected.set(999, 2);
        expected.set(100, 2);
        expected.set(101, 2);
        expected.set(0, 0);

        assertEquals(expected, matrix1.subtract(matrix2));
    }

}
