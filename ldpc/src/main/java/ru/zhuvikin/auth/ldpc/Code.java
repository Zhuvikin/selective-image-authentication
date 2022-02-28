package ru.zhuvikin.auth.ldpc;

import lombok.Getter;
import lombok.Setter;
import ru.zhuvikin.auth.matrix.Modulo2Matrix;

@Getter
@Setter
public class Code {

    private long k;
    private long n;
    private Modulo2Matrix parityCheckMatrix;
    private Modulo2Matrix generatorMatrix;

}
