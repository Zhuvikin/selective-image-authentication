package ru.zhuvikin.auth.ldpc;

import org.junit.Test;
import ru.zhuvikin.auth.code.BitSequence;
import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.code.Encoder;
import ru.zhuvikin.auth.matrix.LUDecomposition;
import ru.zhuvikin.auth.matrix.Matrix;
import ru.zhuvikin.auth.matrix.modulo2.Modulo2Matrix;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LdpcEncoderTest {

    private static final Code CODE = new Code(12, 24, 1);
    private static final Encoder ENCODER = new LdpcEncoder();

    @Test
    public void encode() throws Exception {
        BitSequence bitSequence = new BitSequence(12).set(0).set(1).set(6).set(8).set(11);

        List<BitSequence> encoded = ENCODER.encode(CODE, bitSequence);

        assertEquals(1, encoded.size());
        assertTrue(encoded.get(0).getBits().containsAll(asList(0, 4, 6, 9, 10, 12, 18, 20, 23)));
    }

    @Test
    public void decode() throws Exception {
        BitSequence bitSequence = new BitSequence(12).set(2).set(8).set(9).set(10);

        List<BitSequence> encoded = ENCODER.encode(CODE, bitSequence);

        assertEquals(1, encoded.size());
        assertTrue(encoded.get(0).getBits().containsAll(asList(0, 1, 2, 11, 12, 14, 20, 21, 22)));

        BitSequence decoded = ENCODER.decode(CODE, encoded);
        assertEquals(encoded, decoded);
    }

    @Test
    public void encode2() {

        // Parity-check matrix:
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 1 0 0 0 0 0
        // 0 0 0 1 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1
        // 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 1 0 0 1 0 0 0 0 0 0 0 0
        // 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0
        // 0 0 1 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0
        // 0 0 1 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 1 1 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0 0
        // 0 0 0 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 1
        // 0 0 0 0 0 0 0 1 1 0 0 1 0 0 0 1 0 0 1 0 0 0 1 0 0 0 0 0 0 0 0 1
        // 1 0 0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 1 0 0 0 0 1 0 1 1 0
        // 1 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 1 1 1 0 0 0 1 0 1 0 0 1 1 1 0
        // 1 1 1 0 1 1 1 1 1 0 0 0 1 1 0 0 1 1 1 0 1 1 0 0 1 1 0 0 0 0 0 0
        // 0 1 0 1 1 1 1 1 1 1 1 0 1 1 1 0 1 1 0 1 1 1 0 0 0 1 1 0 1 0 1 0

        Matrix parityCheckMatrix = new Modulo2Matrix(32, 16).set(14, 0).set(24, 0).set(12, 1).set(16, 1)
                .set(24, 2).set(26, 2).set(3, 3).set(13, 3).set(14, 3).set(31, 3).set(1, 4).set(15, 4).set(20, 4)
                .set(23, 4).set(4, 5).set(10, 5).set(15, 5).set(27, 5).set(2, 6).set(9, 6).set(28, 6).set(2, 7)
                .set(6, 7).set(23, 7).set(26, 7).set(27, 7).set(10, 8).set(22, 8).set(29, 8).set(9, 9).set(11, 9)
                .set(21, 10).set(31, 10).set(7, 11).set(8, 11).set(11, 11).set(15, 11).set(18, 11).set(22, 11)
                .set(31, 11).set(0, 12).set(3, 12).set(5, 12).set(19, 12).set(22, 12).set(27, 12).set(29, 12).set(30, 12)
                .set(0, 13).set(11, 13).set(17, 13).set(18, 13).set(19, 13).set(23, 13).set(25, 13).set(28, 13)
                .set(29, 13).set(30, 13).set(0, 14).set(1, 14).set(2, 14).set(4, 14).set(5, 14).set(6, 14).set(7, 14)
                .set(8, 14).set(12, 14).set(13, 14).set(16, 14).set(17, 14).set(18, 14).set(20, 14).set(21, 14)
                .set(24, 14).set(25, 14).set(1, 15).set(3, 15).set(4, 15).set(5, 15).set(6, 15).set(7, 15).set(8, 15)
                .set(9, 15).set(10, 15).set(12, 15).set(13, 15).set(14, 15).set(16, 15).set(17, 15).set(19, 15)
                .set(20, 15).set(21, 15).set(25, 15).set(26, 15).set(28, 15).set(30, 15);

        System.out.println("parityCheckMatrix = " + parityCheckMatrix);

        LUDecomposition decomposition = parityCheckMatrix.decompose();

        // Expected left:
        // 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 1 0 0 0 0 0 1 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 0
        // 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 1 0 1 0 0 0 1 0 0 0 0 0
        // 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0
        // 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0
        // 0 1 0 1 0 0 1 1 1 1 1 1 1 1 0 1
        // 1 1 1 1 1 1 0 1 1 1 1 1 1 1 1 0

        Matrix expectedLeft = new Modulo2Matrix(16, 16).set(2, 0).set(1, 1).set(4, 2).set(2, 3).set(8, 3).set(7, 4).set(5, 5)
                .set(9, 5).set(0, 6).set(6, 6).set(4, 7).set(6, 7).set(10, 7).set(5, 8).set(0, 9).set(3, 10).set(11, 11)
                .set(12, 12).set(13, 13).set(1, 14).set(3, 14).set(6, 14).set(7, 14).set(8, 14).set(9, 14).set(10, 14)
                .set(11, 14).set(12, 14).set(13, 14).set(15, 14).set(0, 15).set(1, 15).set(2, 15).set(3, 15).set(4, 15)
                .set(5, 15).set(7, 15).set(8, 15).set(9, 15).set(10, 15).set(11, 15).set(12, 15).set(13, 15).set(14, 15);

        System.out.println("expectedLeft = " + expectedLeft);
        System.out.println("actualLeft = " + decomposition.getLeft());

        //  Expected upper:
        //  0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0
        //  0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0

        Matrix expectedUpper = new Modulo2Matrix(32, 16).set(9, 0).set(12, 1).set(14, 2).set(21, 3)
                .set(26, 4).set(10, 5).set(2, 6).set(1, 7).set(13, 8).set(4, 9).set(6, 10).set(7, 11).set(18, 11)
                .set(5, 12).set(19, 12).set(17, 13).set(18, 13).set(19, 13).set(19, 14).set(18, 15);

        System.out.println("expectedUpper = " + expectedUpper);
        System.out.println("actualUpper = " + decomposition.getUpper());

        System.out.println("expectedLeft.multiply(expectedUpper) = " + expectedLeft.multiply(expectedUpper));
        System.out.println("actualLeft.multiply(actualUpper) = " + decomposition.getLeft().multiply(decomposition.getUpper()));

    }


}