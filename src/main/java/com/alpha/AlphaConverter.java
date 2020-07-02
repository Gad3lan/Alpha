package com.alpha;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class AlphaConverter {
    BufferedImage bufferedImage;
    App.AlphaColor alphaColor;
    int threshold;
    boolean whiteAlpha;

    AlphaConverter(Image img, App.AlphaColor alphaColor, double threshold, boolean whiteAlpha) {
        bufferedImage = SwingFXUtils.fromFXImage(img, null);
        if (alphaColor == null) {
            alphaColor = App.AlphaColor.RGB;
        }
        this.alphaColor = alphaColor;
        this.threshold = (int) (threshold * 2.55);
        this.whiteAlpha = whiteAlpha;
    }

    Image convert() {
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int c = bufferedImage.getRGB(x, y);
                int a = (c>>24)&0xff;
                int r = (c>>16)&0xff;
                int g = (c>>8)&0xff;
                int b = c&0xff;
                if (a > threshold) {
                    switch (alphaColor) {
                        case RGB -> a = (r + g + b) / 3;
                        case R -> a = r;
                        case G -> a = g;
                        case B -> a = b;
                        default -> a = 0;
                    }
                    if (a > threshold) {
                        a = 255;
                    }
                    if (whiteAlpha) {
                        a = 255 - a;
                    }
                }
                c = (a<<24) | (r<<16) | (g<<8) | b;
                bufferedImage.setRGB(x, y, c);
            }
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}

