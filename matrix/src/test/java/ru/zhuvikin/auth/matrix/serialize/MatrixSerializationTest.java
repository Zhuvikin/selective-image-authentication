package ru.zhuvikin.auth.matrix.serialize;

import org.junit.Assert;
import org.junit.Test;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MatrixSerializationTest {

    private static final ClassLoader CLASS_LOADER = MatrixSerializationTest.class.getClassLoader();

    @Test
    public void testSerialization() {
        Matrix matrix = new Modulo2Matrix(5, 6).set(0, 1).set(3, 2).set(3, 4).set(4, 1);
        byte[] serialized = matrix.serialize();

        Matrix deserialized = Modulo2Matrix.deserialize(serialized);
        Assert.assertEquals(matrix, deserialized);
    }

    @Test
    public void deserialize() throws Exception {
        Matrix matrix = new Modulo2Matrix(40, 35);
        for (int i = 0; i < 35; i++) matrix.set(i, i);
        matrix.set(3, 2);
        matrix.set(4, 34);
        matrix.set(38, 10);

        byte[] bytes = Files.readAllBytes(Paths.get(CLASS_LOADER.getResource("matrix-file").toURI()));

        Matrix deserialized = Modulo2Matrix.deserialize(bytes);
        Assert.assertEquals(matrix, deserialized);
    }

}
