package ru.zhuvikin.auth.security;

import lombok.Getter;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;

import static java.math.BigInteger.probablePrime;

@Getter
public class RsaKeys {

    private static final long EXPONENT = 65537;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RsaKeys(String passphrase, int length) {
        int pAndQLength = (int) Math.floor(length / 2);

        long seed1 = seedByPassphrase(passphrase);
        BigInteger p = random(pAndQLength - 1, seed1);

        long seed2 = seedByPassphrase(passphrase + ".");
        BigInteger q = random(pAndQLength, seed2);

        BigInteger phi = p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));

        BigInteger modulo = p.multiply(q);
        this.publicKey = new PublicKey(modulo, length);

        BigInteger privateExponent = this.publicKey.exponent.modInverse(phi);
        this.privateKey = new PrivateKey(modulo, privateExponent, length);
    }

    @Getter
    public static class PublicKey {
        private int length;
        private BigInteger modulo;
        private final BigInteger exponent = new BigInteger(String.valueOf(EXPONENT));

        public PublicKey(BigInteger modulo, int length) {
            this.modulo = modulo;
            this.length = length;
        }
    }

    @Getter
    public static class PrivateKey {
        private int length;
        private BigInteger modulo;
        private BigInteger privateExponent;

        public PrivateKey(BigInteger modulo, BigInteger privateExponent, int length) {
            this.modulo = modulo;
            this.privateExponent = privateExponent;
            this.length = length;
        }
    }

    private static long seedByPassphrase(String passphrase) {
        BitSet hash = SignatureProvider.hash(passphrase, 9);
        BigInteger integer = new BigInteger(1, hash.toByteArray());
        return integer.longValueExact();
    }

    private static BigInteger random(int n, long seed) {
        return probablePrime(n, new Random(seed));
    }

}