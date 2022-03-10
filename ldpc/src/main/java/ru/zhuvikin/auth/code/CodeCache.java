package ru.zhuvikin.auth.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import ru.zhuvikin.auth.ldpc.ParityCheckMatrix;
import ru.zhuvikin.auth.matrix.sparse.GeneratorMatrixInfo;
import ru.zhuvikin.auth.matrix.sparse.Matrix;
import ru.zhuvikin.auth.matrix.sparse.modulo2.Modulo2Matrix;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Getter
@AllArgsConstructor
public class CodeCache {

    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Matrix>> PARITY_CHECK_MATRICES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, GeneratorMatrixInfo>> GENERATOR_MATRIX_INFO = new ConcurrentHashMap<>();

    private static final String LDPC_JAR = "ldpc.jar";
    private static final String CACHE_FOLDER_NAME = "cache";
    private static final String LDPC_FILE_PREFIX = "LDPC";
    private static final String PARITY_CHECK_MATRIX_FILE_EXTENSION = "pchk";
    private static final String GENERATION_MATRIX_FILE_EXTENSION = "gen";

    private static long SEED = 1;

    static Code getCode(int rank, int length) {
        Code result = new Code(rank, length);

        if (PARITY_CHECK_MATRICES.containsKey(rank)) {
            ConcurrentHashMap<Integer, Matrix> rankMatrices = PARITY_CHECK_MATRICES.get(rank);
            if (rankMatrices.containsKey(length)) {
                result.setParityCheckMatrix(rankMatrices.get(length));
            } else {
                return getParityCheckMatrixAndGetCode(rank, length, result);
            }
        } else {
            return getParityCheckMatrixAndGetCode(rank, length, result);
        }

        Matrix parityCheckMatrix = result.getParityCheckMatrix();
        if (GENERATOR_MATRIX_INFO.containsKey(rank)) {
            ConcurrentHashMap<Integer, GeneratorMatrixInfo> rankMatrices = GENERATOR_MATRIX_INFO.get(rank);
            if (rankMatrices.containsKey(length)) {
                result.setGeneratorMatrix(rankMatrices.get(length));
                return result;
            } else {
                return getGenerationMatrixAndGetCode(result, parityCheckMatrix);
            }
        } else {
            return getGenerationMatrixAndGetCode(result, parityCheckMatrix);
        }
    }

    private static Code getParityCheckMatrixAndGetCode(int rank, int length, Code result) {
        Matrix parityCheckMatrix = (Matrix) checkCache(rank, length, Matrix.class);
        if (parityCheckMatrix == null) {
            parityCheckMatrix = ParityCheckMatrix.generate(rank, length, SEED);
            cacheParityCheckMatrix(parityCheckMatrix);
        }
        result.setParityCheckMatrix(parityCheckMatrix);
        return getGenerationMatrixAndGetCode(result, parityCheckMatrix);
    }

    private static Code getGenerationMatrixAndGetCode(Code result, Matrix parityCheckMatrix) {
        int length = parityCheckMatrix.getHeight();
        int rank = parityCheckMatrix.getWidth();
        GeneratorMatrixInfo generatorMatrix = (GeneratorMatrixInfo) checkCache(rank, length, GeneratorMatrixInfo.class);
        if (generatorMatrix == null) {
            generatorMatrix = parityCheckMatrix.getGenerationMatrixInfo();
            cacheGeneratorMatrix(generatorMatrix);
        }
        result.setGeneratorMatrix(generatorMatrix);
        return result;
    }

    @SneakyThrows
    private static void cacheGeneratorMatrix(GeneratorMatrixInfo generatorMatrix) {
        int width = generatorMatrix.getSourceWidth();
        int height = generatorMatrix.getSourceHeight();

        GENERATOR_MATRIX_INFO.putIfAbsent(width, new ConcurrentHashMap<>());
        GENERATOR_MATRIX_INFO.get(width).putIfAbsent(height, generatorMatrix);

        String cacheFolder = checkCacheFolder();

        String codeFileName = LDPC_FILE_PREFIX + "_" + width + "_" + height + "." + GENERATION_MATRIX_FILE_EXTENSION;
        File codeFile = new File(cacheFolder + separator + codeFileName);

        byte[] bytes = generatorMatrix.serialize();
        Files.write(codeFile.toPath(), bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    @SneakyThrows
    private static void cacheParityCheckMatrix(Matrix parityCheckMatrix) {
        int width = parityCheckMatrix.getWidth();
        int height = parityCheckMatrix.getHeight();

        PARITY_CHECK_MATRICES.putIfAbsent(width, new ConcurrentHashMap<>());
        PARITY_CHECK_MATRICES.get(width).putIfAbsent(height, parityCheckMatrix);

        String cacheFolder = checkCacheFolder();

        String codeFileName = LDPC_FILE_PREFIX + "_" + parityCheckMatrix.getWidth() + "_" + parityCheckMatrix.getHeight() + "." + PARITY_CHECK_MATRIX_FILE_EXTENSION;
        File codeFile = new File(cacheFolder + separator + codeFileName);

        byte[] bytes = parityCheckMatrix.serializeAsParityCheckMatrix();
        Files.write(codeFile.toPath(), bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    @SneakyThrows
    private static Serializable checkCache(int rank, int length, Class<? extends Serializable> clazz) {
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
                String name = LDPC_FILE_PREFIX + "_" + rank + "_" + length + ".";
                if (clazz.equals(Matrix.class)) {
                    name += PARITY_CHECK_MATRIX_FILE_EXTENSION;
                } else if (clazz.equals(GeneratorMatrixInfo.class)) {
                    name += GENERATION_MATRIX_FILE_EXTENSION;
                } else {
                    throw new IllegalArgumentException("Failed to check cache of class " + clazz);
                }
                if (file.getName().equals(name)) {
                    if (file.isFile()) {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        if (clazz.equals(Matrix.class)) {
                            return Modulo2Matrix.deserializeAsParityCheckMatrix(bytes);
                        } else if (clazz.equals(GeneratorMatrixInfo.class)) {
                            return GeneratorMatrixInfo.deserialize(bytes);
                        }
                    }
                }
            }
        }
        return null;
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
