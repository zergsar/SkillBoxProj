package main.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import main.api.request.EditProfileRequest;
import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.request.RestorePassLinkRequest;
import main.api.request.RestorePassRequest;
import main.api.response.ResponseResult;
import main.api.response.auth.ResponseAuth;
import main.api.response.captcha.CaptchaInfoResponse;
import main.api.response.passsword.ResponsePassword;
import main.api.response.profile.ResponseUpdateProfile;
import main.service.AuthService;
import main.service.CaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    ResponseAuth ra = authService.authentication(user, sessionId);
    return new ResponseEntity<>(ra, HttpStatus.OK);
  }

  @PostMapping("/api/auth/register")
  @Transactional
  public ResponseEntity<ResponseAuth> newUserReg(@RequestBody RegistrationRequest user) {
    ResponseAuth ra = authService.saveUpdateUserToBase(user);
    return new ResponseEntity<>(ra, HttpStatus.OK);
  }

  @PostMapping(value = "/api/profile/my")
  @Transactional
  public ResponseEntity<ResponseUpdateProfile> editProfileWithoutPhoto(HttpSession httpSession,
      @RequestBody EditProfileRequest request) {
    String sessionId = httpSession.getId();
    ResponseUpdateProfile rup = authService.updateProfile(sessionId, request, null);
    return new ResponseEntity<>(rup, HttpStatus.OK);
  }

  @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Transactional
  public ResponseEntity<ResponseUpdateProfile> editProfileWithPhoto(HttpSession httpSession,
      @RequestParam("name") String name, @RequestParam("email") String email,
      @RequestParam(value = "password", required = false, defaultValue = "") String password,
      @RequestParam(value = "removePhoto", required = false, defaultValue = "0") String removePhoto,
      @RequestParam(value = "photo", required = false, defaultValue = "") MultipartFile photo) {
    String sessionId = httpSession.getId();
    EditProfileRequest epr = new EditProfileRequest();
    epr.setEmail(email);
    epr.setName(name);
    epr.setPassword(password);
    epr.setRemovePhoto(removePhoto);
    ResponseUpdateProfile rup = authService.updateProfile(sessionId, epr, photo);
    return new ResponseEntity<>(rup, HttpStatus.OK);
  }

  @GetMapping("/api/auth/captcha")
  @Transactional
  public ResponseEntity<CaptchaInfoResponse> getCaptcha() {
    return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
  }

  @PostMapping("/api/auth/restore")
  @Transactional
  public ResponseEntity<ResponseResult> restorePasswordLink(
      @RequestBody RestorePassLinkRequest rplr, HttpServletRequest request) {
    return new ResponseEntity<>(authService.getRestoreLink(rplr, request), HttpStatus.OK);
  }

  @PostMapping("/api/auth/password")
  @Transactional
  public ResponseEntity<ResponsePassword> restorePasswordRequest(
      @RequestBody RestorePassRequest restorePassRequest) {
    return new ResponseEntity<>(authService.restorePass(restorePassRequest), HttpStatus.OK);
  }

}
