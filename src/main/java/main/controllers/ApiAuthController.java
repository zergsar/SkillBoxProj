package main.controllers;

import main.controllers.requesttemplate.RequestInfo;
import main.controllers.responsetemplate.Response;
import main.service.AuthService;
import main.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class ApiAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CaptchaService captchaService;

    private String sessionId;
    private Response response;


    @GetMapping("/api/auth/check")
    public ResponseEntity isAuthorization(HttpSession httpSession)
    {
        sessionId = httpSession.getId();
        return new ResponseEntity(authService.isActiveSession(sessionId), HttpStatus.OK);
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity logout(HttpSession httpSession)
    {
        sessionId = httpSession.getId();
        return new ResponseEntity(authService.logout(sessionId), HttpStatus.OK);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity login(@RequestBody RequestInfo user, HttpSession httpSession)
    {
        sessionId = httpSession.getId();
        response = authService.authentication(user, sessionId);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity newUserReg(@RequestBody RequestInfo user)
    {
        response = authService.saveNewUserToBase(user);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity editProfile(HttpSession httpSession, @RequestBody RequestInfo requestInfo)
    {
        authService.profileSetup(httpSession, requestInfo);
        return new ResponseEntity(response, HttpStatus.OK);

    }


    @GetMapping("/api/auth/captcha")
    public ResponseEntity getCaptcha()
    {
        return new ResponseEntity(captchaService.generateCaptcha(), HttpStatus.OK);
    }


}
