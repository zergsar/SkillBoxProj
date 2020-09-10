package main.controllers;

import main.api.response.info.AppInfo;
import main.model.Tag;
import main.model.repository.TagRepository;
import main.service.AuthService;
import main.service.GeneralService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
  public AppInfo init() {
    AppInfo response = new AppInfo();
    response.setTitle(title);
    response.setSubtitle(subtitle);
    response.setPhone(phone);
    response.setEmail(email);
    response.setCopyright(copyright);
    response.setCopyrightFrom(copyrightFrom);
    return response;
  }

  @GetMapping("/api/settings")
  @Transactional(readOnly = true)
  public JSONObject getSettings() {
    return generalService.getSettingsFromBase();
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


//  @GetMapping("/api/tag")
//  public Iterable<Tag> getTag() {
//    return tagRepository.findAll();
//
//  }

}
