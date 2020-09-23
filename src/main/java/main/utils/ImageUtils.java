package main.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;

public class ImageUtils {

  public static BufferedImage scaleImage(BufferedImage image, int scaleFactor, float scaleH,
      float scaleW) {

    int newHeight = scaleFactor == 2 ? (image.getHeight() / 2) : (int) (image.getHeight() / scaleH);
    int newWidth = scaleFactor == 2 ? (image.getWidth() / 2) : (int) (image.getWidth() / scaleW);

    BufferedImage afterScale = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = afterScale.createGraphics();

    if (scaleFactor == 2) {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    } else {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    g.drawImage(image, 0, 0, afterScale.getWidth(), afterScale.getHeight(), null);
    g.dispose();

    if (scaleFactor != 2) {
      afterScale = scaleImage(afterScale, 2, 0, 0);
    }

    return afterScale;
  }

  public static BufferedImage testScale(BufferedImage image, int newHeight, int newWidth) {
    BufferedImage imageAfterScale = image;
    int sourceHeight = image.getHeight();
    int sourceWidth = image.getWidth();
    if (sourceHeight <= newHeight && sourceWidth <= newWidth) {
      return imageAfterScale;
    } else {
      float scaleH = 0;
      float scaleW = 0;

      if (sourceHeight > sourceWidth) {
        scaleH = (float) sourceHeight / (newHeight * 4);
        scaleW = scaleH;
      } else {
        scaleW = (float) sourceWidth / (newWidth * 4);
        scaleH = scaleW;
      }

      imageAfterScale = scaleImage(image, 1, scaleH, scaleW);
    }
    return imageAfterScale;
  }

  public static BufferedImage testScaleTwo(BufferedImage image, int height, int width) {
    int sourceHeight = image.getHeight();
    int sourceWidth = image.getWidth();
    if (sourceHeight <= height && sourceWidth <= width) {
      return image;
    }
    int ratioNorm = sourceHeight > sourceWidth ? sourceHeight / height : sourceWidth / width;
    int newHeight = sourceHeight > sourceWidth ? height : sourceHeight / ratioNorm;
    int newWidth = sourceHeight < sourceWidth ? width : sourceWidth / ratioNorm;
    BufferedImage thumbnail = null;

    try {
      thumbnail = Thumbnails.of(image).size(newWidth, newHeight).asBufferedImage();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return thumbnail;
  }

}
