package ru.zhuvikin.auth.ldpc;

import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.matrix.sparse.Element;
import ru.zhuvikin.auth.matrix.sparse.LUDecomposition;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.Vector;
import ru.zhuvikin.auth.matrix.sparse.modulo2.BitSequence;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static ru.zhuvikin.auth.ldpc.ParityCheckMatrix.generate;
import static ru.zhuvikin.auth.matrix.sparse.EquationSolver.backwardSubstitution;
import static ru.zhuvikin.auth.matrix.sparse.EquationSolver.forwardSubstitution;

public final class LdpcEncoder {

    private static final int MAX_ITERATION = 500;

    private static final double BSC_ERROR_PROBABILITY = 0.1d;

    public static BitSet encode(Code code, BitSet messageBits, int bitsLength) {
        prepareCode(code);
        int length = code.getLength();
        int rank = code.getRank();
        int blockLength = rank - length;
        int words = (int) Math.ceil(bitsLength / blockLength);
        int bits = words * blockLength;

        BitSequence bitSet = new BitSequence(bits);
        for (int i = 0; i < bitsLength; i++) {
            if (messageBits.get(i)) {
                bitSet.set(i);
            }
        }

        BitSet encoded = new BitSet();
        for (int wordIndex = 0; wordIndex < words; wordIndex++) {
            BitSequence blockBits = bitSet.subSequence(wordIndex * blockLength, (wordIndex + 1) * blockLength);
            BitSet word = encodeBlock(blockBits, code);
            for (int i = 0; i < rank; i++) {
                if (word.get(i)) {
                    encoded.set(rank * wordIndex + i);
                }
            }
        }
        return encoded;
    }

    public static BitSet decode(Code code, BitSet encoded, int bitsLength) {
        int rank = code.getRank();
        int blockCount = bitsLength / rank;
        List<BitSequence> encodedBlocks = new ArrayList<>();
        for (int blockIndex = 0; blockIndex < blockCount; blockIndex++) {
            BitSequence block = new BitSequence(rank);
            for (int i = 0; i < rank; i++) {
                int encodedBitIndex = blockIndex * rank + i;
                if (encoded.get(encodedBitIndex)) {
                    block.set(i);
                }
            }
            encodedBlocks.add(block);
        }

        BitSequence bitSequence = encodedBlocks.stream()
                .map(codeWord -> decode(code, codeWord))
                .reduce(BitSequence::concatenate)
                .orElse(new BitSequence(0));

        BitSet decoded = new BitSet();
        bitSequence.forEach(decoded::set);
        return decoded;
    }

