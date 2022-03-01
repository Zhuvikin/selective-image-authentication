package ru.zhuvikin.auth.security;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static java.math.BigInteger.probablePrime;

public final class SignatureProvider {

    public static BitSet sign(List<Integer> quantizedFeatures, String passphrase, int signatureLength) {
        int pAndQLength = (int) Math.floor(signatureLength / 2);

        long seed1 = seedByPassphrase(passphrase);
        BigInteger p = random(pAndQLength, seed1);

        long seed2 = seedByPassphrase(passphrase + ".");
        BigInteger q = random(pAndQLength, seed2);

        RsaKeys keys = new RsaKeys(p, q);

        BitSet hash = hash(quantizedFeatures, signatureLength);
        BigInteger hashInteger = new BigInteger(1, hash.toByteArray());

        BigInteger n = keys.getN();
        BigInteger d = keys.getD();

        BigInteger signature = hashInteger.modPow(d, n);
        return BitSet.valueOf(signature.toByteArray());
    }

    public static boolean verify(List<Integer> quantizedFeatures, String passphrase, BitSet signature) {
        BitSet check = sign(quantizedFeatures, passphrase, signature.length());
        return check.equals(signature);
    }

    private static BigInteger random(int n, long seed) {
        return probablePrime(n, new Random(seed));
    }

    private static long seedByPassphrase(String passphrase) {
        BitSet hash = hash(passphrase, 9);
        BigInteger integer = new BigInteger(1, hash.toByteArray());
        return integer.longValueExact();
    }

    @SneakyThrows
    private static BitSet hash(String message, int length) {
        BitSet bitSet = BitSet.valueOf(message.getBytes("UTF-8"));
        return hash(bitSet, length);
    }

    @SneakyThrows
    private static BitSet hash(List<Integer> message, int length) {
        BitSet bitSet = BitSet.valueOf(message.toString().getBytes("UTF-8"));
        return hash(bitSet, length);
    }

    @SneakyThrows
    private static BitSet hash(BitSet message, int length) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");

        BitSet bitSet = new BitSet(length);
        bitSet.set(0, length, false);

        message.stream().forEach(bitSet::set);
        BitSet digest = BitSet.valueOf(md.digest(bitSet.toByteArray()));

        BitSet result = new BitSet(length);
        for (int i = 0; i < length; i++) {
            result.set(i, digest.get(i % 512));
        }
        return result;
    }

}
