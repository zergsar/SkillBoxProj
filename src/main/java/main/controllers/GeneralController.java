package main.controllers;

import main.model.Tag;
import main.model.TagRepository;
import main.service.AuthService;
import main.service.GeneralService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GeneralController {

  @Value("${title}")
  private String title;
  @Value("${subtitle}")
  private String subtitle;
  @Value("${phone}")
  private String phone;
  @Value("${email}")
  private String email;
  @Value("${copyright}")
  private String copyright;
  @Value("${copyrightFrom}")
  private String copyrightFrom;

  private JSONObject response;

  private final AuthService authService;
  private final TagRepository tagRepository;
  private final GeneralService generalService;

  public GeneralController(AuthService authService, TagRepository tagRepository,
      GeneralService generalService) {
    this.authService = authService;
    this.tagRepository = tagRepository;
    this.generalService = generalService;
  }


  @GetMapping("/api/init")
  public JSONObject init() {
    response = new JSONObject();
    response.put("title", title);
    response.put("subtitle", subtitle);
    response.put("phone", phone);
    response.put("email", email);
    response.put("copyright", copyright);
    response.put("copyrightFrom", copyrightFrom);

    return response;
  }

  @GetMapping("/api/settings")
  public JSONObject getSettings() {
    return generalService.getSettingsFromBase();
  }


  @PostMapping(value = "/api/profile/my")
  public ResponseEntity getTypeContent(@RequestHeader("Content-Type") String contType) {

    return null;
  }

//    public ResponseEntity profileMulti(HttpSession httpSession,
//                                    @RequestHeader("Content-Type") String contType,
//                                  @RequestParam(value = "name") String name,
//                                  @RequestParam(value = "email") String email,
//                                  @RequestParam(value = "password", required = false) Optional<String> password,
//                                  @Valid User user,
//                                  @RequestParam(value = "removePhoto", required = false) Integer removePhoto,
//                                  @RequestParam(value = "photo", required = false) MultipartFile photo)
//    {
//
//        JSONObject jsonObject = new JSONObject();

//        jsonObject.put("name", name.isEmpty() ? "" : name);
//        jsonObject.put("email", email.isEmpty() ? "" : email);
//        jsonObject.put("password", password.isEmpty() ? "" : password);
//        jsonObject.put("removePhoto", removePhoto.toString().isEmpty() ? "" : removePhoto);

//        if(contType.contains("multipart/form-data")) {
//            System.out.println(user.getName() + " " + user.getEmail() + " " + contType);
//        }
//
//
//        JSONObject response = authService.profileSetup(jsonObject, httpSession, photo);
//
//        if(response.get("result").equals("unauthorized"))
//        {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//        else
//        {
//            return new ResponseEntity(authService.profileSetup(jsonObject, httpSession, photo), HttpStatus.OK);
//        }

//            return null;
//
//    }


  @GetMapping("/api/tag")
  public Iterable<Tag> getTag() {
    return tagRepository.findAll();

  }

}
