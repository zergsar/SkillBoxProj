package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentToPostRequest {

  @JsonProperty("parent_id")
  private String parentId;
  @JsonProperty("post_id")
  private String postId;
  private String text;

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
