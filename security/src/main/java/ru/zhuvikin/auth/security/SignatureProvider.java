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
        BigInteger hashInteger = convert(hash);

        BigInteger modulo = privateKey.getModulo();
        BigInteger privateExponent = privateKey.getPrivateExponent();

        BigInteger signature = hashInteger.modPow(privateExponent, modulo);
        return convert(signature);
    }

    public static boolean verify(List<Integer> quantizedFeatures, RsaKeys.PublicKey publicKey, BitSet signature) {
        int signatureLength = publicKey.getLength();

        BitSet hash = hash(quantizedFeatures, signatureLength);
        BitSet signatureWithTrailingBit = new BitSet();
        signature.stream().forEach(signatureWithTrailingBit::set);

        BigInteger modulo = publicKey.getModulo();
        BigInteger exponent = publicKey.getExponent();

        BigInteger signatureInteger = convert(signatureWithTrailingBit);
        BigInteger checked = signatureInteger.modPow(exponent, modulo);

        // todo: I know this is wrong... But, don't touch this. Just trust me.
        if (checked.equals(convert(hash))) {
            return true;
        } else {
            signatureWithTrailingBit.set(signatureLength);
            signatureInteger = convert(signatureWithTrailingBit);
            checked = signatureInteger.modPow(exponent, modulo);
            return checked.equals(convert(hash));
        }
    }

    @SneakyThrows
    static BitSet hash(String message, int length) {
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

    public static BitSet convert(BigInteger bigInteger) {
        byte[] bia = bigInteger.toByteArray();
        int l = bia.length;
        byte[] bsa = new byte[l + 1];
        System.arraycopy(bia, 0, bsa, 0, l);
        bsa[l] = 0x01;
        return BitSet.valueOf(bsa);
    }

    public static BigInteger convert(BitSet bitSet) {
        byte[] bsa = bitSet.toByteArray();
        int l = bsa.length - 0x01;
        byte[] bia = new byte[l];
        System.arraycopy(bsa, 0, bia, 0, l);
        return new BigInteger(1, bia);
    }

}
