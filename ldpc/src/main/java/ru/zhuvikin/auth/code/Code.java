package ru.zhuvikin.auth.code;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.zhuvikin.auth.matrix.sparse.LUDecomposition;
import ru.zhuvikin.auth.matrix.sparse.Matrix;

@Getter
@EqualsAndHashCode
public class Code {

    private int length;
    private int rank;

    @Setter
    private Matrix parityCheckMatrix;
    @Setter
    private LUDecomposition generatorMatrix;

    public Code() {
    }

    Code(int length, int rank) {
        this.length = length;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "LDPC {" + length + ", " + rank + '}';
    }

}
