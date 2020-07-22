package main.model;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post postId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tagId;

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
}
