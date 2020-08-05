package main.service;

import java.util.Calendar;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import main.controllers.request.EditProfileRequest;
import main.controllers.request.LoginRequest;
import main.controllers.request.RegistrationRequest;
import main.controllers.response.AuthErrorsInfoResponse;
import main.controllers.response.ResponseAuth;
import main.controllers.response.AuthUserInfoResponse;
import main.model.User;
import main.model.UserRepository;
import main.model.cache.RedisCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final CaptchaService captchaService;
  private final RedisCache redisCache;

  public AuthService(UserRepository userRepository, CaptchaService captchaService,
      RedisCache redisCache) {
    this.userRepository = userRepository;
    this.captchaService = captchaService;
    this.redisCache = redisCache;
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

  public ResponseAuth saveNewUserToBase(RegistrationRequest user) {
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

    if (userRepository.findByEmail(email).isPresent()) {
      isExist = true;
    }

    return isExist;
  }

  private boolean isNameNotNull(String name) {
    boolean isNotNull = false;

    if (name.length() > 0) {
      isNotNull = true;
    }

    return isNotNull;
  }

  private boolean isLenCondPass(String password) {
    boolean isLenCondPass = false;

    if (password.length() >= 6) {
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
    AuthErrorsInfoResponse authErrorsInfoResponse = new AuthErrorsInfoResponse();

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

  private boolean isModerator(int id) {
    boolean isModer = false;
    User user = userRepository.findById(id).get();
    if (user.isModerator() == 1) {
      isModer = true;
    }

    return isModer;
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

    if (redisCache.isCacheSession(sessionId)) {
      int id = redisCache.findUserIdBySessionId(sessionId);
      responseAuth.setUser(getUserInfo(id));
      isLogin = true;
    }

    responseAuth.setResult(isLogin);

    return responseAuth;

  }

  private AuthUserInfoResponse getUserInfo(int id) {
    AuthUserInfoResponse authUserInfoResponse = new AuthUserInfoResponse();

    String name = userRepository.findById(id).get().getName();
    String email = userRepository.findById(id).get().getEmail();
    String photo = userRepository.findById(id).get().getPhoto();
    int modCount = 0;                                                       //пока нет инфы о кол. постов поэтому 0

    boolean moderation = isModerator(id);
    boolean settings = isModerator(id);                                     //непонятно что за настройка

    authUserInfoResponse.setId(id);
    authUserInfoResponse.setName(name);
    authUserInfoResponse.setPhoto(photo);
    authUserInfoResponse.setEmail(email);
    authUserInfoResponse.setModeration(moderation);
    authUserInfoResponse.setModerationCount(modCount);
    authUserInfoResponse.setSettings(settings);

    return authUserInfoResponse;
  }


  public ResponseAuth profileSetup(HttpSession httpSession, EditProfileRequest editProfileRequest) {
    ResponseAuth responseAuth = new ResponseAuth();
    AuthErrorsInfoResponse authErrorsInfoResponse = new AuthErrorsInfoResponse();
    String sessionId = httpSession.getId();

    boolean isActive = isActiveSession(sessionId).getResult();
    boolean isErrors = false;

    if (isActive) {
      int id = redisCache.findUserIdBySessionId(sessionId);
      User user = userRepository.findById(id).get();
      String email = editProfileRequest.getEmail();
      String name = editProfileRequest.getName();

      HashMap<String, Object> fields = editProfileRequest.getProfileFieldsMap();

      for (String field : fields.keySet()) {
        System.out.println(field + " " + fields.get(field));

      }

//            if(!user.getEmail().equals(email) || isExistEmail(email))
//            {
//                user.setEmail(email);
//            }
//            if(!user.getName().equals(name))
//            {
//                user.setName(name);
//            }

//            for(String param : editProfileRequest.keySet())
//            {
//                String value = jsonObject.get(param).toString();
//
//                switch(param) {
//                    case "name":
//                        if (isNameNotNull(value)) {
//                            user.setName(value);
//                        } else {
//                            responseAuth.put(param, "Имя указано неверно");
//                            isErrors = true;
//                        }
//                        break;
//
//                    case "email":
//                        if (isExistEmail(value)) {
//                            user.setEmail(value);
//                        }else {
//                            responseAuth.put(param, "Этот e-mail уже зарегистрирован");
//                            isErrors = true;
//                        }
//                        break;
//
//                    case "photo":
//                        try {
//                            if(!photo.isEmpty()) {
//
//                                long fileSize = photo.getSize();
//                                byte[] allBytes = photo.getBytes();
//
//                                System.out.println("Размер файла: " + fileSize);
//
//                                System.out.println(allBytes[54]);
//                            }
//
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                }
//            }
//        }
//        else
//        {
//            responseAuth.put("result", "unauthorized");
//        }
    }
    return responseAuth;
  }
//    }

//    private JSONObject changeUserInformation(User user, String param, String value)
//    {
//        JSONObject response = new JSONObject();
//
//
//
//        userRepository.
//
//    }


}
