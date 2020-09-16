package main.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import main.api.request.EditProfileRequest;
import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.request.RestorePassRequest;
import main.api.response.ResponseResult;
import main.api.response.auth.ResponseAuth;
import main.api.response.captcha.CaptchaInfoResponse;
import main.service.AuthService;
import main.service.CaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
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
    ResponseAuth responseAuth = authService.authentication(user, sessionId);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @PostMapping("/api/auth/register")
  @Transactional
  public ResponseEntity<ResponseAuth> newUserReg(@RequestBody RegistrationRequest user) {
    ResponseAuth responseAuth = authService.saveNewUserToBase(user);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

//  @PostMapping(value = "/api/profile/my", consumes = {"multipart/form-data", "application/json"})
//  @Transactional
//  public ResponseEntity<ResponseAuth> editProfile(HttpSession httpSession,
//      @ModelAttribute EditProfileRequest request) {
//    ResponseAuth responseAuth = authService.profileSetup(httpSession, request);
//    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
//  }

  @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_MIXED_VALUE)
  @Transactional
  public ResponseEntity<ResponseAuth> editProfile(HttpSession httpSession,
      @RequestPart("name") String name, @RequestPart("email") String email,
      @RequestPart(value = "password", required = false) String password,
      @RequestPart(value = "removePhoto", required = false) Integer removePhoto,
      @RequestPart(value = "photo", required = false) MultipartFile photo) {
    EditProfileRequest editProfileRequest = new EditProfileRequest();
    editProfileRequest.setEmail(email);
    editProfileRequest.setName(name);
    editProfileRequest.setPassword(password);
    editProfileRequest.setRemovePhoto(removePhoto);
    editProfileRequest.setPhoto(photo);
    ResponseAuth responseAuth = authService.profileSetup(httpSession, editProfileRequest);
    return new ResponseEntity<>(responseAuth, HttpStatus.OK);
  }

  @GetMapping("/api/auth/captcha")
  @Transactional
  public ResponseEntity<CaptchaInfoResponse> getCaptcha() {
    return new ResponseEntity<>(captchaService.generateCaptcha(), HttpStatus.OK);
  }

  @PostMapping("/api/auth/restore")
  @Transactional
  public ResponseEntity<ResponseResult> restoreRequest(
      @RequestBody RestorePassRequest restorePassRequest, HttpServletRequest request) {
    return new ResponseEntity<>(authService.getRestoreLink(restorePassRequest, request), HttpStatus.OK);
  }
//
//  @PostMapping("/api/auth/password")
//  @Transactional
//  public ResponseEntity

}
