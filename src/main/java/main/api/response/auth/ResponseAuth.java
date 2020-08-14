package main.api.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAuth implements Serializable {

  private boolean result;
  private AuthUserInfoResponse user;
  private AuthErrorsInfoResponse errors;


  public boolean getResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public AuthUserInfoResponse getUser() {
    return user;
  }

  public void setUser(AuthUserInfoResponse user) {
    this.user = user;
  }

  public AuthErrorsInfoResponse getErrors() {
    return errors;
  }

  public void setErrors(AuthErrorsInfoResponse errors) {
    this.errors = errors;
  }
}
