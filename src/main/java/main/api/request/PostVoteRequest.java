package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class PostVoteRequest implements Serializable {

  @JsonProperty("post_id")
  private int postId;


  public int getPostId() {
    return postId;
  }

  public void setPostId(int postId) {
    this.postId = postId;
  }
}
