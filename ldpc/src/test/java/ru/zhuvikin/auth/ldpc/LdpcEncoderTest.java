package ru.zhuvikin.auth.ldpc;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.code.CodeCache;
import ru.zhuvikin.auth.matrix.sparse.LUDecomposition;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;
import static ru.zhuvikin.auth.ldpc.LdpcEncoder.decode;
import static ru.zhuvikin.auth.ldpc.LdpcEncoder.encode;

public class LdpcEncoderTest {

    private static Code CODE;

    @BeforeClass
    public static void init () {
        CODE = CodeCache.of(12, 24);
    }

    @Test
    public void testEncode() {
        BitSet bitSequence = bitSet(0, 1, 6, 8, 11);

        BitSet encoded = encode(CODE, bitSequence, 12);

        BitSet expected = bitSet(1, 9, 10, 12, 13, 14, 18, 20, 23);
        assertEquals(expected, encoded);
    }

    @Test
    public void testDecode() {
        BitSet bitSequence = bitSet(2, 8, 9, 10);

        BitSet encoded = encode(CODE, bitSequence, 12);

        BitSet expectedEncoded = bitSet(2, 3, 8, 11, 20, 21, 22);

        assertEquals(expectedEncoded, encoded);

        BitSet decoded = decode(CODE, encoded, 24);
        assertEquals(bitSequence, decoded);
    }

    @Test
    public void encodeAndDecode() {

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
                .set(0, 13).set(11, 13).set(17, 13).set(18, 13).set(19, 13).set(23, 13).set(25, 13).set(28, 13).set(29, 13)
                .set(30, 13).set(0, 14).set(1, 14).set(2, 14).set(4, 14).set(5, 14).set(6, 14).set(7, 14).set(8, 14)
                .set(12, 14).set(13, 14).set(16, 14).set(17, 14).set(18, 14).set(20, 14).set(21, 14).set(24, 14).set(25, 14)
                .set(1, 15).set(3, 15).set(4, 15).set(5, 15).set(6, 15).set(7, 15).set(8, 15).set(9, 15).set(10, 15)
                .set(12, 15).set(13, 15).set(14, 15).set(16, 15).set(17, 15).set(19, 15).set(20, 15).set(21, 15)
                .set(25, 15).set(26, 15).set(28, 15).set(30, 15);


        System.out.println("parityCheckMatrix = " + parityCheckMatrix);

        LUDecomposition decomposition = parityCheckMatrix.decompose();

        // Expected left:
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1
        // 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 1 0 0 0 1 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0
        // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0
        // 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 1 0 0 1 0 1 0 0 0 0 0 0 0 0 0 0
        // 1 1 1 1 1 0 1 1 0 1 1 0 0 0 0 0
        // 0 1 0 1 1 1 1 1 0 0 0 1 1 0 0 0

        Matrix expectedLeft = new Modulo2Matrix(16, 16).set(13, 0).set(11, 1).set(15, 2).set(3, 3)
                .set(1, 4).set(4, 5).set(2, 6).set(2, 7).set(6, 7).set(9, 8).set(8, 9).set(14, 10).set(7, 11).set(0, 12)
                .set(0, 13).set(3, 13).set(5, 13).set(0, 14).set(1, 14).set(2, 14).set(3, 14).set(4, 14).set(6, 14)
                .set(7, 14).set(9, 14).set(10, 14).set(1, 15).set(3, 15).set(4, 15).set(5, 15).set(6, 15).set(7, 15)
                .set(11, 15).set(12, 15);

        System.out.println("actualLeft = " + decomposition.getLeft());

        assertEquals(expectedLeft, decomposition.getLeft());

        //  Expected upper:
        // 1 0 0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 1 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 1 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 1 0 0 0 0 0 1 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 1 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 1 1 0 1 0 0 0 0 0 0 1 0 0 1 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0
        // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0

        Matrix expectedUpper = new Modulo2Matrix(32, 16).set(0, 0).set(3, 0).set(5, 0).set(1, 1).set(2, 2)
                .set(9, 2).set(3, 3).set(13, 3).set(14, 3).set(4, 4).set(10, 4).set(5, 5).set(11, 5).set(13, 5).set(14, 5)
                .set(6, 6).set(9, 6).set(7, 7).set(11, 7).set(9, 8).set(11, 8).set(10, 9).set(11, 10).set(12, 10)
                .set(14, 10).set(21, 10).set(24, 10).set(12, 11).set(13, 12).set(14, 12).set(21, 12).set(14, 13).set(24, 13)
                .set(21, 14).set(24, 15);

        System.out.println("actualUpper = " + decomposition.getUpper());

        assertEquals(expectedUpper, decomposition.getUpper());

        // 1 0 1 0 0 0 0 0 0 0 0 0 0 1 1 0
        BitSet source = bitSet(0, 2, 13, 14);

        System.out.println("source = " + source);

        Code code = CodeCache.of(16, 32);
        code.setParityCheckMatrix(parityCheckMatrix);
        code.setGeneratorMatrix(parityCheckMatrix.decompose());

        BitSet encoded = encode(code, source, 16);

        BitSet encodedExpected = bitSet(2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 16, 18, 29, 30);

        System.out.println("encoded = " + encoded);

        assertEquals(encodedExpected, encoded);

        BitSet decoded = decode(code, encoded, 32);
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);

        // ------------------------------------------------------------------------

        BitSet tampered = (BitSet) encoded.clone();
        tampered.set(0);
        System.out.println("tampered = " + tampered);

        decoded = decode(code, tampered, 32);
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);

        // ------------------------------------------------------------------------

        tampered = (BitSet) encoded.clone();
        tampered.set(28);
        System.out.println("tampered = " + tampered);

        decoded = decode(code, tampered, 32);
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);

        // ------------------------------------------------------------------------

        tampered = (BitSet) encoded.clone();
        tampered.set(28);
        tampered.set(31);

        System.out.println("tampered = " + tampered);

        decoded = decode(code, tampered, 32);
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);
    }

    private static BitSet bitSet(int... indicies) {
        BitSet bitSet = new BitSet();
        for (int index : indicies) {
            bitSet.set(index);
        }
        return bitSet;
    }

}