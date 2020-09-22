package main.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtils {


  public static BufferedImage scaleImage(BufferedImage image, int scale) {
    int newHeight = image.getHeight() / scale;
    int newWidth = image.getWidth() / scale;

    BufferedImage afterScale = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = afterScale.createGraphics();

    if (scale == 2) {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    } else {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    g.drawImage(image, 0, 0, afterScale.getWidth(), afterScale.getHeight(), null);
    g.dispose();

    if (scale != 2) {
      afterScale = scaleImage(afterScale, 2);
    }

    return afterScale;
  }


}
