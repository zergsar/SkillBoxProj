package main.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.coobird.thumbnailator.Thumbnails;

public class ImageUtils {
  public static BufferedImage scale(BufferedImage image, int height, int width) {
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
