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
@Table(name = "post_votes")
public class PostVotes {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User userId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "post_id", nullable = false)
  private Post postId;

  @Column(name = "time", nullable = false)
  private Calendar time;

  @Column(name = "value", nullable = false)
  private byte value;


  public PostVotes(){}

  public PostVotes(User userId, Post postId, byte value){

    this.userId = userId;
    this.postId = postId;
    this.value = value;
    this.time = Calendar.getInstance();

  }

  public int getId() {
    return id;
  }

  public User getUserId() {
    return userId;
  }

  public void setUserId(User userId) {
    this.userId = userId;
  }

  public Post getPostId() {
    return postId;
  }

  public void setPostId(Post postId) {
    this.postId = postId;
  }

  public Calendar getTime() {
    return time;
  }

  public void setTime(Calendar time) {
    this.time = time;
  }

  public byte getValue() {
    return value;
  }

  public void setValue(byte value) {
    this.value = value;
  }
}
