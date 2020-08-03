package main.controllers;

import javax.servlet.http.HttpSession;
import main.controllers.request.EditProfileRequest;
import main.controllers.request.LoginRequest;
import main.controllers.request.RegistrationRequest;
import main.controllers.response.CaptchaInfoResponse;
import main.controllers.response.Response;
import main.service.AuthService;
import main.service.CaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  private AuthService authService;
  private final CaptchaService captchaService;

  public AuthController(AuthService authService, CaptchaService captchaService) {
    this.authService = authService;
    this.captchaService = captchaService;
  }


  @GetMapping("/api/auth/check")
  public ResponseEntity<Response> isAuthorization(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(authService.isActiveSession(sessionId), HttpStatus.OK);
  }

  @GetMapping("/api/auth/logout")
  public ResponseEntity<Response> logout(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(authService.logout(sessionId), HttpStatus.OK);
  }

  @PostMapping("/api/auth/login")
  public ResponseEntity<Response> login(@RequestBody LoginRequest user, HttpSession httpSession) {
    String sessionId = httpSession.getId();
    Response response = authService.authentication(user, sessionId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/api/auth/register")
  public ResponseEntity<Response> newUserReg(@RequestBody RegistrationRequest user) {
    Response response = authService.saveNewUserToBase(user);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Response> editProfile(HttpSession httpSession,
      @RequestBody EditProfileRequest editProfileRequest) {
    Response response = authService.profileSetup(httpSession, editProfileRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/api/auth/captcha")
  public ResponseEntity<CaptchaInfoResponse> getCaptcha() {
    return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
  }


}
