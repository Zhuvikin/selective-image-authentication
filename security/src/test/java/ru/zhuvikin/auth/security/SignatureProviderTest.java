package ru.zhuvikin.auth.security;

import org.junit.Test;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.zhuvikin.auth.security.SignatureProvider.sign;
import static ru.zhuvikin.auth.security.SignatureProvider.verify;

public class SignatureProviderTest {

    private static final List<Integer> FEATURES = Arrays.asList(1, -1, 0, 3, 7, 8);

    private static final String PASSWORD =  "password";
    private static final int SIGNATURE_LENGTH = 64;

    private static final RsaKeys RSA_KEYS = new RsaKeys(PASSWORD, SIGNATURE_LENGTH);

    @Test
    public void testSign() throws Exception {

        BitSet signature = sign(FEATURES, RSA_KEYS.getPrivateKey());

        List<Integer> expectedOnes = Arrays.asList(1, 3, 5, 6, 9, 12, 13, 14, 15, 18, 20, 22, 24, 26, 28, 31, 32, 33, 40, 41, 46, 47, 52, 53, 54, 62);
        BitSet expected = new BitSet();
        expectedOnes.forEach(expected::set);

        assertEquals(expected, signature);
    }

    @Test
    public void testVerify() throws Exception {
        BitSet signature = sign(FEATURES, RSA_KEYS.getPrivateKey());

        boolean authentic = verify(FEATURES, RSA_KEYS.getPublicKey(), signature);

        assertTrue(authentic);
    }

}