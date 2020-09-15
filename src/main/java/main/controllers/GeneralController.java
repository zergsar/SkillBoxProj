package main.controllers;

import javax.servlet.http.HttpSession;
import main.api.response.info.AppInfo;
import main.api.response.settings.ResponseGeneralSettings;
import main.api.response.statistics.ResponseAllBlogStatistics;
import main.model.repository.TagRepository;
import main.service.AuthService;
import main.service.GeneralService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final GeneralService generalService;

  public GeneralController(AuthService authService, TagRepository tagRepository,
      GeneralService generalService) {
    this.authService = authService;
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

  @GetMapping("/api/statistics/all")
  public ResponseEntity<ResponseAllBlogStatistics> allBlogStatistics(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    boolean isPublicStatistics = generalService.getSettingsFromBase().isStatisticsIsPublic();
    if (isPublicStatistics) {
      return new ResponseEntity<>(generalService.getAllBlogStatistics(), HttpStatus.OK);
    } else {
      if (authService.isActiveSession(sessionId).getResult()) {
        Integer id = authService.isActiveSession(sessionId).getUser().getId();
        if (authService.isModerator(id)) {
          return new ResponseEntity<>(generalService.getAllBlogStatistics(), HttpStatus.OK);
        }
      }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
  }

  @GetMapping("/api/statistics/my")
  public ResponseEntity<ResponseAllBlogStatistics> myBlogStatistics(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    if (authService.isActiveSession(sessionId).getResult()) {
      Integer id = authService.isActiveSession(sessionId).getUser().getId();
      return new ResponseEntity<>(generalService.getMyBlogStatistics(id), HttpStatus.OK);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
  }


  @GetMapping("/api/settings")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponseGeneralSettings> getSettings() {
    return new ResponseEntity<>(generalService.getSettingsFromBase(), HttpStatus.OK);
  }

}
