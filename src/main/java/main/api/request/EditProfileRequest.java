package main.api.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import org.hibernate.mapping.Collection;
import org.springframework.web.multipart.MultipartFile;

public class EditProfileRequest implements Serializable {

  private String email;
  private String name;
  private String password;
  private MultipartFile photo;
  private Integer removePhoto;


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

  public MultipartFile getPhoto() {
    return photo;
  }

  public void setPhoto(MultipartFile photo) {
    this.photo = photo;
  }

  public Integer getRemovePhoto() {
    return removePhoto;
  }

  public void setRemovePhoto(Integer removePhoto) {
    this.removePhoto = removePhoto;
  }

  public HashMap<String, Object> getProfileFieldsMap() {
    HashMap<String, Object> fields = new HashMap<>();

//    fields.put("email", this.email == null ? "" : this.email);
//    fields.put("name", this.name == null ? "" : this.name);
//    fields.put("password", this.password == null ? "" : this.password);
//    fields.put("photo", this.photo == null ? "" : this.photo);
//    fields.put("removePhoto", this.removePhoto == null ? "" : this.removePhoto);

    fields.put("email", this.email);
    fields.put("name", this.name);
    fields.put("password", this.password);
    fields.put("photo", this.photo);
    fields.put("removePhoto", this.removePhoto);

    Iterator iterKey = fields.keySet().iterator();

    while(iterKey.hasNext())
    {
      String val = iterKey.next().toString();
      if(val == null)
      {
        iterKey.remove();
      }
    }

//    for (String key : fields.keySet()) {
//      if (fields.get(key) == null) {
//        fields.remove(key);
//      }
//    }

    return fields;
  }
}
