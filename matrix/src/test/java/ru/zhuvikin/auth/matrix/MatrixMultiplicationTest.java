package ru.zhuvikin.auth.matrix;

import org.junit.Test;
import ru.zhuvikin.auth.matrix.modulo2.Modulo2Matrix;

import static org.junit.Assert.assertEquals;

public class MatrixMultiplicationTest {

    @Test
    public void testMultiplication1() {
        System.out.println("testMultiplication1");

        Matrix matrix1 = new Modulo2Matrix(3, 2);
        matrix1.set(1, 0);
        matrix1.set(2, 0);
        matrix1.set(2, 1);
        System.out.println("matrix1: " + matrix1);

        Matrix matrix2 = new Modulo2Matrix(3, 3);
        matrix2.set(1, 2);
        System.out.println("matrix2: " + matrix2);

        Matrix expected = new Modulo2Matrix(3, 2);
        expected.set(1, 0);
        expected.set(1, 1);
        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.multiply(matrix2));
    }

    @Test
    public void testMultiplication2() {
        System.out.println("testMultiplication2");

        Matrix matrix1 = new Modulo2Matrix(3, 4);
        matrix1.set(1, 0);
        matrix1.set(2, 0);
        matrix1.set(0, 3);
        System.out.println("matrix1: " + matrix1);

        Matrix matrix2 = new Modulo2Matrix(2, 3);
        matrix2.set(0, 0);
        matrix2.set(1, 0);
        matrix2.set(0, 1);
        matrix2.set(0, 2);
        matrix2.set(1, 2);
        System.out.println("matrix2: " + matrix2);

        Matrix expected = new Modulo2Matrix(2, 4);
        expected.set(1, 0);
        expected.set(0, 3);
        expected.set(1, 3);
        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.multiply(matrix2));
    }

    @Test
    public void testMultiplication3() {
        System.out.println("testMultiplication3");

        Matrix matrix1 = new Modulo2Matrix(3, 9);
        matrix1.set(1, 0);
        matrix1.set(2, 0);
        matrix1.set(1, 1);
        matrix1.set(1, 3);
        matrix1.set(1, 5);
        matrix1.set(2, 5);
        matrix1.set(1, 7);
        matrix1.set(0, 8);
        System.out.println("matrix1: " + matrix1);

        Matrix matrix2 = new Modulo2Matrix(2, 3);
        matrix2.set(0, 0);
        matrix2.set(1, 0);
        matrix2.set(1, 1);
        matrix2.set(0, 2);
        matrix2.set(1, 2);
        System.out.println("matrix2: " + matrix2);

        Matrix expected = new Modulo2Matrix(2, 9);
        expected.set(0, 0);
        expected.set(1, 1);
        expected.set(1, 3);
        expected.set(0, 5);
        expected.set(1, 7);
        expected.set(0, 8);
        expected.set(1, 8);
        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.multiply(matrix2));
    }

    @Test
    public void testMultiplication4() {
        System.out.println("testMultiplication4");

        Matrix matrix1 = new Modulo2Matrix(7, 5);

        /*  0 1 2 3 4 5 6
           --------------
        0 | 0 0 0 0 0 0 0
        1 | 0 0 0 1 0 1 0
        2 | 0 0 0 0 0 0 0
        3 | 1 1 0 0 0 0 1
        4 | 0 0 0 0 0 0 0 */

        matrix1.set(3, 1);
        matrix1.set(5, 1);
        matrix1.set(0, 3);
        matrix1.set(1, 3);
        matrix1.set(6, 3);

        System.out.println("matrix1: " + matrix1);

        Matrix matrix2 = new Modulo2Matrix(4, 7);

        /*  0 1 2 3
           --------
        0 | 1 0 0 0
        1 | 0 1 0 0
        2 | 0 0 0 0
        3 | 0 0 0 0
        4 | 0 0 0 0
        5 | 0 1 1 1
        6 | 0 0 0 0 */

        matrix2.set(0, 0);
        matrix2.set(1, 1);
        matrix2.set(1, 5);
        matrix2.set(2, 5);
        matrix2.set(3, 5);

        System.out.println("matrix2: " + matrix2);

        Matrix expected = new Modulo2Matrix(4, 5);

        /*  0 1 2 3
           --------
        0 | 0 0 0 0
        1 | 0 1 1 1
        2 | 0 0 0 0
        3 | 1 1 0 0
        4 | 0 0 0 0 */

        expected.set(0, 3);
        expected.set(1, 1);
        expected.set(1, 3);
        expected.set(2, 1);
        expected.set(3, 1);

        System.out.println("expected: " + expected);

        assertEquals(expected, matrix1.multiply(matrix2));
    }

}