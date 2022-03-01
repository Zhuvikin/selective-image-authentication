package ru.zhuvikin.auth.ldpc;

import ru.zhuvikin.auth.code.BitSequence;
import ru.zhuvikin.auth.code.Code;
import ru.zhuvikin.auth.code.Encoder;
import ru.zhuvikin.auth.matrix.Element;
import ru.zhuvikin.auth.matrix.LUDecomposition;
import ru.zhuvikin.auth.matrix.Matrix;
import ru.zhuvikin.auth.matrix.Vector;
import ru.zhuvikin.auth.matrix.modulo2.Modulo2Matrix;

import java.util.ArrayList;
import java.util.List;

import static ru.zhuvikin.auth.ldpc.ParityCheckMatrix.generate;
import static ru.zhuvikin.auth.matrix.EquationSolver.backwardSubstitution;
import static ru.zhuvikin.auth.matrix.EquationSolver.forwardSubstitution;

public class LdpcEncoder implements Encoder {

    private static final int MAX_ITERATION = 500;

    private static final double BSC_ERROR_PROBABILITY = 0.1d;

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

    private double changed(double[] lratio, // Likelihood ratios for bits
                           boolean[] dblk,  // Candidate decoding
                           int n) {         // Number of bits
        double changed = 0;
        for (int j = 0; j < n; j++) {
            if (lratio[j] == 1) {
                changed += 0.5;
            } else {
                if (dblk[j] != (lratio[j] > 1)) {
                    changed += 1;
                }
            }
        }
        return changed;
    }

    private int propagationDecode(Matrix parityCheckMatrix,  // Parity check matrix
                                  double[] lratio,           // Likelihood ratios for bits
                                  boolean[] dblk,               // Place to store decoding
                                  double[] bprb) {           // Place to store bit probabilities)
        // Initialize probability and likelihood ratios, and find initial guess
        initprp(parityCheckMatrix, lratio, dblk, bprb);

        // Do up to abs(MAX_ITERATION) iterations of probability propagation, stopping
        // early if a codeword is found, unless MAX_ITERATION is negative.
        int iterations = MAX_ITERATION;
        int n;
        for (n = 0; ; n++) {
            boolean c = check(parityCheckMatrix, dblk);
            if (n == iterations || n == -iterations || c) {
                break;
            }
            iterprp(parityCheckMatrix, lratio, dblk, bprb);
        }

        return n;
    }

    private boolean check(Matrix parityCheckMatrix, // Parity check matrix
                          boolean[] dblk) {         // Guess for codeword
        Matrix matrix = new Modulo2Matrix(1, dblk.length);
        for (int i = 0; i < dblk.length; i++) {
            if (dblk[i]) {
                matrix.set(0, i);
            }
        }
        Matrix syndrome = parityCheckMatrix.multiply(matrix);
        return syndrome.getColumns().isEmpty();
    }

    private void initprp(Matrix parityCheckMatrix,  // Parity check matrix
                         double[] lratio,           // Likelihood ratios for bits
                         boolean[] dblk,               // Place to store decoding
                         double[] bprb) {           // Place to store bit probabilities
        for (int j = 0; j < parityCheckMatrix.getWidth(); j++) {
            for (Element e = parityCheckMatrix.firstInColumn(j); e.bottom() != null; e = e.bottom()) {
                e.setProbabilityRatio(lratio[j]);
                e.setLikelihoodRatio(1);
            }
            bprb[j] = 1 - 1 / (1 + lratio[j]);
            dblk[j] = lratio[j] >= 1;
        }
    }

    private void iterprp(Matrix parityCheckMatrix,  // Parity check matrix
                         double[] lratio,           // Likelihood ratios for bits
                         boolean[] dblk,            // Place to store decoding
                         double[] bprb) {           // Place to store bit probabilities, 0 if not wanted

        int M = parityCheckMatrix.getHeight();
        int N = parityCheckMatrix.getWidth();

        // Recompute likelihood ratios
        for (int i = 0; i < M; i++) {
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
        for (int j = 0; j < N; j++) {
            double pr = lratio[j];
            for (Element e = parityCheckMatrix.firstInColumn(j); e.bottom() != null; e = e.bottom()) {
                e.setProbabilityRatio(pr);
                pr *= e.getLikelihoodRatio();
            }
            // todo: check if is undefined    if (isnan(pr)) { pr = 1; }

            bprb[j] = 1 - 1 / (1 + pr);
            dblk[j] = pr >= 1;
            pr = 1;
            for (Element e = parityCheckMatrix.lastInColumn(j); e.top() != null; e = e.top()) {
                e.setProbabilityRatio(e.getProbabilityRatio() * pr);
                // todo: check if is undefined     if (isnan(e.getProbabilityRatio())) { e.setProbabilityRatio(1); }
                pr *= e.getLikelihoodRatio();
            }
        }
    }

}
