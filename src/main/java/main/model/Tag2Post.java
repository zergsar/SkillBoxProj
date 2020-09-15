package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tag2post")
public class Tag2Post {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post postId;

  @ManyToOne
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tagId;

  public Tag2Post() {
  }

  public Tag2Post(Post postId, Tag tagId) {
    this.postId = postId;
    this.tagId = tagId;
  }

  public int getId() {
    return id;
  }

  public Post getPostId() {
    return postId;
  }

  public void setPostId(Post postId) {
    this.postId = postId;
  }

  public Tag getTagId() {
    return tagId;
  }

  public void setTagId(Tag tagId) {
    this.tagId = tagId;
  }

  public void setId(int id) {
    this.id = id;
  }
}
