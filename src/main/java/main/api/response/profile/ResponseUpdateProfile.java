package main.api.response.profile;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUpdateProfile {

  private boolean result;
  private UpdateProfilesErrorsResponse errors;

  public boolean isResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public UpdateProfilesErrorsResponse getErrors() {
    return errors;
  }

  public void setErrors(
      UpdateProfilesErrorsResponse errors) {
    this.errors = errors;
  }
}
