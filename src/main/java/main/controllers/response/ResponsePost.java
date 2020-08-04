package main.controllers.response;

import java.util.ArrayList;

public class ResponsePost {

  private int count;
  private ArrayList<PostInfoResponse> posts;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public ArrayList<PostInfoResponse> getPosts() {
    return posts;
  }

  public void setPosts(ArrayList<PostInfoResponse> posts) {
    this.posts = posts;
  }
}
