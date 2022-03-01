package ru.zhuvikin.auth.code;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Consumer;

@Getter
@Setter
public class BitSequence {

    private int length;
    private NavigableSet<Integer> bits = new TreeSet<>();

    public BitSequence(int length) {
        this.length = length;
    }

    public BitSequence set(int index) {
        checkBounds(index);
        bits.add(index);
        return this;
    }

    public BitSequence subSequence(int start, boolean startInclusive, int end, boolean endInclusive) {
        checkBounds(startInclusive ? start : start + 1);
        checkBounds(endInclusive ? end : end - 1);
        BitSequence sequence = new BitSequence(end - start);
        sequence.setAll(bits.subSet(start, startInclusive, end, endInclusive));
        return sequence;
    }

    public BitSequence subSequence(int start, int end) {
        return subSequence(start, true, end, false);
    }

    public BitSequence remove(int index) {
        checkBounds(index);
        bits.remove(index);
        return this;
    }

    public boolean isSet(int index) {
        checkBounds(index);
        return bits.contains(index);
    }

    public void setAll(Collection<Integer> indicies) {
        bits.addAll(indicies);
    }

    private void checkBounds(int index) {
        if (index < 0 || index > length - 1) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bit sequence bounds");
        }
    }

    public void forEach(Consumer<Integer> action) {
        bits.forEach(action);
    }

    public NavigableSet<Integer> getBits() {
        return bits;
    }

    public BitSequence concatenate(BitSequence bitSequence) {
        BitSequence sequence = new BitSequence(length + bitSequence.getLength());
        for (Integer index : bits) {
            sequence.set(index);
        }
        for (Integer index : bitSequence.getBits()) {
            sequence.set(length + index);
        }
        return sequence;
    }

    @Override
    public BitSequence clone() {
        BitSequence sequence = new BitSequence(length);
        bits.forEach(sequence::set);
        return sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BitSequence)) return false;

        BitSequence that = (BitSequence) o;

        if (length != that.length) return false;
        return bits.equals(that.bits);
    }

    @Override
    public int hashCode() {
        int result = length;
        result = 31 * result + bits.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(bits.contains(i) ? "1" : "0");
            if (i < length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }
}
