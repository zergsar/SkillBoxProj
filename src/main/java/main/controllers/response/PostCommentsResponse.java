package main.controllers.response;

public class PostCommentsResponse {

  private int id;
  private long timestamp;
  private String text;
  private PostUserInfoResponse user;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public PostUserInfoResponse getUser() {
    return user;
  }

  public void setUser(PostUserInfoResponse user) {
    this.user = user;
  }
}
