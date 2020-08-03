package main.model;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "is_moderator", nullable = false)
  private byte isModerator;

  @Column(name = "reg_time", nullable = false)
  private Calendar regTime;

  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "email", nullable = false)
  private String email;
  @Column(name = "password", nullable = false)
  private String password;

  @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  private String code;
  private String photo;

  public User() {
    this.isModerator = 0;
    this.regTime = Calendar.getInstance();
  }

  public User(byte isModerator, Calendar regTime, String name, String email, String password) {
    this.isModerator = isModerator;
    this.regTime = regTime;
    this.name = name;
    this.email = email;
    this.password = password;

  }


  public int getId() {
    return id;
  }

  public byte getIsModerator() {
    return isModerator;
  }

  public void setIsModerator(byte isModerator) {
    this.isModerator = isModerator;
  }

  public Calendar getRegTime() {
    return regTime;
  }

  public void setRegTime(Calendar regTime) {
    this.regTime = regTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }
}
