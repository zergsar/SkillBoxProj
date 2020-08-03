package main.model;


import java.util.Calendar;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "time", nullable = false)
  private Calendar time;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "secret_code", nullable = false)
  private String secretCode;


  public CaptchaCodes() {

  }

  public CaptchaCodes(String code, String secretCode) {
    this.code = code;
    this.secretCode = secretCode;
    this.time = Calendar.getInstance();

  }


  public int getId() {
    return id;
  }

  public Calendar getTime() {
    return time;
  }

  public void setTime(Calendar time) {
    this.time = time;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getSecretCode() {
    return secretCode;
  }

  public void setSecretCode(String secretCode) {
    this.secretCode = secretCode;
  }
}
