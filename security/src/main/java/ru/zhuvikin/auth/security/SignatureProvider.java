package ru.zhuvikin.auth.security;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.BitSet;
import java.util.List;

public final class SignatureProvider {

    public static BitSet sign(List<Integer> quantizedFeatures, RsaKeys.PrivateKey privateKey) {
        int signatureLength = privateKey.getLength();

        BitSet hash = hash(quantizedFeatures, signatureLength);
        BigInteger hashInteger = new BigInteger(1, hash.toByteArray());

        BigInteger modulo = privateKey.getModulo();
        BigInteger privateExponent = privateKey.getPrivateExponent();

        BigInteger signature = hashInteger.modPow(privateExponent, modulo);
        return BitSet.valueOf(signature.toByteArray());
    }

    public static boolean verify(List<Integer> quantizedFeatures, RsaKeys.PublicKey publicKey, BitSet signature) {
        int signatureLength = publicKey.getLength();

        BitSet hash = hash(quantizedFeatures, signatureLength);

        BigInteger signatureInteger = new BigInteger(1, signature.toByteArray());

        BigInteger modulo = publicKey.getModulo();
        BigInteger exponent = publicKey.getExponent();

        BigInteger checked = signatureInteger.modPow(exponent, modulo);
        BitSet checkBits = BitSet.valueOf(checked.toByteArray());

        return checkBits.equals(hash);
    }

    @SneakyThrows
    public static BitSet hash(String message, int length) {
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
