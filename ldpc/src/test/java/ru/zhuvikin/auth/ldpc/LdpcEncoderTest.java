package ru.zhuvikin.auth.ldpc;

import org.junit.Test;
import ru.zhuvikin.auth.code.BitSequence;
import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.code.Encoder;
import ru.zhuvikin.auth.matrix.LUDecomposition;
import ru.zhuvikin.auth.matrix.Matrix;
import ru.zhuvikin.auth.matrix.modulo2.Modulo2Matrix;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LdpcEncoderTest {

    private static final Code CODE = new Code(12, 24, 1);
    private static final Encoder ENCODER = new LdpcEncoder();

    @Test
    public void encode() throws Exception {
        BitSequence bitSequence = new BitSequence(12).set(0).set(1).set(6).set(8).set(11);

        List<BitSequence> encoded = ENCODER.encode(CODE, bitSequence);

        assertEquals(1, encoded.size());
        BitSequence sequence = new BitSequence(24).set(0).set(1).set(3).set(6).set(9).set(10).set(12).set(18).set(20).set(23);
        assertEquals(sequence, encoded.get(0));
    }

    @Test
    public void decode() throws Exception {
        BitSequence bitSequence = new BitSequence(12).set(2).set(8).set(9).set(10);

        List<BitSequence> encoded = ENCODER.encode(CODE, bitSequence);

        assertEquals(1, encoded.size());

        BitSequence expectedEncoded = new BitSequence(24).set(0).set(11).set(12).set(14).set(20).set(21).set(22);

        assertEquals(expectedEncoded, encoded.get(0));

        BitSequence decoded = ENCODER.decode(CODE, encoded);
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
        BitSequence source = new BitSequence(16).set(0).set(2).set(13).set(14);

        System.out.println("source = " + source);

        Code code = new Code(16, 32);
        code.setParityCheckMatrix(parityCheckMatrix);
        code.setGeneratorMatrix(parityCheckMatrix.decompose());

        LdpcEncoder encoder = new LdpcEncoder();
        List<BitSequence> codeWords = encoder.encode(code, source);

        BitSequence encodedExpected = new BitSequence(32).set(2).set(3).set(4).set(5).set(6).set(9).set(10)
                .set(11).set(12).set(13).set(16).set(18).set(29).set(30);

        System.out.println("encoded = " + codeWords);

        BitSequence encoded = codeWords.get(0);
        assertEquals(encodedExpected, encoded);

        BitSequence decoded = encoder.decode(code, codeWords);
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);

        // ------------------------------------------------------------------------

        BitSequence tampered = encoded.clone().set(0);
        System.out.println("tampered = " + tampered);

        decoded = encoder.decode(code, Collections.singletonList(tampered));
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);

        // ------------------------------------------------------------------------

        tampered = encoded.clone().set(28);
        System.out.println("tampered = " + tampered);

        decoded = encoder.decode(code, Collections.singletonList(tampered));
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);

        // ------------------------------------------------------------------------

        tampered = encoded.clone().set(28).set(31);
        System.out.println("tampered = " + tampered);

        decoded = encoder.decode(code, Collections.singletonList(tampered));
        System.out.println("decoded = " + decoded);
        assertEquals(source, decoded);
    }


}