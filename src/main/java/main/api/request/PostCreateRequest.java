package main.api.request;

import java.util.ArrayList;
import java.util.Calendar;

public class PostCreateRequest {

  private Calendar timestamp;
  private byte active;
  private String title;
  private ArrayList<String> tags;
  private String text;

  public Calendar getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Calendar timestamp) {
    this.timestamp = timestamp;
  }

  public byte getActive() {
    return active;
  }

  public void setActive(byte active) {
    this.active = active;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ArrayList<String> getTags() {
    return tags;
  }

  public void setTags(ArrayList<String> tags) {
    this.tags = tags;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
