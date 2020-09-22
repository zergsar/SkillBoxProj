package main.api.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import org.springframework.web.multipart.MultipartFile;

public class EditProfileRequest implements Serializable {

  private String email;
  private String name;
  private String password;
  private String removePhoto;
//  private MultipartFile photo;


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

//  public MultipartFile getPhoto() {
//    return photo;
//  }
//
//  public void setPhoto(MultipartFile photo) {
//    this.photo = photo;
//  }

  public String getRemovePhoto() {
    return removePhoto;
  }

  public void setRemovePhoto(String removePhoto) {
    this.removePhoto = removePhoto;
  }
}
