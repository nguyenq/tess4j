/**
 * <a url=http://www.jdeskew.com/>JDeskew</a>
 */
package com.recognition.software.jdeskew;

//import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
//import java.io.File;
//import java.io.IOException;
//import javax.imageio.ImageIO;

public class ImageUtil {

//    public static BufferedImage readImageFile(File imageFile) throws IOException {
//        return ImageIO.read(imageFile);
//    }

    public static boolean isBlack(BufferedImage image, int x, int y) {
        if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            WritableRaster raster = image.getRaster();
            int pixelRGBValue = raster.getSample(x, y, 0);
            if (pixelRGBValue == 0) {
                return true;
            } else {
                return false;
            }
        }

        int luminanceValue = 140;
        return isBlack(image, x, y, luminanceValue);
    }

    public static boolean isBlack(BufferedImage image, int x, int y, int luminanceCutOff) {
        int pixelRGBValue;
        int r;
        int g;
        int b;
        double luminance = 0.0;

        // return white on areas outside of image boundaries
        if (x < 0 || y < 0 || x > image.getWidth() || y > image.getHeight()) {
            return false;
        }

        try {
            pixelRGBValue = image.getRGB(x, y);
            r = (pixelRGBValue >> 16) & 0xff;
            g = (pixelRGBValue >> 8) & 0xff;
            b = (pixelRGBValue) & 0xff;
            luminance = (r * 0.299) + (g * 0.587) + (b * 0.114);
        } catch (Exception e) {
            // ignore.
        }

        return luminance < luminanceCutOff;
    }

//    public static BufferedImage rotate(BufferedImage image, double angle, int cx, int cy) {
//        int width = image.getWidth(null);
//        int height = image.getHeight(null);
//
//        int minX, minY, maxX, maxY;
//        minX = minY = maxX = maxY = 0;
//
//        int[] corners = {0, 0, width, 0, width, height, 0, height};
//
//        double theta = Math.toRadians(angle);
//        for (int i = 0; i < corners.length; i += 2) {
//            int x = (int) (Math.cos(theta) * (corners[i] - cx)
//                    - Math.sin(theta) * (corners[i + 1] - cy) + cx);
//            int y = (int) (Math.sin(theta) * (corners[i] - cx)
//                    + Math.cos(theta) * (corners[i + 1] - cy) + cy);
//
//            if (x > maxX) {
//                maxX = x;
//            }
//
//            if (x < minX) {
//                minX = x;
//            }
//
//            if (y > maxY) {
//                maxY = y;
//            }
//
//            if (y < minY) {
//                minY = y;
//            }
//
//        }
//
//        cx = (cx - minX);
//        cy = (cy - minY);
//
//        BufferedImage bi = new BufferedImage((maxX - minX), (maxY - minY),
//                image.getType());
//        Graphics2D g2 = bi.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//
//        g2.setBackground(Color.white);
//        g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
//
//        AffineTransform at = new AffineTransform();
//        at.rotate(theta, cx, cy);
//
//        g2.setTransform(at);
//        g2.drawImage(image, -minX, -minY, null);
//        g2.dispose();
//
//        return bi;
//    }
}
