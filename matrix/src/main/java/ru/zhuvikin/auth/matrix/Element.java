package ru.zhuvikin.auth.matrix;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class Element {

    private int column;
    private int row;

    private Matrix matrix;

    double probabilityRatio;
    double likelihoodRatio;

    public Element(int column, int row, Matrix matrix) {
        this.column = column;
        this.row = row;
        this.matrix = matrix;
    }

    public Element right() {
        if (column < 0) {
            return null;
        }
        Map.Entry<Integer, Element> entry = matrix.getRows().get(row).higherEntry(column);
        if (entry == null) {
            return new Element(-1, row, matrix);
        }
        return entry.getValue();
    }

    public Element left() {
        if (column < 0) {
            return null;
        }
        Map.Entry<Integer, Element> entry = matrix.getRows().get(row).lowerEntry(column);
        if (entry == null) {
            return new Element(-1, row, matrix);
        }
        return entry.getValue();
    }

    public Element bottom() {
        if (row < 0) {
            return null;
        }
        Map.Entry<Integer, Element> entry = matrix.getColumns().get(column).higherEntry(row);
        if (entry == null) {
            return new Element(column, -1, matrix);
        }
        return entry.getValue();
    }

    public Element top() {
        if (row < 0) {
            return null;
        }
        Map.Entry<Integer, Element> entry = matrix.getColumns().get(column).lowerEntry(row);
        if (entry == null) {
            return new Element(column, -1, matrix);
        }
        return entry.getValue();
    }

    public void remove() {
        matrix.remove(column, row);
    }

    @Override
    public String toString() {
        return "{" + column + ", " + row + '}';
    }
}
