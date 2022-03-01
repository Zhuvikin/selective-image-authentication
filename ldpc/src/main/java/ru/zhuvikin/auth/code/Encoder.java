package ru.zhuvikin.auth.code;

import ru.zhuvikin.auth.matrix.sparse.modulo2.BitSequence;

import java.util.List;

public interface Encoder {

    List<BitSequence> encode(Code code, BitSequence bitSequence);

    BitSequence decode(Code code, List<BitSequence> codeWords);

}
