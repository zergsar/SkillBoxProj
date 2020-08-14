package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class RegistrationRequest implements Serializable {

  @JsonProperty("e_mail")
  private String email;
  private String name;
  private String password;
  private String captcha;
  @JsonProperty("captcha_secret")
  private String secretCode;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public String getSecretCode() {
    return secretCode;
  }

  public void setSecretCode(String secretCode) {
    this.secretCode = secretCode;
  }
}
