package ru.zhuvikin.auth.code;

import java.util.List;

public interface Encoder {

    List<BitSequence> encode(Code code, BitSequence bitSequence);

    BitSequence decode(Code code, List<BitSequence> codeWords);

}
