package ru.zhuvikin.auth.watermarking;

import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class StringEncoderTest {

    private static final int MAXIMUM_NAME_LENGTH = 32;

    @Test
    public void encodeTest1() {
        String name = "ИВАНОВ ИВАН IVANOVICH";

        BitSet encoded = StringEncoder.encode(name, MAXIMUM_NAME_LENGTH);
        String decoded = StringEncoder.decode(encoded, MAXIMUM_NAME_LENGTH);

        assertEquals(name, decoded);
    }

    @Test
    public void encodeTest2() {
        String name = "Петров-Иванов Петр Петрович";

        BitSet encoded = StringEncoder.encode(name, MAXIMUM_NAME_LENGTH);
        String decoded = StringEncoder.decode(encoded, MAXIMUM_NAME_LENGTH);

        assertEquals(name.toUpperCase(), decoded);
    }

    @Test
    public void encodeTestEmpty() {
        String name = "";

        BitSet encoded = StringEncoder.encode(name, MAXIMUM_NAME_LENGTH);
        String decoded = StringEncoder.decode(encoded, MAXIMUM_NAME_LENGTH);

        assertEquals(name.toUpperCase(), decoded);
    }

    @Test
    public void encodeTestNull() {
        String name = null;

        BitSet encoded = StringEncoder.encode(name, MAXIMUM_NAME_LENGTH);
        String decoded = StringEncoder.decode(encoded, MAXIMUM_NAME_LENGTH);

        assertEquals("", decoded);
    }

}