package ru.zhuvikin.auth.watermarking;

import java.awt.image.BufferedImage;
import java.util.BitSet;

import static ru.zhuvikin.auth.image.hwt.HaarWaveletTransform.doHaar2DFWTransform;
import static ru.zhuvikin.auth.image.hwt.HaarWaveletTransform.doHaar2DInvTransform;

public final class StenographyEmbedded {

    private static final int HWT_LEVELS = 3;

    public static BufferedImage embed(BufferedImage image, BitSet bitSet, int length, double gamma) {
        int width = image.getWidth();
        int height = image.getHeight();

        double[][] pixels = getPixels(image);
        double[][] domain = doHaar2DFWTransform(pixels, HWT_LEVELS);

        if (domain == null) {
            throw new RuntimeException("Failed to perform Haar Wavelet Transform");
        }

        boolean[][] map2x = mapData(bitSet, length, 64, 128);
        embedToDomains(gamma, domain, map2x);

        double[][] inverted = doHaar2DInvTransform(domain, HWT_LEVELS);

        if (inverted == null) {
            throw new RuntimeException("Failed to perform Inverse Haar Wavelet Transform");
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int luminance = (int) Math.round(inverted[i][j]);
                if (luminance > 255) luminance = 255;
                if (luminance < 0) luminance = 0;
                java.awt.Color c = new java.awt.Color(luminance, luminance, luminance);
                result.setRGB(i, j, c.getRGB());
            }
        }
        return result;
    }

    public static BitSet extract(BufferedImage image, int length, double gamma) {
        double[][] pixels = getPixels(image);
        double[][] domain = doHaar2DFWTransform(pixels, HWT_LEVELS);

        if (domain == null) {
            throw new RuntimeException("Failed to perform Haar Wavelet Transform");
        }

        BitSet data = new BitSet();
        int bitIndex = 0;

        extractFromDomain(gamma, domain, data, bitIndex, 0, 64, 64, 128);
        extractFromDomain(gamma, domain, data, bitIndex, 64, 128, 0, 64);

        return data;
    }

    private static void extractFromDomain(double gamma, double[][] domain, BitSet data, int bitIndex, int fromJ, int toJ, int fromI, int toI) {
        for (int j = fromJ; j < toJ; j++) {
            for (int i = fromI; i < toI; i++) {
                if (domain[i][j] - gamma * (Math.round(domain[i][j] / gamma)) >= 0) {
                    data.set(bitIndex);
                }
                bitIndex++;
            }
        }
    }

    private static boolean[][] mapData(BitSet binary, int length, int w, int h) {
        boolean[][] result = new boolean[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) result[y][x] = binary.get((y * w + x) % length);
        }
        return result;
    }

    private static double[][] getPixels(BufferedImage image) {
        double[][] pixels = new double[image.getWidth()][image.getHeight()];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                java.awt.Color c = new java.awt.Color(image.getRGB(i, j));
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                pixels[i][j] = (r + g + b) / 3;
            }
        }
        return pixels;
    }

    private static void embedToDomains(double gamma, double[][] domain, boolean[][] map2x) {
        // embed into HL3
        for (int i = 64; i < 128; i++) {
            for (int j = 0; j < 64; j++) {
                if (map2x[j][i - 64]) {
                    domain[i][j] = gamma * ((double) Math.round(domain[i][j] / gamma) + 0.25f);
                } else {
                    domain[i][j] = gamma * ((double) Math.round(domain[i][j] / gamma) - 0.25f);
                }
            }
        }

        System.out.println();
        // embed into LH3
        for (int i = 0; i < 64; i++) {
            for (int j = 64; j < 128; j++) {
                if (map2x[j][i]) {
                    domain[i][j] = gamma * ((double) Math.round(domain[i][j] / gamma) + 0.25f);
                } else {
                    domain[i][j] = gamma * ((double) Math.round(domain[i][j] / gamma) - 0.25f);
                }
            }
        }
    }

}
