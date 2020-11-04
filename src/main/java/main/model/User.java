package main.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import main.model.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "is_moderator", nullable = false)
  private byte isModerator;

  @Column(name = "reg_time", nullable = false)
  private Calendar regTime;

  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "email", nullable = false)
  private String email;
  @Column(name = "password", nullable = false)
  private String password;

  @JsonManagedReference
  @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
  private List<Post> posts;

  private String code;
  private String photo;

  public User() {
  }


  public int getId() {
    return id;
  }

  public byte isModerator() {
    return isModerator;
  }

  public void setIsModerator(byte isModerator) {
    this.isModerator = isModerator;
  }

  public Calendar getRegTime() {
    return regTime;
  }

  public void setRegTime(Calendar regTime) {
    this.regTime = regTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Role getRole(){
    return isModerator == 1 ? Role.MODERATOR : Role.USER;
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return name;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }


  public static class Builder{

    private User newUser;

    public Builder(){
      newUser = new User();
    }

    public Builder withName(String name){
      newUser.setName(name);
      return this;
    }

    public Builder withIsModerator(byte isModerator){
      newUser.setIsModerator(isModerator);
      return this;
    }

    public Builder withEmail(String email){
      newUser.setEmail(email);
      return this;
    }

    public Builder withPassword(String password){
      newUser.setPassword(password);
      return this;
    }

    public Builder withRegTime(Calendar regTime){
      newUser.setRegTime(regTime);
      return this;
    }

    public User build(){
      return newUser;
    }
  }


}
