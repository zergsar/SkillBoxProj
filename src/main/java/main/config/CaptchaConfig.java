package main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaConfig {

  @Value("${captcha.timeout}")
  private int timeout;

  @Value("${captcha.visible.length}")
  private int visibleLength;

  @Value("${captcha.secret.length}")
  private int secretLength;

  @Value("${captcha.appearance.width}")
  private int width;

  @Value("${captcha.appearance.height}")
  private int height;

  @Value("${captcha.appearance.fontSize}")
  private int fontSize;

  @Value("${captcha.appearance.textCoordX}")
  private int textCoordX;

  @Value("${captcha.appearance.textCoordY}")
  private int textCoordY;


  public int getTimeout() {
    return timeout;
  }

  public int getVisibleLength() {
    return visibleLength;
  }

  public int getSecretLength() {
    return secretLength;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getFontSize() {
    return fontSize;
  }

  public int getTextCoordX() {
    return textCoordX;
  }

  public int getTextCoordY() {
    return textCoordY;
  }
}
