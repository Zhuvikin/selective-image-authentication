package ru.zhuvikin.auth.watermarking;

import java.awt.image.BufferedImage;

public interface SelectiveImageAuthentication {

    BufferedImage watermark(BufferedImage image);

    boolean authenticate(BufferedImage image);

}
