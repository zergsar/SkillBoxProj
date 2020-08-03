package main.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

public class Generator {

  public static String generateRandomString(int len) {
    String symbols = "abcdefghijklmnopqrstuvwxyz0123456789";
    return new Random().ints(len, 0, symbols.length())
        .mapToObj(symbols::charAt)
        .map(Object::toString)
        .collect(Collectors.joining());
  }

  public static String generateCaptchaImageString(String visibleCode) {
    String imageString64 = null;
    int width = 100;
    int height = 35;
    int fontSize = 16;
    int textCoordX = 20;
    int textCoordY = 20;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    Graphics2D g2 = image.createGraphics();

    g2.setFont(new Font("Arial", Font.BOLD, fontSize));
    g2.setColor(Color.BLACK);
    g2.drawString(visibleCode, textCoordX, textCoordY);

    try {

      ImageIO.write(image, "png", os);
      byte[] imageBytes = os.toByteArray();
      imageString64 = Base64.getEncoder().encodeToString(imageBytes);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return imageString64;
  }

}
