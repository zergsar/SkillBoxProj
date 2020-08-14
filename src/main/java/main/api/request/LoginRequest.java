package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class LoginRequest implements Serializable {

  @JsonProperty("e_mail")
  private String email;
  private String password;

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
}
