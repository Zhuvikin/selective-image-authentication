package ru.zhuvikin.auth.matrix.sparse;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import lombok.SneakyThrows;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MatrixUtility {

    @SneakyThrows
    public static byte[] serialize(Matrix matrix) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(bos);

        stream.writeInt(matrix.getHeight());
        stream.writeInt(matrix.getWidth());

        for (int i = 0; i < matrix.getHeight(); i++) {
            Element e = matrix.firstInRow(i);
            if (e != null && e.right() != null) {
                stream.writeInt(-(i + 1));
                while (e.right() != null) {
                    stream.writeInt(e.getColumn() + 1);
                    e = e.right();
                }
            }
        }
        stream.writeInt(0);
        return bos.toByteArray();
    }

    public static Matrix deserialize(byte[] bytes) {
        LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream(bytes));
        return deserialize(stream);
    }

    @SneakyThrows
    public static Matrix deserialize(LittleEndianDataInputStream stream) {
        int height = stream.readInt();
        if (height <= 0) {
            throw new IllegalStateException("Number of rows should be greater than zero");
        }

        int width = stream.readInt();
        if (width <= 0) {
            throw new IllegalStateException("Number of columns should be greater than zero");
        }

        Matrix matrix = new Modulo2Matrix(width, height);

        int row = -1;
        for (; ; ) {
            int v;
            if (stream.available() >= 4) {
                v = stream.readInt();
            } else if (stream.available() >= 2) {
                v = stream.readShort();
            } else if (stream.available() >= 1) {
                v = stream.readByte();
            } else {
                break;
            }
            if (v == 0) {
                return matrix;
            } else if (v < 0) {
                row = -v - 1;
                if (row >= height) break;
            } else {
                int col = v - 1;
                if (col >= width) break;
                if (row == -1) break;
                matrix.set(col, row);
            }
        }

        throw new IllegalStateException("Failed to read matrix from bytes");
    }

}
