package ru.zhuvikin.auth.matrix;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@Getter
@Setter
public class Modulo2Matrix implements Matrix {

    private int width;
    private int height;

    private NavigableMap<Integer, NavigableMap<Integer, Element>> columns = new TreeMap<>();
    private NavigableMap<Integer, NavigableMap<Integer, Element>> rows = new TreeMap<>();

    public Modulo2Matrix(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private Modulo2Matrix(int width, int height, NavigableMap<Integer, NavigableMap<Integer, Element>> columns, NavigableMap<Integer, NavigableMap<Integer, Element>> rows) {
        this.width = width;
        this.height = height;
        this.columns = columns;
        this.rows = rows;
    }


    private void checkBounds(int column, int row) {
        checkColumnIndex(column, this);
        checkRowIndex(row, this);
    }

    private void checkColumnIndex(int column, Matrix matrix) {
        if (column < 0 || column >= matrix.getWidth()) {
            throw new IndexOutOfBoundsException("Column index '" + column + "' exceed the width of the matrix");
        }
    }

    private void checkRowIndex(int row, Matrix matrix) {
        if (row < 0 || row >= matrix.getHeight()) {
            throw new IndexOutOfBoundsException("Row index '" + row + "' exceed the height of the matrix");
        }
    }

    private void checkOperandDimensions(Matrix matrix) {
        checkOperandWidth(matrix);
        checkOperandHeight(matrix);
    }

    private void checkOperandHeight(Matrix matrix) {
        if (matrix.getWidth() != getWidth()) {
            throw new RuntimeException("Width of the matrices should be equal");
        }
    }

    private void checkOperandWidth(Matrix matrix) {
        if (matrix.getHeight() != getHeight()) {
            throw new RuntimeException("Height of the matrices should be equal");
        }
    }

    @Override
    public boolean isSet(int column, int row) {
        checkBounds(column, row);
        return columns.containsKey(column) && columns.get(column).containsKey(row);
    }

    @Override
    public Element get(int column, int row) {
        if (isSet(column, row)) {
            return columns.get(column).get(row);
        }
        return null;
    }

    @Override
    public Element firstInRow(int row) {
        if (!rows.containsKey(row)) {
            return null;
        }
        Map.Entry<Integer, Element> firstEntry = rows.get(row).firstEntry();
        if (firstEntry != null) {
            return firstEntry.getValue();
        }
        return null;
    }

    @Override
    public Element firstInColumn(int column) {
        if (!columns.containsKey(column)) {
            return null;
        }
        Map.Entry<Integer, Element> firstEntry = columns.get(column).firstEntry();
        if (firstEntry != null) {
            return firstEntry.getValue();
        }
        return null;
    }

    @Override
    public Matrix set(int column, int row) {
        checkBounds(column, row);
        setElement(column, row);
        return this;
    }

    @Override
    public Matrix remove(int column, int row) {
        checkBounds(column, row);
        if (isSet(column, row)) {
            columns.get(column).remove(row);
            rows.get(row).remove(column);
        }
        NavigableMap<Integer, Element> activeColumn = columns.get(column);
        if (activeColumn.isEmpty()) {
            columns.remove(column);
        }
        NavigableMap<Integer, Element> activeRow = rows.get(row);
        if (activeRow.isEmpty()) {
            rows.remove(row);
        }
        return this;
    }

    @Override
    public Matrix add(Matrix matrix) {
        checkOperandDimensions(matrix);
        Matrix result = clone();
        for (Integer columnIndex : matrix.getColumns().keySet()) {
            NavigableMap<Integer, Element> column = matrix.getColumns().get(columnIndex);
            for (Integer rowIndex : column.keySet()) {
                if (isSet(columnIndex, rowIndex)) {
                    result.remove(columnIndex, rowIndex);
                } else {
                    result.set(columnIndex, rowIndex);
                }
            }
        }
        return result;
    }

    @Override
    public Matrix subtract(Matrix matrix) {
        return add(matrix);
    }

    @Override
    public Matrix multiply(Matrix matrix) {
        if (width != matrix.getHeight()) {
            throw new RuntimeException("Height of right matrix isn't equal to the width of the left one");
        }
        Modulo2Matrix result = new Modulo2Matrix(matrix.getWidth(), getHeight());

        for (int i = 0; i < result.getHeight(); i++) {
            Element firstInRow = firstInRow(i);
            if (firstInRow == null || firstInRow.bottom() == null) {
                continue;
            }

            for (int j = 0; j < result.getWidth(); j++) {
                boolean b = false;

                Element e1 = firstInRow;
                Element e2 = matrix.firstInColumn(j);

                if (e2 != null) {
                    while (e1.right() != null && e2.bottom() != null) {
                        if (e1.getColumn() == e2.getRow()) {
                            b = !b;
                            e1 = e1.right();
                            e2 = e2.bottom();
                        } else if (e1.getColumn() <= e2.getRow()) {
                            e1 = e1.right();
                        } else {
                            e2 = e2.bottom();
                        }
                    }
                }

                if (b) {
                    result.set(j, i);
                }
            }
        }
        return result;
    }

    @Override
    public Matrix addRow(int toRow, Matrix matrix, int row) {
        checkRowIndex(toRow, this);
        checkRowIndex(row, matrix);

        NavigableMap<Integer, Element> targetRow = rows.get(toRow);
        NavigableMap<Integer, Element> fromRow = matrix.getRows().get(row);

        for (Integer columnIndex : fromRow.keySet()) {
            if (targetRow.containsKey(columnIndex)) {
                remove(columnIndex, toRow);
            } else {
                set(columnIndex, toRow);
            }
        }
        return this;
    }

    @Override
    public Matrix transpose() {
        Matrix clone = clone();
        return new Modulo2Matrix(clone.getHeight(), clone.getWidth(), clone.getRows(), clone.getColumns());
    }

    @Override
    public NavigableMap<Integer, Element> rowEntries(int row) {
        checkRowIndex(row, this);
        return rows.get(row);
    }

    @Override
    public NavigableMap<Integer, Element> columnEntries(int column) {
        checkColumnIndex(column, this);
        return columns.get(column);
    }

    @Override
    public LUDecomposition decompose() {
        int subMatrixDimension = width < height ? width : height;

        Matrix left = new Modulo2Matrix(subMatrixDimension, height);
        Matrix upper = new Modulo2Matrix(width, subMatrixDimension);
        Matrix B = clone();

        Element e = null, f, fn;

        int i, j, k;

        boolean found;

        int[] rinv = new int[height];
        int[] cinv = new int[width];

        int[] rows = new int[height];
        int[] columns = new int[width];

        for (i = 0; i < height; i++) {
            rinv[i] = i;
            rows[i] = i;
        }

        for (j = 0; j < width; j++) {
            cinv[j] = j;
            columns[j] = j;
        }


        for (i = 0; i < subMatrixDimension; i++) {
            found = false;
            for (k = i; k < width; k++) {
                e = B.firstInColumn(columns[k]);
                if (e != null) {
                    while (e.bottom() != null) {
                        if (rinv[e.getRow()] >= i) {
                            found = true;
                            break;
                        }
                        e = e.bottom();
                    }
                    if (found) {
                        break;
                    }
                }
            }

            if (found) {
                if (cinv[e.getColumn()] != k) {
                    throw new RuntimeException("Invalid result");
                }

                columns[k] = columns[i];
                columns[i] = e.getColumn();

                cinv[columns[k]] = k;
                cinv[columns[i]] = i;

                k = rinv[e.getRow()];

                if (k < i) {
                    throw new RuntimeException("Invalid result: k(" + k + ") < i(" + i + ")");
                }

                rows[k] = rows[i];
                rows[i] = e.getRow();

                rinv[rows[k]] = k;
                rinv[rows[i]] = i;
            }

            // Update L, U, and B
            f = B.firstInColumn(columns[i]);
            if (f != null) {
                while (f.bottom() != null) {
                    fn = f.bottom();
                    k = f.getRow();

                    if (rinv[k] > i) {
                        B.addRow(k, B, e.getRow());
                        left.set(i, k);
                    } else if (rinv[k] < i) {
                        upper.set(columns[i], rinv[k]);
                    } else {
                        left.set(i, k);
                        upper.set(columns[i], i);
                    }
                    f = fn;
                }

                // Get rid of all entries in the current column of B, just to save space.
                for (; ; ) {
                    f = B.firstInColumn(columns[i]);
                    if (f == null || f.bottom() == null) {
                        break;
                    }
                    f.remove();
                }
            }
        }

        for (i = subMatrixDimension; i < height; i++) {
            for (; ; ) {
                f = left.firstInRow(rows[i]);
                if (f == null || f.right() == null) {
                    break;
                }
                f.remove();
            }
        }

        return new LUDecomposition(left, upper);
    }

    private void setElement(int column, int row) {
        Element element = new Element(column, row, this);

        columns.putIfAbsent(column, new TreeMap<>());
        columns.get(column).put(row, element);

        rows.putIfAbsent(row, new TreeMap<>());
        rows.get(row).put(column, element);
    }

    @Override
    public Matrix clone() {
        Modulo2Matrix result = new Modulo2Matrix(getWidth(), getHeight());
        NavigableMap<Integer, NavigableMap<Integer, Element>> cloneColumns = cloneEntries(result, columns);
        NavigableMap<Integer, NavigableMap<Integer, Element>> cloneRows = cloneEntries(result, rows);
        result.setColumns(cloneColumns);
        result.setRows(cloneRows);
        return result;
    }

    private NavigableMap<Integer, NavigableMap<Integer, Element>> cloneEntries(Modulo2Matrix result, NavigableMap<Integer, NavigableMap<Integer, Element>> entries1) {
        NavigableMap<Integer, NavigableMap<Integer, Element>> cloneColumns = new TreeMap<>();
        for (Integer row : entries1.keySet()) {
            TreeMap<Integer, Element> values = new TreeMap<>(entries1.get(row));
            for (Element element : values.values()) {
                element.setMatrix(result);
            }
            cloneColumns.put(row, values);
        }
        return cloneColumns;
    }

    @Override
    public boolean equals(Object o) {
        if (!this.getClass().equals(o.getClass())) {
            return false;
        }
        Matrix matrix = (Matrix) o;

        if (matrix.getWidth() != this.width) {
            return false;
        }

        if (matrix.getHeight() != this.height) {
            return false;
        }

        if (columns.size() != matrix.getColumns().size()) {
            return false;
        }

        if (rows.size() != matrix.getRows().size()) {
            return false;
        }

        for (Integer columnIndex : columns.keySet()) {
            if (columns.get(columnIndex).size() != matrix.getColumns().get(columnIndex).size()) {
                return false;
            }
            for (Integer rowIndex : columns.get(columnIndex).keySet()) {
                if (!matrix.getColumns().get(columnIndex).containsKey(rowIndex)) {
                    return false;
                }
            }
        }

        for (Integer rowIndex : rows.keySet()) {
            if (rows.get(rowIndex).size() != matrix.getRows().get(rowIndex).size()) {
                return false;
            }
            for (Integer columnIndex : rows.get(rowIndex).keySet()) {
                if (!matrix.getRows().get(rowIndex).containsKey(columnIndex)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Matrix(").append(width).append(", ").append(height).append(") :\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isSet(x, y)) {
                    out.append("1 ");
                } else {
                    out.append("0 ");
                }
            }
            out.append("\n");
        }
        out.append("\n");
        return out.toString();
    }

}
