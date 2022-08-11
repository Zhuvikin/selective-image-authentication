package ru.zhuvikin.auth.matrix.sparse.modulo2;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.zhuvikin.auth.matrix.sparse.Element;
import ru.zhuvikin.auth.matrix.sparse.GeneratorMatrixInfo;
import ru.zhuvikin.auth.matrix.sparse.LUDecomposition;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.MatrixUtility;
import ru.zhuvikin.auth.matrix.sparse.Vector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class Modulo2Matrix implements Matrix {

    private int width;
    private int height;

    private NavigableMap<Integer, NavigableMap<Integer, Element>> columns = new TreeMap<>();
    private NavigableMap<Integer, NavigableMap<Integer, Element>> rows = new TreeMap<>();

    public Modulo2Matrix() {
    }

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

    public static Matrix deserialize(byte[] bytes) {
        return MatrixUtility.deserialize(bytes);
    }

    @SneakyThrows
    public static Matrix deserializeAsParityCheckMatrix(byte[] bytes) {
        LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream(bytes));
        int p = stream.readInt();
        if (p != ('P' << 8) + 0x80) {
            throw new IllegalStateException("Failed to read parity-check matrix");
        }
        return MatrixUtility.deserialize(stream);
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
    public Element lastInRow(int row) {
        if (!rows.containsKey(row)) {
            return null;
        }
        Map.Entry<Integer, Element> lastEntry = rows.get(row).lastEntry();
        if (lastEntry != null) {
            return lastEntry.getValue();
        }
        return null;
    }

    @Override
    public Element lastInColumn(int column) {
        if (!columns.containsKey(column)) {
            return null;
        }
        Map.Entry<Integer, Element> lastEntry = columns.get(column).lastEntry();
        if (lastEntry != null) {
            return lastEntry.getValue();
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
        if (activeColumn != null && activeColumn.isEmpty()) {
            columns.remove(column);
        }
        NavigableMap<Integer, Element> activeRow = rows.get(row);
        if (activeRow != null && activeRow.isEmpty()) {
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
    public Vector multiply(Vector vector) {
        Vector result = new Vector(height, true);
        for (int j = 0; j < vector.size(); j++) {
            if (vector.isSet(j)) {
                for (Element e = firstInColumn(j); e.bottom() != null; e = e.bottom()) {
                    int row = e.getRow();
                    if (result.isSet(row)) {
                        result.remove(e.getRow());
                    } else {
                        result.set(row);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Matrix addRow(int toRow, Matrix matrix, int row) {
        if (width < matrix.getWidth()) {
            throw new RuntimeException("Row added to is shorter than row added from");
        }

        if (toRow < 0 || toRow >= height || row < 0 || row >= matrix.getHeight()) {
            throw new RuntimeException("Row index out of range");
        }

        Element ft;
        Element f1 = firstInRow(toRow);
        Element f2 = matrix.firstInRow(row);

        while (f1.right() != null && f2.right() != null) {
            if (f1.getColumn() > f2.getColumn()) {
                set(f2.getColumn(), toRow);
                f2 = f2.right();
            } else {
                ft = f1.right();
                if (f1.getColumn() == f2.getColumn()) {
                    f1.remove();
                    f2 = f2.right();
                }
                f1 = ft;
            }
        }

        while (f2.right() != null) {
            set(f2.getColumn(), toRow);
            f2 = f2.right();
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
    public GeneratorMatrixInfo getGenerationMatrixInfo() {
        System.out.println("Start matrix LU decomposition");
        int subMatrixDimension = width < height ? width : height;

        Matrix left = new Modulo2Matrix(subMatrixDimension, height);
        Matrix upper = new Modulo2Matrix(width, subMatrixDimension);
        Matrix B = clone();

        List<Integer> rows = IntStream.range(0, height).boxed().collect(Collectors.toList());
        List<Integer> columns = IntStream.range(0, width).boxed().collect(Collectors.toList());

        List<Integer> rowsInv = new ArrayList<>(rows);
        List<Integer> columnsInv = new ArrayList<>(columns);

        for (int i = 0; i < subMatrixDimension; i++) {
//            System.out.println(100d * (double) i / (double) subMatrixDimension + " %");
            boolean found = false;
            Element e = null, first, next;
            int k;
            for (k = i; k < width; k++) {
                e = B.firstInColumn(columns.get(k));
                if (e != null) {
                    while (e.bottom() != null) {
                        if (rowsInv.get(e.getRow()) >= i) {
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
                if (columnsInv.get(e.getColumn()) != k) {
                    throw new RuntimeException("Invalid result");
                }

                columns.set(k, columns.get(i));
                columns.set(i, e.getColumn());

                columnsInv.set(columns.get(k), k);
                columnsInv.set(columns.get(i), i);

                k = rowsInv.get(e.getRow());

                if (k < i) {
                    throw new RuntimeException("Invalid result: k(" + k + ") < i(" + i + ")");
                }

                rows.set(k, rows.get(i));
                rows.set(i, e.getRow());

                rowsInv.set(rows.get(k), k);
                rowsInv.set(rows.get(i), i);
            }

            first = B.firstInColumn(columns.get(i));
            if (first != null) {
                while (first.bottom() != null) {
                    next = first.bottom();
                    k = first.getRow();
                    if (rowsInv.get(k) > i && e != null) {
                        B.addRow(k, B, e.getRow());
                        left.set(i, k);
                    } else if (rowsInv.get(k) < i) {
                        upper.set(columns.get(i), rowsInv.get(k));
                    } else {
                        left.set(i, k);
                        upper.set(columns.get(i), i);
                    }
                    first = next;
                }
            }
        }

        System.out.println("Remove not needed bits");
        for (int i = subMatrixDimension; i < height; i++) {
            for (; ; ) {
                Element first = left.firstInRow(rows.get(i));
                if (first == null || first.right() == null) {
                    break;
                }
                first.remove();
            }
        }

        System.out.println("LU decomposition is finished");
        LUDecomposition luDecomposition = new LUDecomposition(left, upper);
        return new GeneratorMatrixInfo(luDecomposition, width, height, rows, columns);
    }

    @Override
    public LUDecomposition decompose() {
        return getGenerationMatrixInfo().getLuDecomposition();
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
        for (Integer column : columns.keySet()) {
            for (Integer row : columns.get(column).keySet()) {
                result.set(column, row);
            }
        }
        return result;
    }

    @Override
    public byte[] serialize() {
        return MatrixUtility.serialize(this);
    }

    @Override
    @SneakyThrows
    public byte[] serializeAsParityCheckMatrix() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(bos);
        stream.writeInt(('P' << 8) + 0x80);
        stream.write(serialize());
        return bos.toByteArray();
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
        return "Modulo2Matrix(" + width + ", " + height + ")";
    }

}
