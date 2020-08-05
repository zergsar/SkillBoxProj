package main.model;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import main.model.enums.ModerationStatus;

@Entity
@Table(name = "posts")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "is_active", nullable = false)
  private byte isActive;

  @Column(name = "moderation_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ModerationStatus moderationStatus;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "moderator_id")
  private User moderatorId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User userId;

  @Column(name = "time", nullable = false)
  private Calendar time;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "text", nullable = false)
  private String text;

  @Column(name = "view_count", nullable = false)
  private int viewCount;

  @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
  private Set<PostComments> postsComments;

  @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
  private List<Tag2Post> tag2Post;


  public Post() {

  }


  public ModerationStatus getModerationStatus() {
    return moderationStatus;
  }

  public void setModerationStatus(ModerationStatus moderationStatus) {
    this.moderationStatus = moderationStatus;
  }

  public byte getIsActive() {
    return isActive;
  }

  public void setIsActive(byte isActive) {
    this.isActive = isActive;
  }

  public int getId() {
    return id;
  }

  public User getModeratorId() {
    return moderatorId;
  }

  public void setModeratorId(User moderatorId) {
    this.moderatorId = moderatorId;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getViewCount() {
    return viewCount;
  }

  public void setViewCount(int viewCount) {
    this.viewCount = viewCount;
  }

  public Set<PostComments> getPostsComments() {
    return postsComments;
  }

  public void setPostsComments(Set<PostComments> postsComments) {
    this.postsComments = postsComments;
  }

  public List<Tag2Post> getTag2Post() {
    return tag2Post;
  }

  public void setTag2Post(List<Tag2Post> tag2Post) {
    this.tag2Post = tag2Post;
  }
}
