package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthUserInfoResponse implements Serializable {

  private String email;
  private String name;
  private String password;
  private String captcha;
  private String photo;
  private boolean moderation;
  private Integer moderationCount;
  private boolean settings;
  private Integer id;
  private boolean result;


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

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public boolean isModeration() {
    return moderation;
  }

  public void setModeration(boolean moderation) {
    this.moderation = moderation;
  }

  public Integer getModerationCount() {
    return moderationCount;
  }

  public void setModerationCount(Integer moderationCount) {
    this.moderationCount = moderationCount;
  }

  public boolean isSettings() {
    return settings;
  }

  public void setSettings(boolean settings) {
    this.settings = settings;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public boolean isResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }
}
