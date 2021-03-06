package main.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import main.api.request.EditProfileRequest;
import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.request.RestorePassLinkRequest;
import main.api.request.RestorePassRequest;
import main.api.response.ResponseResult;
import main.api.response.auth.AuthErrorsInfoResponse;
import main.api.response.auth.AuthUserInfoResponse;
import main.api.response.auth.ResponseAuth;
import main.api.response.password.PasswordErrorsResponse;
import main.api.response.password.ResponsePassword;
import main.api.response.profile.ResponseUpdateProfile;
import main.api.response.profile.UpdateProfilesErrorsResponse;
import main.model.User;
import main.model.cache.RedisCache;
import main.model.repository.UserRepository;
import main.utils.FileUtils;
import main.utils.Generator;
import main.utils.ImageUtils;
import main.utils.MultipartImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final CaptchaService captchaService;
  private final RedisCache redisCache;
  private final PostService postService;
  private final MailSender mailSender;
  private final AuthenticationManager authenticationManager;

  @Value("${image.upload.max.size}")
  private int maxSizeFileMb;
  @Value("${subdir.name.length}")
  private int subdirNameLength;
  @Value("${subdir.depth}")
  private int subdirDepth;
  @Value("${default.upload.dir}")
  private String defaultUploadDir;
  @Value("${profile.photo.height}")
  private int maxProfilePhotoHeight;
  @Value("${profile.photo.width}")
  private int maxProfilePhotoWidth;

  public AuthService(UserRepository userRepository, CaptchaService captchaService,
      RedisCache redisCache, PostService postService, MailSender mailSender,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.captchaService = captchaService;
    this.redisCache = redisCache;
    this.postService = postService;
    this.mailSender = mailSender;
    this.authenticationManager = authenticationManager;
  }

  private ResponseAuth verifyInfoNewUser(RegistrationRequest user) {
    ResponseAuth responseAuth = new ResponseAuth();
    AuthErrorsInfoResponse authErrorsInfoResponse = new AuthErrorsInfoResponse();

    boolean isInputInfoRight = true;

    if (isExistEmail(user.getEmail())) {
      authErrorsInfoResponse.setEmail("Этот e-mail уже зарегистрирован");
      isInputInfoRight = false;
    }
    if (!isNameNotNull(user.getName())) {
      authErrorsInfoResponse.setName("Имя указано неверно");
      isInputInfoRight = false;
    }
    if (!isLenCondPass(user.getPassword())) {
      authErrorsInfoResponse.setPassword("Пароль короче 6-ти символов");
      isInputInfoRight = false;
    }
    if (!isRightCaptcha(user.getCaptcha(), user.getSecretCode())) {
      authErrorsInfoResponse.setCaptcha("Код с картинки введён неверно");
      isInputInfoRight = false;
    }

    responseAuth.setResult(isInputInfoRight);

    if (!isInputInfoRight) {
      responseAuth.setErrors(authErrorsInfoResponse);
    }

    return responseAuth;

  }

  public ResponseAuth saveUpdateUserToBase(RegistrationRequest user) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String email = user.getEmail();
    String name = user.getName();
    String password = user.getPassword();

    ResponseAuth responseAuth = verifyInfoNewUser(user);

    if (responseAuth.getResult()) {
      User newUser = new User.Builder()
          .withIsModerator((byte) 0)
          .withRegTime(Calendar.getInstance())
          .withName(name)
          .withEmail(email)
          .withPassword(bCryptPasswordEncoder.encode(password))
          .build();
      userRepository.save(newUser);
    }

    return responseAuth;
  }

  private boolean isExistEmail(String email) {
    boolean isExist = false;
    if (email != null && userRepository.findByEmail(email).isPresent()) {
      isExist = true;
    }
    return isExist;
  }

  private boolean isNameNotNull(String name) {
    boolean isNotNull = false;
    if (name != null && name.length() > 0) {
      isNotNull = true;
    }
    return isNotNull;
  }

  private boolean isLenCondPass(String password) {
    boolean isLenCondPass = false;

    if (password != null && password.length() >= 6) {
      isLenCondPass = true;
    }

    return isLenCondPass;
  }

  private boolean isRightCaptcha(String captcha, String secretCode) {
    return captchaService.validateCaptcha(captcha, secretCode);
  }


  public ResponseAuth authentication(LoginRequest loginRequest, String sessionId) {

    try {
      Authentication auth = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
              loginRequest.getPassword()));

      ResponseAuth ra;

      if (auth.isAuthenticated()) {
        org.springframework.security.core.userdetails.User userSecurity =
            (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        ra = getUserInfo(userSecurity.getUsername());
        if (ra.getResult()) {
          SecurityContextHolder.getContext().setAuthentication(auth);
          int id = ra.getUser().getId();
          redisCache.saveSessionToCache(sessionId, id);
          return ra;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return new ResponseAuth();

  }

  public boolean isModerator(int id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    return user.get().isModerator() == 1;
  }

  public ResponseAuth logout(String sessionId) {
    ResponseAuth responseAuth = new ResponseAuth();
    redisCache.deleteSessionFromCache(sessionId);
    responseAuth.setResult(true);
    return responseAuth;
  }


  public ResponseAuth isActiveSession(String sessionId) {
    Optional<Integer> optId = redisCache.findUserIdBySessionId(sessionId);
    if (optId.isPresent()) {
      int userId = optId.get();
      String email = userRepository.findById(userId).orElseThrow(
          () -> new UsernameNotFoundException("Пользователь с id: " + userId + " не найден"))
          .getEmail();
      return getUserInfo(email);
    }
    return new ResponseAuth();
  }

  public ResponseResult getRestoreLink(RestorePassLinkRequest restorePassLinkRequest,
      HttpServletRequest request) {
    ResponseResult rr = new ResponseResult();
    String email = restorePassLinkRequest.getEmail();
    String subj = "SkillBlog: Ссылка для восстановления пароля";
    if (isExistEmail(email)) {
      User user = getUserFromEmail(email);
      if (user != null) {
        try {
          URL url = new URL(request.getRequestURL().toString());
          String host =
              url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort()
                  : "");
          String hash = Generator.generateHash(10);
          String link = host + "/login/change-password/" + hash;
          String textEmail = "Link for restore password: <a href=\"" + link + "\">" + link + "</a>";
          user.setCode(hash);
          userRepository.save(user);
          mailSender.sendMail(email, subj, textEmail);
          rr.setResult(true);
          return rr;
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
    }
    rr.setResult(false);
    return rr;
  }

  public ResponsePassword restorePass(RestorePassRequest restorePassRequest) {
    ResponsePassword rp = validateRestorePassRequest(restorePassRequest);
    if (rp.isResult()) {
      String code = restorePassRequest.getCode();
      Optional<User> userOpt = userRepository.findByCode(code);
      if (userOpt.isPresent()) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String pass = restorePassRequest.getPassword();
        User user = userOpt.get();
        user.setCode(null);
        user.setPassword(bCryptPasswordEncoder.encode(pass));
        userRepository.save(user);
      }
    }
    return rp;
  }

  private ResponsePassword validateRestorePassRequest(RestorePassRequest restorePassRequest) {
    boolean result = true;
    PasswordErrorsResponse per = new PasswordErrorsResponse();
    ResponsePassword rp = new ResponsePassword();
    String code = restorePassRequest.getCode();
    String captcha = restorePassRequest.getCaptcha();
    String captchaSecret = restorePassRequest.getCaptchaSecret();
    String pass = restorePassRequest.getPassword();

    if (userRepository.findByCode(code).isEmpty()) {
      result = false;
      per.setCode(
          "Ссылка для восстановления пароля устарела. <a href=\"/login/restore-password\">Запросить ссылку снова</a>");
    }
    if (!isRightCaptcha(captcha, captchaSecret)) {
      result = false;
      per.setCaptcha("Код с картинки введен неверно");
    }
    if (!isLenCondPass(pass)) {
      result = false;
      per.setCaptcha("Пароль короче 6 символов");
    }
    rp.setResult(result);
    rp.setErrors(per);
    return rp;
  }

  private ResponseAuth getUserInfo(String email) {
    boolean isAllow = false;
    ResponseAuth responseAuth = new ResponseAuth();
    AuthUserInfoResponse authUserInfoResponse = new AuthUserInfoResponse();

    User user = userRepository.findByEmail(email).orElseThrow(
        () -> new UsernameNotFoundException(email));

    if (user != null) {
      isAllow = true;
      int id = user.getId();
      String name = user.getName();
      String photo = user.getPhoto();
      int modCount = 0;

      boolean isModerator = user.isModerator() == 1;
      boolean settings = isModerator;

      if (isModerator) {
        modCount = postService.getCountPostsForModeration();
      }

      authUserInfoResponse.setId(id);
      authUserInfoResponse.setName(name);
      authUserInfoResponse.setPhoto(photo);
      authUserInfoResponse.setEmail(email);
      authUserInfoResponse.setModeration(isModerator);
      authUserInfoResponse.setModerationCount(modCount);
      authUserInfoResponse.setSettings(settings);
    }

    responseAuth.setResult(isAllow);
    responseAuth.setUser(authUserInfoResponse);

    return responseAuth;
  }

  public ResponseUpdateProfile updateProfile(String sessionId,
      EditProfileRequest editProfileRequest, MultipartFile photo) {
    ResponseUpdateProfile rup = validateProfileUpdates(sessionId, editProfileRequest, photo);

    if (rup.isResult()) {
      User user = getUserFromSessionID(sessionId);
      String email = editProfileRequest.getEmail();
      String name = editProfileRequest.getName();
      String pass = editProfileRequest.getPassword();
      String removePhoto = editProfileRequest.getRemovePhoto();
      boolean changeEmail = !email.equals(user.getEmail());
      boolean changeName = !name.equals(user.getName());
      boolean changePass = !(pass == null || pass.isBlank());
      boolean isRemovePhoto = removePhoto != null && removePhoto.equals("1");

      if (changeEmail) {
        user.setEmail(email);
      }
      if (changeName) {
        user.setName(name);
      }
      if (changePass) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(pass));
      }
      if (isRemovePhoto) {
        user.setPhoto(null);
      }
      if (photo != null && FileUtils.isMpfFileNotNull(photo)) {
        try {
          BufferedImage bi = ImageIO.read(photo.getInputStream());
          bi = ImageUtils.scale(bi, maxProfilePhotoHeight, maxProfilePhotoWidth);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(bi, "jpeg", baos);
          MultipartFile photoAfterScale = new MultipartImage.Builder()
              .fromBytesArray(baos.toByteArray())
              .withName(photo.getName())
              .withOriginalFilename(photo.getOriginalFilename())
              .withContentType(photo.getContentType())
              .withSize(photo.getSize())
              .build();
          user.setPhoto(
              FileUtils
                  .uploadFile(defaultUploadDir, subdirNameLength, subdirDepth, photoAfterScale));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      userRepository.save(user);
    }
    return rup;
  }

  private User getUserFromEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      return null;
    }
    return user.get();
  }

  private ResponseUpdateProfile validateProfileUpdates(String sessionId,
      EditProfileRequest editProfileRequest, MultipartFile photo) {
    ResponseUpdateProfile rup = new ResponseUpdateProfile();
    UpdateProfilesErrorsResponse uper = new UpdateProfilesErrorsResponse();
    boolean result = true;
    User user = getUserFromSessionID(sessionId);
    if (user == null) {
      System.out.println("Пользователь не найден");
      result = false;
      rup.setResult(result);
      return rup;
    }

    String email = editProfileRequest.getEmail();
    String name = editProfileRequest.getName();
    String pass = editProfileRequest.getPassword();
    boolean changeEmail = !email.equals(user.getEmail());
    boolean changeName = !name.equals(user.getName());
    boolean changePass = !(pass == null || pass.isBlank());
    boolean isExistPhoto = photo != null && FileUtils.isMpfFileNotNull(photo);

    if (changePass && !isLenCondPass(pass)) {
      uper.setPassword("Пароль короче 6-ти символов");
      result = false;
    }
    if (changeEmail && isExistEmail(email)) {
      uper.setEmail("Этот e-mail уже зарегистрирован или введен неверно");
      result = false;
    }
    if (changeName && !isNameNotNull(name)) {
      uper.setName("Имя указано неверно");
      result = false;
    }

    if (isExistPhoto && !FileUtils.isValidMpfFileSize(photo, maxSizeFileMb)) {
      uper.setPhoto("Слишком большой размер файла с фотографией. (максимально допустимый размер: "
          + maxSizeFileMb + " Мб)");
      result = false;
    }

    rup.setResult(result);
    rup.setErrors(uper);
    return rup;
  }

  private User getUserFromSessionID(String sessionId) {
    boolean isActiveSession = redisCache.isCacheSession(sessionId);
    Optional<Integer> optId = redisCache.findUserIdBySessionId(sessionId);
    if (isActiveSession && optId.isPresent()) {
      int id = optId.get();
      Optional<User> userOpt = userRepository.findById(id);
      if (userOpt.isPresent()) {
        return userOpt.get();
      }
    }
    return null;
  }
}
