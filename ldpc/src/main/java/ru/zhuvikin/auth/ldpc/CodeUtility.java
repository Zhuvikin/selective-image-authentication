package ru.zhuvikin.auth.ldpc;

public final class CodeUtility {

    public static Code getCode(long k, long n) {
        Code code = new Code();
        code.setK(k);
        code.setN(n);

        // todo: generate parity check matrix
        // todo: generate generator matrix

        return code;
    }

}
