package main.model;


import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "post_comments")
public class PostComments {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private PostComments parentId;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post postId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User userId;

  @Column(name = "time", nullable = false)
  private Calendar time;

  @Column(columnDefinition = "TEXT NOT NULL", name = "text")
  private String text;

  public PostComments() {

  }

  public PostComments(String text, Post postId, PostComments parentId, User userId) {
    this.text = text;
    this.postId = postId;
    this.parentId = parentId;
    this.userId = userId;
    this.time = Calendar.getInstance();
  }

  public PostComments(String text, Post postId, User userId) {
    this.text = text;
    this.postId = postId;
    this.userId = userId;
    this.time = Calendar.getInstance();
  }

  public int getId() {
    return id;
  }

  public PostComments getParentId() {
    return parentId;
  }

  public void setParentId(PostComments parentId) {
    this.parentId = parentId;
  }

  public Post getPostId() {
    return postId;
  }

  public void setPostId(Post postId) {
    this.postId = postId;
  }

  public User getUserId() {
    return userId;
  }

  public void setUserId(User userId) {
    this.userId = userId;
  }

  public Calendar getTime() {
    return time;
  }

  public void setTime(Calendar time) {
    this.time = time;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
