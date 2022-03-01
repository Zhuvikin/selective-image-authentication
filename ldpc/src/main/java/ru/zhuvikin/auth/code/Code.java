package ru.zhuvikin.auth.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.zhuvikin.auth.matrix.sparse.LUDecomposition;
import ru.zhuvikin.auth.matrix.sparse.Matrix;

@Getter
@AllArgsConstructor
public class Code {

    private int length;
    private int rank;

    @Setter
    private long seed = 1;

    @Setter
    private Matrix parityCheckMatrix;

    @Setter
    private LUDecomposition generatorMatrix;

    public Code(int length, int rank) {
        this.length = length;
        this.rank = rank;
    }

    public Code(int length, int rank, long seed) {
        this(length, rank);
        this.seed = seed;
    }

    @Override
    public String toString() {
        return "LDPC {" + length + ", " + rank + '}';
    }
}
