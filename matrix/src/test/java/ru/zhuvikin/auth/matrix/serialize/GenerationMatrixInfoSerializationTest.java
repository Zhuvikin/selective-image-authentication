package ru.zhuvikin.auth.matrix.serialize;

import org.junit.Test;
import ru.zhuvikin.auth.matrix.sparse.GeneratorMatrixInfo;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class GenerationMatrixInfoSerializationTest {

    private static final ClassLoader CLASS_LOADER = GenerationMatrixInfoSerializationTest.class.getClassLoader();

    @Test
    public void testSerialization() {
        Matrix matrix = new Modulo2Matrix(7, 6).set(3, 0).set(5, 0).set(6, 1).set(1, 1).set(0, 2).set(1, 3).set(2, 3).set(2, 4).set(0, 4).set(6, 5);
        GeneratorMatrixInfo generationMatrixInfo = matrix.getGenerationMatrixInfo();
        byte[] bytes = generationMatrixInfo.serialize();

        GeneratorMatrixInfo deserialized = GeneratorMatrixInfo.deserialize(bytes);
        assertEquals(generationMatrixInfo, deserialized);
    }

    @Test
    public void deserialize() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(CLASS_LOADER.getResource("generation-matrix-info").toURI()));

        GeneratorMatrixInfo deserialized = GeneratorMatrixInfo.deserialize(bytes);
        // assertEquals(matrix, deserialized);
    }

}
