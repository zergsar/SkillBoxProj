package main.api.response.password;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePassword implements Serializable {

  private boolean result;
  private PasswordErrorsResponse errors;


  public boolean isResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public PasswordErrorsResponse getErrors() {
    return errors;
  }

  public void setErrors(PasswordErrorsResponse errors) {
    this.errors = errors;
  }
}
