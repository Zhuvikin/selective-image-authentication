package ru.zhuvikin.auth.matrix.sparse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.NavigableMap;

@Getter
@Setter
@EqualsAndHashCode
public class Element {

    private int column;
    private int row;

    @JsonIgnore
    private Matrix matrix;

    @JsonIgnore
    double probabilityRatio;
    @JsonIgnore
    double likelihoodRatio;

    public Element() {
    }

    public Element(int column, int row, Matrix matrix) {
        this.column = column;
        this.row = row;
        this.matrix = matrix;
    }

    public Element right() {
        if (column < 0) {
            return null;
        }
        NavigableMap<Integer, NavigableMap<Integer, Element>> rows = matrix.getRows();
        NavigableMap<Integer, Element> map = rows.get(row);
        if (map == null) {
            return getBoundRowElement(row, matrix);
        }
        Map.Entry<Integer, Element> entry = map.higherEntry(column);
        if (entry == null) {
            return getBoundRowElement(row, matrix);
        }
        return entry.getValue();
    }

    public Element left() {
        if (column < 0) {
            return null;
        }
        NavigableMap<Integer, NavigableMap<Integer, Element>> rows = matrix.getRows();
        NavigableMap<Integer, Element> map = rows.get(row);
        if (map == null) {
            return getBoundRowElement(row, matrix);
        }
        Map.Entry<Integer, Element> entry = map.lowerEntry(column);
        if (entry == null) {
            return getBoundRowElement(row, matrix);
        }
        return entry.getValue();
    }

    public Element bottom() {
        if (row < 0) {
            return null;
        }
        NavigableMap<Integer, NavigableMap<Integer, Element>> columns = matrix.getColumns();
        NavigableMap<Integer, Element> map = columns.get(column);
        if (map == null) {
            return getColumnBoundElement(column, matrix);
        }
        Map.Entry<Integer, Element> entry = map.higherEntry(row);
        if (entry == null) {
            return getColumnBoundElement(column, matrix);
        }
        return entry.getValue();
    }

    public Element top() {
        if (row < 0) {
            return null;
        }
        NavigableMap<Integer, NavigableMap<Integer, Element>> columns = matrix.getColumns();
        NavigableMap<Integer, Element> map = columns.get(column);
        if (map == null) {
            return getColumnBoundElement(column, matrix);
        }
        Map.Entry<Integer, Element> entry = map.lowerEntry(row);
        if (entry == null) {
            return getColumnBoundElement(column, matrix);
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

    private static Element getColumnBoundElement(int column, Matrix matrix) {
        return new Element(column, -1, matrix);
    }

    private static Element getBoundRowElement(int row, Matrix matrix) {
        return new Element(-1, row, matrix);
    }

}
