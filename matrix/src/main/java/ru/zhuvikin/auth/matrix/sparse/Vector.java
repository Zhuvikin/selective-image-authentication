package ru.zhuvikin.auth.matrix.sparse;

import lombok.Getter;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.util.function.BiConsumer;

@Getter
public class Vector {

    private boolean vertical;
    private Matrix matrix;

    public Vector(int length, boolean vertical) {
        Modulo2Matrix matrix;
        this.vertical = vertical;
        if (vertical) {
            matrix = new Modulo2Matrix(1, length);
        } else {
            matrix = new Modulo2Matrix(length, 1);
        }
        this.matrix = matrix;
    }

    public Vector set(int index) {
        if (vertical) {
            matrix.set(0, index);
        } else {
            matrix.set(index, 0);
        }
        return this;
    }

    public boolean isSet(int index) {
        if (vertical) {
            return matrix.isSet(0, index);
        } else {
            return matrix.isSet(index, 0);
        }
    }

    public Vector remove(int index) {
        if (vertical) {
            matrix.remove(0, index);
        } else {
            matrix.remove(index, 0);
        }
        return this;
    }

    public int size() {
        if (vertical) {
            return matrix.getHeight();
        } else {
            return matrix.getWidth();
        }
    }

    public void foreach(BiConsumer<Integer, Element> action) {
        (vertical ? matrix.getColumns() : matrix.getRows()).get(0).forEach(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;

        Vector vector = (Vector) o;

        if (vertical != vector.vertical) return false;
        return matrix != null ? matrix.equals(vector.matrix) : vector.matrix == null;
    }

    @Override
    public int hashCode() {
        int result = (vertical ? 1 : 0);
        result = 31 * result + (matrix != null ? matrix.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (vertical) {
            return matrix.transpose().toString();
        } else {
            return matrix.toString();
        }
    }

    public int cardinality() {
        if (vertical) {
            return matrix.getRows().size();
        } else {
            return matrix.getColumns().size();
        }
    }

}
