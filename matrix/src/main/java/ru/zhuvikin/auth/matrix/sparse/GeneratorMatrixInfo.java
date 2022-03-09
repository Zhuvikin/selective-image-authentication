package ru.zhuvikin.auth.matrix.sparse;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class GeneratorMatrixInfo implements Serializable {

    private LUDecomposition luDecomposition;

    private int sourceWidth;
    private int sourceHeight;

    private List<Integer> rows;
    private List<Integer> columns;

    @SneakyThrows
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(bos);

        stream.writeInt(('G' << 8) + 0x80);
        stream.writeByte('s');

        stream.writeInt(sourceHeight);
        stream.writeInt(sourceWidth);

        for (int i = 0; i < sourceWidth; i++) {
            stream.writeInt(columns.get(i));
        }
        for (int i = 0; i < sourceHeight; i++) {
            stream.writeInt(rows.get(i));
        }

        stream.write(MatrixUtility.serialize(luDecomposition.getLeft()));
        stream.write(MatrixUtility.serialize(luDecomposition.getUpper()));
        return bos.toByteArray();
    }

    @SneakyThrows
    public static GeneratorMatrixInfo deserialize(byte[] bytes) {
        LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream(bytes));

        int g = stream.readInt();
        if (g != ('G' << 8) + 0x80) {
            throw new IllegalStateException("Failed to read generation matrix");
        }

        byte s = stream.readByte();
        if (s != 's') {
            throw new IllegalStateException("Specified matrix is not sparse");
        }

        int height = stream.readInt();
        int width = stream.readInt();

        List<Integer> columns = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            columns.add(stream.readInt());
        }
        List<Integer> rows = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            rows.add(stream.readInt());
        }

        Matrix left = MatrixUtility.deserialize(stream);
        Matrix upper = MatrixUtility.deserialize(stream);

        return new GeneratorMatrixInfo(new LUDecomposition(left, upper), width, height, rows, columns);
    }
}
