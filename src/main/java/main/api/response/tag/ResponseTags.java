package main.api.response.tag;

import java.util.HashSet;

public class ResponseTags {

  private HashSet<TagsWeightResponse> tags;

  public HashSet<TagsWeightResponse> getTags() {
    return tags;
  }

  public void setTags(HashSet<TagsWeightResponse> tags) {
    this.tags = tags;
  }
}
