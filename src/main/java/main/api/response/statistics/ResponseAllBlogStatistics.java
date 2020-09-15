package main.api.response.statistics;

import java.util.Calendar;

public class ResponseAllBlogStatistics {

  private int postsCount;
  private int likesCount;
  private int dislikesCount;
  private int viewsCount;
  private long firstPublication;

  public int getPostsCount() {
    return postsCount;
  }

  public void setPostsCount(int postsCount) {
    this.postsCount = postsCount;
  }

  public int getLikesCount() {
    return likesCount;
  }

  public void setLikesCount(int likesCount) {
    this.likesCount = likesCount;
  }

  public int getDislikesCount() {
    return dislikesCount;
  }

  public void setDislikesCount(int dislikesCount) {
    this.dislikesCount = dislikesCount;
  }

  public int getViewsCount() {
    return viewsCount;
  }

  public void setViewsCount(int viewsCount) {
    this.viewsCount = viewsCount;
  }

  public long getFirstPublication() {
    return firstPublication;
  }

  public void setFirstPublication(long firstPublication) {
    this.firstPublication = firstPublication;
  }
}
