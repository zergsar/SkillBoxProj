package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseImageUpload {

  private boolean result;
  private PostImageErrorsResponse errors;
  private String pathToImage;

  public boolean isResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public PostImageErrorsResponse getErrors() {
    return errors;
  }

  public void setErrors(PostImageErrorsResponse errors) {
    this.errors = errors;
  }

  public String getPathToImage() {
    return pathToImage;
  }

  public void setPathToImage(String pathToImage) {
    this.pathToImage = pathToImage;
  }
}
