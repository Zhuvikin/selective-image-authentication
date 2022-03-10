package ru.zhuvikin.auth.code;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.zhuvikin.auth.matrix.sparse.GeneratorMatrixInfo;
import ru.zhuvikin.auth.matrix.sparse.Matrix;

@Getter
@EqualsAndHashCode
public class Code {

    private int rank;
    private int length;

    @Setter
    private Matrix parityCheckMatrix;
    @Setter
    private GeneratorMatrixInfo generatorMatrix;

    public Code() {
    }

    Code(int rank, int length) {
        this.length = length;
        this.rank = rank;
    }

    public static Code of(int rank, int length) {
        return CodeCache.getCode(rank, length);
    }

    @Override
    public String toString() {
        return "LDPC {" + rank + ", " + length + '}';
    }

}
