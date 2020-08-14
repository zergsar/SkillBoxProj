package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "name", nullable = false)
  private String name;

//  @OneToMany(mappedBy = "tagId", cascade = CascadeType.ALL)
//  private List<Tag2Post> listTag2Post;

  public Tag() {

  }

  public Tag(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setId(int id) {
    this.id = id;
  }

//  public List<Tag2Post> getListTag2Post() {
//    return listTag2Post;
//  }
//
//  public void setListTag2Post(List<Tag2Post> listTag2Post) {
//    this.listTag2Post = listTag2Post;
//  }
}
