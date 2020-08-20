package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class DecisionToPostRequest implements Serializable {

  @JsonProperty("post_id")
  private int postId;
  private String decision;

  public int getPostId() {
    return postId;
  }

  public void setPostId(int postId) {
    this.postId = postId;
  }

  public String getDecision() {
    return decision;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }
}
