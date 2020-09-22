package main.service;

import java.awt.image.BufferedImage;
import java.awt.image.MultiPixelPackedSampleModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import main.api.response.passsword.PasswordErrorsResponse;
import main.api.response.passsword.ResponsePassword;
import main.api.response.profile.ResponseUpdateProfile;
import main.api.response.profile.UpdateProfilesErrorsResponse;
import main.model.User;
import main.model.cache.RedisCache;
import main.model.repository.UserRepository;
import main.utils.FileUtils;
import main.utils.Generator;
import main.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${image.upload.max.size}")
  private int maxSizeFileMb;
  @Value("${subdir.name.length}")
  private int subdirNameLength;
  @Value("${subdir.depth}")
  private int subdirDepth;
  @Value("${default.upload.dir}")
  private String defaultUploadDir;
  @Value("${default.upload.temp.dir}")
  private String defaultUploadTempDir;
  @Value("${profile.photo.height}")
  private int maxProfilePhotoHeight;
  @Value("${profile.photo.width}")
  private int maxProfilePhotoWidth;

  public AuthService(UserRepository userRepository, CaptchaService captchaService,
      RedisCache redisCache, PostService postService, MailSender mailSender) {
    this.userRepository = userRepository;
    this.captchaService = captchaService;
    this.redisCache = redisCache;
    this.postService = postService;
    this.mailSender = mailSender;
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
      User newUser = new User((byte) 0, Calendar.getInstance(), name, email,
          bCryptPasswordEncoder.encode(password));
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

  public ResponseAuth authentication(LoginRequest user, String sessionId) {
    boolean isAllow = false;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    String email = user.getEmail();
    String password = user.getPassword();

    ResponseAuth responseAuth = new ResponseAuth();

    if (isExistEmail(email)) {
      int id = userRepository.findByEmail(email).get().getId();
      String dbPass = userRepository.findByEmail(email).get().getPassword();

      if (bCryptPasswordEncoder.matches(password, dbPass)) {
        responseAuth.setUser(getUserInfo(id));
        isAllow = true;
        redisCache.saveSessionToCache(sessionId, id);
      }
    }

    responseAuth.setResult(isAllow);

    return responseAuth;
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
    ResponseAuth responseAuth = new ResponseAuth();
    boolean isLogin = false;
    Optional<Integer> optId = redisCache.findUserIdBySessionId(sessionId);
    if (optId.isPresent()) {
      int id = optId.get();
      responseAuth.setUser(getUserInfo(id));
      isLogin = true;
    }
    responseAuth.setResult(isLogin);
    return responseAuth;
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

  private AuthUserInfoResponse getUserInfo(int id) {
    AuthUserInfoResponse authUserInfoResponse = new AuthUserInfoResponse();

    Optional<User> userOptional = userRepository.findById(id);

    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    User user = userOptional.get();

    String name = user.getName();
    String email = user.getEmail();
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

    return authUserInfoResponse;
  }

  public ResponseUpdateProfile updateProfile(String sessionId,
      EditProfileRequest editProfileRequest, MultipartFile photo) {
    ResponseUpdateProfile rup = validateProfileUpdates(sessionId, editProfileRequest, photo);

    if (rup.isResult()) {
      User user = getUserFromSessionID(sessionId);
      String email = editProfileRequest.getEmail();
      String name = editProfileRequest.getName();
      String pass = editProfileRequest.getPassword();
//      MultipartFile photo = editProfileRequest.getPhoto();
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
      if(photo != null && FileUtils.isMpfFileNotNull(photo)) {
        try {
          BufferedImage bi = ImageIO.read(photo.getInputStream());
          int width = bi.getWidth();
          int height = bi.getHeight();

          if(width <= maxProfilePhotoWidth && height <= maxProfilePhotoHeight)
          {

          }
          String tempPath = (defaultUploadTempDir.endsWith("/") ? defaultUploadTempDir : defaultUploadTempDir + "/") + photo.getOriginalFilename();
          File scaleFile = new File(tempPath);
          ImageIO.write(bi, "png", scaleFile);
          user.setPhoto(FileUtils.uploadFile(defaultUploadDir, subdirNameLength, subdirDepth, photo));
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
    }

//    MultipartFile photo = editProfileRequest.getPhoto();
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
      uper.setPhoto("Фото слишком большое, нужно не более " + maxSizeFileMb + " Мб");
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
