package ru.zhuvikin.auth.matrix;

import org.junit.Test;
import ru.zhuvikin.auth.matrix.sparse.GeneratorMatrixInfo;
import ru.zhuvikin.auth.matrix.sparse.LUDecomposition;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.Vector;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import static org.junit.Assert.assertEquals;
import static ru.zhuvikin.auth.matrix.sparse.EquationSolver.backwardSubstitution;
import static ru.zhuvikin.auth.matrix.sparse.EquationSolver.forwardSubstitution;

public class EquationSolverTest {

    private static final Matrix matrix = new Modulo2Matrix(7, 6)
            .set(3, 0).set(5, 0).set(6, 1).set(1, 1).set(0, 2).set(1, 3).set(2, 3).set(2, 4).set(0, 4).set(6, 5);

    private static final GeneratorMatrixInfo decomposition = matrix.getGenerationMatrixInfo();

    @Test
    public void testForwardAnsBackwardSubstitutions() throws Exception {
        System.out.println("matrix: " + matrix);

        // 0 1 1 0 1 0 0
        Vector x = new Vector(6, true).set(1).set(2).set(4);

        Vector y = forwardSubstitution(decomposition, x);

        System.out.println("Solution of Ly=x with x from ( 0 1 1 0 1 0 ) : " + y);

        // 1 1 1 0 1 0
        Vector expectedY = new Vector(6, true).set(0).set(1).set(2).set(4);

        assertEquals(expectedY, y);

        Vector z = backwardSubstitution(decomposition, y);

        System.out.println("Solution of Uz=y : " + z);

        // 1 0 0 0 0 0 1
        Vector expectedZ = new Vector(7, true).set(0).set(6);

        assertEquals(expectedZ, z);
    }

}