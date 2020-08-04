package main.controllers;

import javax.servlet.http.HttpSession;
import main.controllers.request.EditProfileRequest;
import main.controllers.request.LoginRequest;
import main.controllers.request.RegistrationRequest;
import main.controllers.response.CaptchaInfoResponse;
import main.controllers.response.ResponseAuth;
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
  public ResponseEntity<ResponseAuth> isAuthorization(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(authService.isActiveSession(sessionId), HttpStatus.OK);
  }

  @GetMapping("/api/auth/logout")
  public ResponseEntity<ResponseAuth> logout(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(authService.logout(sessionId), HttpStatus.OK);
  }

  @PostMapping("/api/auth/login")
  public ResponseEntity<ResponseAuth> login(@RequestBody LoginRequest user, HttpSession httpSession) {
    String sessionId = httpSession.getId();
    ResponseAuth responseAuth = authService.authentication(user, sessionId);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @PostMapping("/api/auth/register")
  public ResponseEntity<ResponseAuth> newUserReg(@RequestBody RegistrationRequest user) {
    ResponseAuth responseAuth = authService.saveNewUserToBase(user);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ResponseAuth> editProfile(HttpSession httpSession,
      @RequestBody EditProfileRequest editProfileRequest) {
    ResponseAuth responseAuth = authService.profileSetup(httpSession, editProfileRequest);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @GetMapping("/api/auth/captcha")
  public ResponseEntity<CaptchaInfoResponse> getCaptcha() {
    return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
  }


}
