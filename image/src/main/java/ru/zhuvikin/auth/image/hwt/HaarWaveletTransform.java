package ru.zhuvikin.auth.image.hwt;

import static java.lang.System.arraycopy;

public final class HaarWaveletTransform {

    private static int getHaarMaxCycles(int hw) {
        int cycles = 0;
        while (hw > 1) {
            cycles++;
            hw /= 2;
        }
        return cycles;
    }

    private static boolean isCycleAllowed(int maxCycle, int cycles) {
        return cycles <= maxCycle;
    }

    public static double[] doHaar1DFWTransform(double[] pixels, int cycles) {
        int w = pixels.length;
        int maxCycle = getHaarMaxCycles(w);
        boolean isCycleAllowed = isCycleAllowed(maxCycle, cycles);
        if (isCycleAllowed) {
            double[] tempPixels = new double[w];
            for (int i = 0; i < cycles; i++) {
                for (int j = 0; j < w; j++) {
                    tempPixels[j] = (pixels[2 * j] + pixels[2 * j + 1]) / 2;
                    tempPixels[j + w] = (pixels[2 * j] - pixels[2 * j + 1]) / 2;
                }
                w /= 2;
            }
            return tempPixels;
        }
        return null;
    }

    public static double[][] doHaar2DFWTransform(double[][] pixels, int cycles) {
        int w = pixels[0].length;
        int h = pixels.length;
        int maxCycle = getHaarMaxCycles(w);
        boolean isCycleAllowed = isCycleAllowed(maxCycle, cycles);
        if (isCycleAllowed) {
            double[][] ds = new double[h][w];
            double[][] tempds = new double[h][w];
            for (int i = 0; i < pixels.length; i++) {
                arraycopy(pixels[i], 0, ds[i], 0, pixels[0].length);
            }
            for (int i = 0; i < cycles; i++) {
                w /= 2;
                for (int j = 0; j < h; j++) {
                    for (int k = 0; k < w; k++) {
                        double a = ds[j][2 * k];
                        double b = ds[j][2 * k + 1];
                        double add = a + b;
                        double sub = a - b;
                        double avgAdd = add / 2;
                        double avgSub = sub / 2;
                        tempds[j][k] = avgAdd;
                        tempds[j][k + w] = avgSub;
                    }
                }
                for (int j = 0; j < h; j++) {
                    for (int k = 0; k < w; k++) {
                        ds[j][k] = tempds[j][k];
                        ds[j][k + w] = tempds[j][k + w];
                    }
                }
                h /= 2;
                for (int j = 0; j < w; j++) {
                    for (int k = 0; k < h; k++) {
                        double a = ds[2 * k][j];
                        double b = ds[2 * k + 1][j];
                        double add = a + b;
                        double sub = a - b;
                        double avgAdd = add / 2;
                        double avgSub = sub / 2;
                        tempds[k][j] = avgAdd;
                        tempds[k + h][j] = avgSub;
                    }
                }
                for (int j = 0; j < w; j++) {
                    for (int k = 0; k < h; k++) {
                        ds[k][j] = tempds[k][j];
                        ds[k + h][j] = tempds[k + h][j];
                    }
                }
            }
            return ds;
        }
        return null;
    }

    public static double[][] doHaar2DInvTransform(double[][] pixels, int cycles) {
        int w = pixels[0].length;
        int h = pixels.length;
        int maxCycle = getHaarMaxCycles(w);
        boolean isCycleAllowed = isCycleAllowed(maxCycle, cycles);
        if (isCycleAllowed) {
            double[][] ds = new double[h][w];
            double[][] tempds = new double[h][w];
            for (int i = 0; i < pixels.length; i++) {
                arraycopy(pixels[i], 0, ds[i], 0, pixels[0].length);
            }
            int hh = h / (int) Math.pow(2, cycles);
            int ww = w / (int) Math.pow(2, cycles);
            for (int i = cycles; i > 0; i--) {
                for (int j = 0; j < ww; j++) {
                    for (int k = 0; k < hh; k++) {
                        double a = ds[k][j];
                        double b = ds[k + hh][j];
                        double add = a + b;
                        double sub = a - b;
                        tempds[2 * k][j] = add;
                        tempds[2 * k + 1][j] = sub;
                    }
                }
                for (int j = 0; j < ww; j++) {
                    for (int k = 0; k < hh; k++) {
                        ds[2 * k][j] = tempds[2 * k][j];
                        ds[2 * k + 1][j] = tempds[2 * k + 1][j];
                    }
                }
                hh *= 2;
                for (int j = 0; j < hh; j++) {
                    for (int k = 0; k < ww; k++) {
                        double a = ds[j][k];
                        double b = ds[j][k + ww];
                        double add = a + b;
                        double sub = a - b;
                        tempds[j][2 * k] = add;
                        tempds[j][2 * k + 1] = sub;
                    }
                }
                for (int j = 0; j < hh; j++) {
                    for (int k = 0; k < ww; k++) {
                        ds[j][2 * k] = tempds[j][2 * k];
                        ds[j][2 * k + 1] = tempds[j][2 * k + 1];
                    }
                }
                ww *= 2;
            }
            return ds;
        }
        return null;
    }

}
