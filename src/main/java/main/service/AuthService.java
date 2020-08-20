package main.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import main.api.request.EditProfileRequest;
import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.response.auth.AuthErrorsInfoResponse;
import main.api.response.auth.ResponseAuth;
import main.api.response.auth.AuthUserInfoResponse;
import main.model.User;
import main.model.repository.UserRepository;
import main.model.cache.RedisCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;
  private final CaptchaService captchaService;
  private final RedisCache redisCache;
  private final PostService postService;

  public AuthService(UserRepository userRepository, CaptchaService captchaService,
      RedisCache redisCache, PostService postService) {
    this.userRepository = userRepository;
    this.captchaService = captchaService;
    this.redisCache = redisCache;
    this.postService = postService;
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

    if (redisCache.isCacheSession(sessionId)) {
      int id = redisCache.findUserIdBySessionId(sessionId).get();
      responseAuth.setUser(getUserInfo(id));
      isLogin = true;
    }

    responseAuth.setResult(isLogin);

    return responseAuth;

  }

  private AuthUserInfoResponse getUserInfo(int id) {
    AuthUserInfoResponse authUserInfoResponse = new AuthUserInfoResponse();

    Optional<User> userOptional = userRepository.findById(id);

    if (userOptional.isEmpty()){
      throw new RuntimeException("User not found");
    }

    User user = userOptional.get();

    String name = user.getName();
    String email = user.getEmail();
    String photo = user.getPhoto();
    int modCount = 0;                                                       //пока нет инфы о кол. постов поэтому 0

    boolean isModerator = user.isModerator() == 1;
    boolean settings = isModerator;

    if(isModerator){
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


  public ResponseAuth profileSetup(HttpSession httpSession, EditProfileRequest editProfileRequest) {
    ResponseAuth responseAuth = new ResponseAuth();
    AuthErrorsInfoResponse authErrorsInfoResponse = new AuthErrorsInfoResponse();
    String sessionId = httpSession.getId();

    boolean isActive = isActiveSession(sessionId).getResult();
    boolean isErrors = false;

    if (isActive) {
      int id = redisCache.findUserIdBySessionId(sessionId).get();
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


  @Override
  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByName(name);

    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }

    return user.get();
  }


}
