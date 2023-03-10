package ru.zhuvikin.auth.matrix.sparse;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.NavigableMap;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Matrix extends Serializable {

    int getWidth();

    int getHeight();

    NavigableMap<Integer, NavigableMap<Integer, Element>> getColumns();

    NavigableMap<Integer, NavigableMap<Integer, Element>> getRows();

    boolean isSet(int column, int row);

    Element firstInRow(int row);

    Element firstInColumn(int column);

    Element lastInRow(int row);

    Element lastInColumn(int column);

    Matrix set(int column, int row);

    Matrix remove(int column, int row);

    Matrix add(Matrix matrix);

    Matrix subtract(Matrix matrix);

    Matrix multiply(Matrix matrix);

    Vector multiply(Vector vector);

    Matrix addRow(int toRow, Matrix matrix, int row);

    Matrix transpose();

    NavigableMap<Integer, Element> rowEntries(int row);

    NavigableMap<Integer, Element> columnEntries(int column);

    GeneratorMatrixInfo getGenerationMatrixInfo();

    LUDecomposition decompose();

    Matrix clone();

    byte[] serialize();

    byte[] serializeAsParityCheckMatrix();

}
