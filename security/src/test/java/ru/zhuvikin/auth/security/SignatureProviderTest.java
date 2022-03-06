package ru.zhuvikin.auth.security;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.zhuvikin.auth.security.SignatureProvider.sign;
import static ru.zhuvikin.auth.security.SignatureProvider.verify;

public class SignatureProviderTest {

    private static final List<Integer> FEATURES = Arrays.asList(1, -1, 0, 3, 7, 8);

    private static final String PASSWORD = "password";
    private static final int SIGNATURE_LENGTH = 64;

    private static final RsaKeys RSA_KEYS = new RsaKeys(PASSWORD, SIGNATURE_LENGTH);
    private static final RsaKeys RSA_KEYS_1024 = new RsaKeys(PASSWORD, 1024);

    @Test
    public void testConvertBitSetToInteger() {
        BigInteger expected = new BigInteger("132");
        BitSet bitSet = SignatureProvider.convert(expected);
        BigInteger integer = SignatureProvider.convert(bitSet);
        assertEquals(expected, integer);
    }

    @Test
    public void testSign() throws Exception {

        BitSet signature = sign(FEATURES, RSA_KEYS.getPrivateKey());

        List<Integer> expectedOnes = Arrays.asList(1, 4, 5, 9, 10, 11, 14, 17, 18, 19, 21, 23, 29, 32, 36, 38, 39, 40, 41, 44, 46, 47, 57, 61, 64);
        BitSet expected = new BitSet();
        expectedOnes.forEach(expected::set);

        assertEquals(expected, signature);
    }

    @Test
    public void testVerify1() throws Exception {
        BitSet signature = sign(FEATURES, RSA_KEYS.getPrivateKey());

        boolean authentic = verify(FEATURES, RSA_KEYS.getPublicKey(), signature);

        assertTrue(authentic);
    }

    @Test
    public void testVerify2() throws Exception {
        BitSet signature = sign(FEATURES, RSA_KEYS_1024.getPrivateKey());

        boolean authentic = verify(FEATURES, RSA_KEYS_1024.getPublicKey(), signature);

        assertTrue(authentic);
    }

}