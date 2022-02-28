package ru.zhuvikin.auth.ldpc;

import org.junit.Assert;
import org.junit.Test;
import ru.zhuvikin.auth.matrix.Element;
import ru.zhuvikin.auth.matrix.Matrix;

import java.util.NavigableMap;

public class ParityCheckMatrixGeneratorTest {

    private final static int MIN_CHECKS = 2;

    @Test
    public void testGeneration() {
        Matrix matrix = ParityCheckMatrix.generate(40, 40, 1L);

        for (NavigableMap<Integer, Element> column : matrix.getColumns().values()) {
            Assert.assertTrue(column.size() >= MIN_CHECKS);
        }

        System.out.println("Generated parity-check matrix: " + matrix);
    }

}
