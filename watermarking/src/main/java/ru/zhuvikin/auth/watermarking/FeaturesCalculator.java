package ru.zhuvikin.auth.watermarking;

import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class FeaturesCalculator {

    public static List<Double> features(BufferedImage bufferedImage, double sigma, int length) {
        Pair<BufferedImage, BufferedImage> cfd = getCfd(bufferedImage, sigma, length);
        BufferedImage hBuffer = cfd.getKey();
        BufferedImage vBuffer = cfd.getValue();
        List<Double> features = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            int x = i % hBuffer.getWidth();
            int y = i / hBuffer.getWidth();
            int h = hBuffer.getRGB(x, y) & 0xFF;
            int v = vBuffer.getRGB(x, y) & 0xFF;
            features.add(Math.sqrt(h * h + v * v));
        }
        return features;
    }

    private static float[] gaussianKernel(double radius, int direction) {
        if (radius == 0) {
            float[] kernel0 = new float[1];
            kernel0[0] = 1;
            return kernel0;
        }
        int r0 = (int) (1.3d * radius);
        float[] kernel = new float[(2 * r0 + 1) * (2 * r0 + 1)];
        for (int i = 0; i < kernel.length; i++) {
            int x = i % (2 * r0 + 1) - r0;
            int y = i / (2 * r0 + 1) - r0;
            if (direction == 0)
                kernel[i] = 1f * (float) (x * Math.exp(-1 * (Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(radius, 2))) / (2 * Math.PI * Math.pow(radius, 3)));
            else
                kernel[i] = 1f * (float) (y * Math.exp(-1 * (Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(radius, 2))) / (2 * Math.PI * Math.pow(radius, 3)));
            kernel[i] = 1f * kernel[i];
        }
        return kernel;
    }

    private static BufferedImage convolve(BufferedImage image, int direction, double sigma) {
        float[] blurredElements = gaussianKernel(sigma, direction);

        double[][] kernel = new double[(int) Math.sqrt(blurredElements.length)][(int) Math.sqrt(blurredElements.length)];
        for (int i = 0; i < blurredElements.length; i++) {
            int x = i % (int) Math.sqrt(blurredElements.length);
            int y = (int) Math.floor(i / (int) Math.sqrt(blurredElements.length));
            kernel[x][y] = blurredElements[i];
        }

        double data[][] = new double[image.getWidth()][image.getHeight()];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                data[x][y] = image.getRGB(x, y) & 0xFF;
            }
        }

        double result[][] = convolve(data, kernel);

        double min = result[0][0];
        double max = result[0][0];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (result[x][y] < min) min = result[x][y];
                if (result[x][y] > max) max = result[x][y];
            }
        }

        double limit = 10.0f;
        if (max > limit) max = limit;
        if (min < -1 * limit) min = -1 * limit;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int luminance = (int) ((result[x][y] - min) * 255.0f / (max - min));
                if (luminance > 255) luminance = 255;
                if (luminance < 0) luminance = 0;
                Color color = new Color(luminance, luminance, luminance);
                image.setRGB(x, y, color.getRGB());
            }
        }

        return image;
    }

    public static BufferedImage getGrayScale(BufferedImage inputImage) {
        BufferedImage img = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();
        return img;
    }

    private static Pair<BufferedImage, BufferedImage> getCfd(BufferedImage bufferedImage, double sigma, int length) {
        BufferedImage grayScale = getGrayScale(bufferedImage);

        BufferedImage horD = convolve(grayScale, 0, sigma);
        BufferedImage verD = convolve(grayScale, 1, sigma);

        int dim = (int) Math.ceil(Math.sqrt(length));
        return new Pair<>(getScaledImage(horD, dim, dim), getScaledImage(verD, dim, dim));
    }

    private static BufferedImage getScaledImage(BufferedImage src, int w, int h) {
        double factor;
        if (src.getWidth() > src.getHeight()) factor = ((double) src.getHeight() / (double) src.getWidth());
        else factor = ((double) src.getWidth() / (double) src.getHeight());

        BufferedImage resizedImg = new BufferedImage((int) (w * factor), (int) (h * factor), BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, (int) (h * factor), (int) (w * factor), null);
        g2.dispose();
        return resizedImg;
    }

    private static double[][] convolve(double[][] input, double[][] kernel) {
        double[][] output;
        if (input.length > 0 && input[0].length > 0) {
            output = new double[input[0].length][input.length];
            int r = (kernel.length - 1) / 2;
            for (int y = 0; y < input.length; y++) {
                for (int x = 0; x < input[0].length; x++) {
                    double acc = 0;
                    if (x - r >= 0 && x + r < input.length && y - r >= 0 && y + r < input[0].length) {
                        for (int j = -r; j <= r; j++) {
                            for (int i = -r; i <= r; i++) {
                                acc += input[x + i][y + j] * kernel[i + r][j + r];
                            }
                        }
                    }
                    output[x][y] = acc;
                }
            }
        } else {
            output = new double[1][1];
        }
        return output;
    }

}
