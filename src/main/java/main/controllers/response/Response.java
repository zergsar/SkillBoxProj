package main.controllers.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response implements Serializable {

  private boolean result;
  private UserInfoResponse user;
  private ErrorsInfoResponse errors;


  public boolean getResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public UserInfoResponse getUser() {
    return user;
  }

  public void setUser(UserInfoResponse user) {
    this.user = user;
  }

  public ErrorsInfoResponse getErrors() {
    return errors;
  }

  public void setErrors(ErrorsInfoResponse errors) {
    this.errors = errors;
  }
}
