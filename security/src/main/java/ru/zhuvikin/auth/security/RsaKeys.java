package ru.zhuvikin.auth.security;

import lombok.Getter;

import java.math.BigInteger;

public class RsaKeys {

    private static final long EXPONENT = 65537;

    @Getter
    private BigInteger n;

    @Getter
    private BigInteger d;

    @Getter
    private final BigInteger e = new BigInteger(String.valueOf(EXPONENT));

    public RsaKeys(BigInteger p, BigInteger q) {
        BigInteger phi = p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
        this.n = p.multiply(q);
        this.d = e.modInverse(phi);
    }

}