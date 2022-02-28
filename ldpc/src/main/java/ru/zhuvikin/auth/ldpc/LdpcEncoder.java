package ru.zhuvikin.auth.ldpc;

import ru.zhuvikin.auth.code.BitSequence;
import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.code.Encoder;
import ru.zhuvikin.auth.matrix.Element;
import ru.zhuvikin.auth.matrix.LUDecomposition;
import ru.zhuvikin.auth.matrix.Matrix;
import ru.zhuvikin.auth.matrix.Vector;

import java.util.ArrayList;
import java.util.List;

import static ru.zhuvikin.auth.ldpc.ParityCheckMatrix.generate;
import static ru.zhuvikin.auth.matrix.EquationSolver.backwardSubstitution;
import static ru.zhuvikin.auth.matrix.EquationSolver.forwardSubstitution;

public class LdpcEncoder implements Encoder {

    @Override
    public List<BitSequence> encode(Code code, BitSequence bitSequence) {
        prepareCode(code);

        Integer length = code.getLength();
        Integer rank = code.getRank();

        int blockLength = rank - length;

        int words = (int) Math.ceil(bitSequence.getLength() / blockLength);
        int bits = words * blockLength;

        BitSequence bitSet = new BitSequence(bits);
        bitSequence.forEach(bitSet::set);

        List<BitSequence> result = new ArrayList<>();
        for (int i = 0; i < words; i++) {
            BitSequence blockBits = bitSet.subSequence(i * blockLength, (i + 1) * blockLength);
            BitSequence word = encodeBlock(blockBits, code);
            result.add(word);
        }
        return result;
    }

    @Override
    public BitSequence decode(Code code, List<BitSequence> codeWords) {
        return codeWords.stream()
                .map(codeWord -> decode(code, codeWord))
                .reduce(BitSequence::concatenate)
                .orElse(new BitSequence(0));
    }

    private void prepareCode(Code code) {
        int length = code.getLength();
        int rank = code.getRank();

        if (code.getParityCheckMatrix() == null) {
            Matrix parityCheckMatrix = generate(rank, length, code.getSeed());
            code.setParityCheckMatrix(parityCheckMatrix);
            code.setGeneratorMatrix(null);
        }

        if (code.getGeneratorMatrix() == null) {
            LUDecomposition generatorMatrix = code.getParityCheckMatrix().decompose();
            code.setGeneratorMatrix(generatorMatrix);
        }
    }

    private BitSequence encodeBlock(BitSequence blockBits, Code code) {
        Integer length = code.getLength();
        Integer rank = code.getRank();

        Vector x = new Vector(length, true);

        Matrix H = code.getParityCheckMatrix();
        LUDecomposition generatorMatrix = code.getGeneratorMatrix();

        List<Integer> columns = generatorMatrix.getColumns();

        // Multiply the vector of source bits by the systematic columns of the
        // parity check matrix, giving x. Also copy these bits to the coded block.
        for (int j = length; j < rank; j++) {
            if (blockBits.isSet(j - length)) {
                for (Element e = H.firstInColumn(columns.get(j)); e.bottom() != null; e = e.bottom()) {
                    int row = e.getRow();
                    if (x.isSet(row)) {
                        x.remove(row);
                    } else {
                        x.set(row);
                    }
                }
            }
        }

        // Solve Ly=x for y by forward substitution, then U(cblk)=y by backward substitution.
        Vector y = forwardSubstitution(generatorMatrix, x);
        Vector z = backwardSubstitution(generatorMatrix, y);

        BitSequence wordBitSequence = new BitSequence(rank);

        for (int i = length; i < rank; i++) {
            if (blockBits.isSet(i - length)) {
                z.set(columns.get(i));
            }
        }

        z.foreach((i, e) -> wordBitSequence.set(i));
        return wordBitSequence;
    }

    private BitSequence decode(Code code, BitSequence codeWord) {
        int length = code.getLength();
        int rank = code.getRank();

        return new BitSequence(rank - length);
    }

}
