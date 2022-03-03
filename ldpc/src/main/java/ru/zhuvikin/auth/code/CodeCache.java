package ru.zhuvikin.auth.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.zhuvikin.auth.ldpc.ParityCheckMatrix;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@AllArgsConstructor
public class CodeCache {

    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Code>> CODES = new ConcurrentHashMap<>();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static long seed = 1;

    static {
        loadCodeCaches();
    }

    public static Code of(int length, int rank) {
        return getCode(length, rank);
    }

    private static void generateMatrices(Code code) {
        code.setParityCheckMatrix(ParityCheckMatrix.generate(code.getRank(), code.getLength(), seed));
        code.setGeneratorMatrix(code.getParityCheckMatrix().decompose());
    }

    private static Code getCode(int blockLength, int rank) {
        Code code;
        boolean newCache = false;
        if (CODES.containsKey(rank)) {
            ConcurrentHashMap<Integer, Code> rateCodes = CODES.get(rank);
            if (rateCodes.containsKey(blockLength)) {
                code = rateCodes.get(blockLength);
            } else {
                code = new Code(blockLength, rank);
                rateCodes.put(blockLength, code);
                newCache = true;
            }
        } else {
            code = new Code(blockLength, rank);
            CODES.put(rank, new ConcurrentHashMap<>());
            CODES.get(rank).put(blockLength, code);
            newCache = true;
        }

        if (newCache) {
            generateMatrices(code);
            cacheCode(code);
        }
        return code;
    }

    @SneakyThrows
    private static void cacheCode(Code code) {
        String cacheFolder = checkCacheFolder();

        String codeFileName = "LDPC_" + code.getRank() + "_" + code.getLength() + ".code";
        File codeFile = new File(cacheFolder + File.separator + codeFileName);

        String contents = JSON_MAPPER.writeValueAsString(code);
        if (!codeFile.createNewFile()) {
            System.err.println("Failed to create code file in " + codeFile.getAbsolutePath());
        }

        FileWriter fw = new FileWriter(codeFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(contents);
        bw.close();
    }

    @SneakyThrows
    private static void loadCodeCaches() {
        String cacheFolderPath = checkCacheFolder();
        File cacheFolder = new File(cacheFolderPath);
        File[] files = cacheFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String contents = new String(Files.readAllBytes(file.toPath()));
                    Code code = JSON_MAPPER.readValue(contents, Code.class);
                    CODES.putIfAbsent(code.getRank(), new ConcurrentHashMap<>());
                    ConcurrentHashMap<Integer, Code> map = CODES.get(code.getRank());

                    code.setParityCheckMatrix(restoreMatrix(code.getParityCheckMatrix()));
                    code.getGeneratorMatrix().setLeft(restoreMatrix(code.getGeneratorMatrix().getLeft()));
                    code.getGeneratorMatrix().setUpper(restoreMatrix(code.getGeneratorMatrix().getUpper()));

                    map.putIfAbsent(code.getLength(), code);
                }
            }
        }
    }

    private static Matrix restoreMatrix(Matrix matrix) {
        Matrix matrixRestored = new Modulo2Matrix(matrix.getWidth(), matrix.getHeight());
        matrix.getColumns().values().forEach(c -> c.values().forEach(e -> matrixRestored.set(e.getColumn(), e.getRow())));
        return matrixRestored;
    }

    private static String checkCacheFolder() {
        String userHome = System.getProperty("user.home");
        String cacheFolder = userHome + File.separator + ".selective-image-authentication" + File.separator + "cache";
        File cacheFolderFile = new File(cacheFolder);
        if (!cacheFolderFile.exists()) {
            if (!cacheFolderFile.mkdirs()) {
                System.err.println("Failed to create cache folder in " + cacheFolder);
            }
        }
        return cacheFolder;
    }

}
