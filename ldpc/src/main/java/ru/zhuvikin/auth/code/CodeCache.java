package ru.zhuvikin.auth.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import ru.zhuvikin.auth.ldpc.ParityCheckMatrix;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Getter
@AllArgsConstructor
public class CodeCache {

    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Code>> CODES = new ConcurrentHashMap<>();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final String LDPC_JAR = "ldpc.jar";
    private static final String CACHE_FOLDER_NAME = "cache";
    private static final String LDPC_FILE_PREFIX = "LDPC";
    private static final String CODE_FILE_EXTENSION = "code";

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
        if (CODES.containsKey(rank)) {
            ConcurrentHashMap<Integer, Code> rateCodes = CODES.get(rank);
            if (rateCodes.containsKey(blockLength)) {
                code = rateCodes.get(blockLength);
            } else {
                code = generateNewCode(blockLength, rank);
                rateCodes.put(blockLength, code);
                cacheCode(code);
            }
        } else {
            code = generateNewCode(blockLength, rank);
            CODES.put(rank, new ConcurrentHashMap<>());
            CODES.get(rank).put(blockLength, code);
            cacheCode(code);
        }
        return code;
    }

    static Code generateNewCode(int blockLength, int rank) {
        Code result = new Code(blockLength, rank);
        generateMatrices(result);
        return result;
    }

    @SneakyThrows
    private static void cacheCode(Code code) {
        String cacheFolder = checkCacheFolder();

        String codeFileName = LDPC_FILE_PREFIX + "_" + code.getRank() + "_" + code.getLength() + "." + CODE_FILE_EXTENSION;
        File codeFile = new File(cacheFolder + separator + codeFileName);

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

        ClassLoader classLoader = CodeCache.class.getClassLoader();
        URL internalCacheFolder = classLoader.getResource("cache");
        if (internalCacheFolder == null) {
            throw new RuntimeException("Failed to load cached LDPC codes");
        }
        URI uri = internalCacheFolder.toURI();
        if (uri.getPath() != null) {
            File cache = new File(uri);
            File[] cacheFiles = cache.listFiles();
            if (cacheFiles != null) {
                for (File cacheFile : cacheFiles) {
                    Path path = cacheFile.toPath();
                    Path fileName = path.getFileName();
                    Files.copy(path, Paths.get(cacheFolderPath + separator + fileName), REPLACE_EXISTING);
                }
            }
        } else {
            File jarFile = new File(CodeCache.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String actualFile = jarFile.getParentFile().getAbsolutePath() + separator + LDPC_JAR;
            final JarFile jar = new JarFile(actualFile);
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                final String name = jarEntry.getName();
                if (name.startsWith(CACHE_FOLDER_NAME + separator + LDPC_FILE_PREFIX)) {
                    String fileName = name.replaceFirst(CACHE_FOLDER_NAME + separator, "");
                    InputStream stream = classLoader.getResourceAsStream(CACHE_FOLDER_NAME + separator + fileName);
                    Path path = Paths.get(cacheFolderPath + separator + fileName);
                    FileUtils.copyInputStreamToFile(stream, path.toFile());
                }
            }
            jar.close();
        }

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
        String cacheFolder = userHome + separator + ".selective-image-authentication" + separator + CACHE_FOLDER_NAME;
        File cacheFolderFile = new File(cacheFolder);
        if (!cacheFolderFile.exists()) {
            if (!cacheFolderFile.mkdirs()) {
                System.err.println("Failed to create cache folder in " + cacheFolder);
            }
        }
        return cacheFolder;
    }

}
