package main.controllers.response;


import java.util.HashMap;
import java.util.HashSet;

public class ResponseCalendar {

  private HashSet<Integer> Years;
  private HashMap<String, Integer> posts;


  public HashSet<Integer> getYears() {
    return Years;
  }

  public void setYears(HashSet<Integer> years) {
    Years = years;
  }

  public HashMap<String, Integer> getPosts() {
    return posts;
  }

  public void setPosts(HashMap<String, Integer> posts) {
    this.posts = posts;
  }
}