    private static void prepareCode(Code code) {
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

    private static BitSet encodeBlock(BitSequence blockBits, Code code) {
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

        BitSet wordBitSequence = new BitSet(rank);

        for (int i = length; i < rank; i++) {
            if (blockBits.isSet(i - length)) {
                z.set(columns.get(i));
            }
        }

        z.foreach((i, e) -> wordBitSequence.set(i));
        return wordBitSequence;
    }

    private static BitSequence decode(Code code, BitSequence codeWord) {
        int M = code.getLength();
        int N = code.getRank();

        boolean[] dblk = new boolean[N];
        double[] lratio = new double[N];
        double[] bitpr = new double[N];

        int iterations;        // Unsigned because can be huge for enum
        double changed;    // Double because can be fraction if lratio==1

        Matrix parityCheckMatrix = code.getParityCheckMatrix();
        LUDecomposition generatorMatrix = code.getGeneratorMatrix();
        List<Integer> columns = generatorMatrix.getColumns();

        // Find likelihood ratio for each bit
        for (int i = 0; i < N; i++) {
            lratio[i] = codeWord.isSet(i) ?
                    (1 - BSC_ERROR_PROBABILITY) / BSC_ERROR_PROBABILITY :
                    BSC_ERROR_PROBABILITY / (1 - BSC_ERROR_PROBABILITY);
        }

        // Try to decode using the specified method.
        iterations = propagationDecode(parityCheckMatrix, lratio, dblk, bitpr);
        System.out.println("Performed iteration: " + iterations);

        // See if it worked, and how many bits were changed.
        boolean valid = check(parityCheckMatrix, dblk);
        System.out.println(valid ? "Block valid" : "Block is invalid");

        changed = changed(lratio, dblk, N);
        System.out.println("Changed bits: " + changed);

        BitSequence bitSequence = new BitSequence(N - M);
        for (int i = M; i < N; i++) {
            if (dblk[columns.get(i)]) {
                bitSequence.set(i - M);
            }
        }
        return bitSequence;
    }

    private static double changed(double[] likelihoodRatios, boolean[] decodingCandidate, int numberOfBits) {
        double changed = 0;
        for (int j = 0; j < numberOfBits; j++) {
            if (likelihoodRatios[j] == 1) {
                changed += 0.5;
            } else if (decodingCandidate[j] != (likelihoodRatios[j] > 1)) {
                changed += 1;
            }
        }
        return changed;
    }

    private static int propagationDecode(Matrix parityCheckMatrix, double[] likelihoodRatios, boolean[] decoded, double[] bitProbabilities) {
        // Initialize probability and likelihood ratios, and find initial guess
        initPropagation(parityCheckMatrix, likelihoodRatios, decoded, bitProbabilities);

        // Do up to abs(MAX_ITERATION) iterations of probability propagation, stopping
        // early if a codeword is found, unless MAX_ITERATION is negative.
        int iterations = MAX_ITERATION;
        int iteration;
        for (iteration = 0; ; iteration++) {
            boolean c = check(parityCheckMatrix, decoded);
            if (iteration == iterations || iteration == -iterations || c) {
                break;
            }
            iteratePropagation(parityCheckMatrix, likelihoodRatios, decoded, bitProbabilities);
        }
        return iteration;
    }

    private static boolean check(Matrix parityCheckMatrix, boolean[] codewordGuess) {
        Matrix matrix = new Modulo2Matrix(1, codewordGuess.length);
        for (int i = 0; i < codewordGuess.length; i++) {
            if (codewordGuess[i]) {
                matrix.set(0, i);
            }
        }
        Matrix syndrome = parityCheckMatrix.multiply(matrix);
        return syndrome.getColumns().isEmpty();
    }

    private static void initPropagation(Matrix parityCheckMatrix, double[] likelihoodRatios, boolean[] decoded, double[] bitProbabilities) {
        for (int j = 0; j < parityCheckMatrix.getWidth(); j++) {
            for (Element e = parityCheckMatrix.firstInColumn(j); e.bottom() != null; e = e.bottom()) {
                e.setProbabilityRatio(likelihoodRatios[j]);
                e.setLikelihoodRatio(1);
            }
            bitProbabilities[j] = 1 - 1 / (1 + likelihoodRatios[j]);
            decoded[j] = likelihoodRatios[j] >= 1;
        }
    }

    private static void iteratePropagation(Matrix parityCheckMatrix, double[] likelihoodRatios, boolean[] decoded, double[] bitProbabilities) {
        // Recompute likelihood ratios
        for (int i = 0; i < parityCheckMatrix.getHeight(); i++) {
            double dl = 1;
            for (Element e = parityCheckMatrix.firstInRow(i); e.right() != null; e = e.right()) {
                e.setLikelihoodRatio(dl);
                dl *= 2 / (1 + e.getProbabilityRatio()) - 1;
            }
            dl = 1;
            for (Element e = parityCheckMatrix.lastInRow(i); e.left() != null; e = e.left()) {
                double t = e.getLikelihoodRatio() * dl;
                e.setLikelihoodRatio((1 - t) / (1 + t));
                dl *= 2 / (1 + e.getProbabilityRatio()) - 1;
            }
        }

        // Recompute probability ratios
        // Also find the next guess based on the individually most likely values
        for (int j = 0; j < parityCheckMatrix.getWidth(); j++) {
            double pr = likelihoodRatios[j];
            for (Element e = parityCheckMatrix.firstInColumn(j); e.bottom() != null; e = e.bottom()) {
                e.setProbabilityRatio(pr);
                pr *= e.getLikelihoodRatio();
            }
            // todo: check if is undefined    if (isnan(pr)) { pr = 1; }

            bitProbabilities[j] = 1 - 1 / (1 + pr);
            decoded[j] = pr >= 1;
            pr = 1;
            for (Element e = parityCheckMatrix.lastInColumn(j); e.top() != null; e = e.top()) {
                e.setProbabilityRatio(e.getProbabilityRatio() * pr);
                // todo: check if is undefined     if (isnan(e.getProbabilityRatio())) { e.setProbabilityRatio(1); }
                pr *= e.getLikelihoodRatio();
            }
        }
    }

}
