package main.controllers;

import javax.servlet.http.HttpSession;
import main.api.request.EditProfileRequest;
import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.response.auth.ResponseAuth;
import main.api.response.captcha.CaptchaInfoResponse;
import main.service.AuthService;
import main.service.CaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  private final AuthService authService;
  private final CaptchaService captchaService;

  public AuthController(AuthService authService, CaptchaService captchaService) {
    this.authService = authService;
    this.captchaService = captchaService;
  }


  @GetMapping("/api/auth/check")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponseAuth> isAuthorization(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(authService.isActiveSession(sessionId), HttpStatus.OK);
  }

  @GetMapping("/api/auth/logout")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponseAuth> logout(HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(authService.logout(sessionId), HttpStatus.OK);
  }

  @PostMapping("/api/auth/login")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponseAuth> login(@RequestBody LoginRequest user,
      HttpSession httpSession) {
    String sessionId = httpSession.getId();
    ResponseAuth responseAuth = authService.authentication(user, sessionId);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @PostMapping("/api/auth/register")
  @Transactional
  public ResponseEntity<ResponseAuth> newUserReg(@RequestBody RegistrationRequest user) {
    ResponseAuth responseAuth = authService.saveNewUserToBase(user);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @PostMapping("/api/profile/my")
  @Transactional
  public ResponseEntity<ResponseAuth> editProfile(HttpSession httpSession,
      @ModelAttribute EditProfileRequest request) {
    ResponseAuth responseAuth = authService.profileSetup(httpSession, request);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @GetMapping("/api/auth/captcha")
  @Transactional
  public ResponseEntity<CaptchaInfoResponse> getCaptcha() {
    return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
  }


}
