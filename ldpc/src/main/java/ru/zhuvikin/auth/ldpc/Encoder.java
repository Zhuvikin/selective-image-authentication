package ru.zhuvikin.auth.ldpc;

import java.util.BitSet;

interface Encoder {

    BitSet encode(BitSet source);

}
