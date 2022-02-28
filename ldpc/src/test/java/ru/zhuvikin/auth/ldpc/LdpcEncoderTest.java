package ru.zhuvikin.auth.ldpc;

import org.junit.Test;
import ru.zhuvikin.auth.code.BitSequence;
import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.code.Encoder;

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

}