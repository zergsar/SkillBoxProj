package main.api.response.comment;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCommentToPost {

  private Integer id;
  private boolean result;
  private CommentErrorsResponse errors;

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

  public CommentErrorsResponse getErrors() {
    return errors;
  }

  public void setErrors(CommentErrorsResponse errors) {
    this.errors = errors;
  }
}
