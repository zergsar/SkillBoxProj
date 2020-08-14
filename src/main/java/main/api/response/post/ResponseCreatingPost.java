package main.api.response.post;

public class ResponseCreatingPost {

  private boolean result;
  private PostCreatingErrorsResponse errors;


  public boolean isResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public PostCreatingErrorsResponse getErrors() {
    return errors;
  }

  public void setErrors(PostCreatingErrorsResponse errors) {
    this.errors = errors;
  }
}
