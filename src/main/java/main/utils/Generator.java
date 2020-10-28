package main.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import main.config.CaptchaConfig;

public class Generator {

  public static String getRandomPathToImage(int lenChar, int countSubDir) {
    String randomString = generateRandomString(lenChar * countSubDir);
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < countSubDir; i++) {
      String randChar = new Random().ints(lenChar, 0, randomString.length())
          .mapToObj(randomString::charAt)
          .map(Object::toString)
          .collect(Collectors.joining());

      sb.append("/").append(randChar);
    }
    return sb.toString();
  }

  public static String generateRandomString(int len) {
    String symbols = "abcdefghijklmnopqrstuvwxyz0123456789";
    return new Random().ints(len, 0, symbols.length())
        .mapToObj(symbols::charAt)
        .map(Object::toString)
        .collect(Collectors.joining());
  }

  public static String generateCaptchaImageString(String visibleCode, CaptchaConfig captchaConfig) {
    String imageString64 = null;
    int width = captchaConfig.getWidth();
    int height = captchaConfig.getHeight();
    int fontSize = captchaConfig.getFontSize();
    int textCoordX = captchaConfig.getTextCoordX();
    int textCoordY = captchaConfig.getTextCoordY();

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

  public static String generateHash(int lenRandomStr){
    String randomString = generateRandomString(lenRandomStr);
    byte[] bytesRandomString = randomString.getBytes(StandardCharsets.UTF_8);
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      byte[] hashInBytes = md.digest(bytesRandomString);
      StringBuilder sb = new StringBuilder();
      for (byte hib : hashInBytes) {
        sb.append(String.format("%02x", hib));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return null;
  }
}
