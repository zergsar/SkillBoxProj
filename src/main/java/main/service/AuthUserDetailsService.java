//package main.service;
//
//import java.util.Optional;
//import javax.servlet.http.HttpSession;
//import main.api.request.LoginRequest;
//import main.api.response.auth.AuthUserInfoResponse;
//import main.api.response.auth.ResponseAuth;
//import main.model.User;
//import main.model.cache.RedisCache;
//import main.model.repository.UserRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthUserDetailsService implements UserDetailsService {
//
//  private final UserRepository userRepository;
//  private final RedisCache redisCache;
//
//  public AuthUserDetailsService(UserRepository userRepository,
//      RedisCache redisCache) {
//    this.userRepository = userRepository;
//    this.redisCache = redisCache;
//  }
//
//  @Override
//  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//
//    Optional<User> user = userRepository.findByEmail(email);
//
//    if (user.isEmpty()) {
//      throw new UsernameNotFoundException(email);
//    }
//
//    return user.get();
//  }
//
//  public ResponseAuth authentication(LoginRequest user, String sessionId)
//  {
//    boolean isAllow = false;
//    ResponseAuth responseAuth = new ResponseAuth();
//    String emailUser = user.getEmail();
//    UserDetails userDetails = loadUserByUsername(emailUser);
//
//    if(userDetails == null)
//    {
//      responseAuth.setResult(isAllow);
//      return responseAuth;
//    }
//
//    int userId = userRepository.findByEmail(emailUser).get().getId();
//
//    isAllow = true;
//    responseAuth.setUser(getUserInfo(userId));
//    redisCache.saveSessionToCache(sessionId, userId);
//
//    responseAuth.setResult(isAllow);
//
//    return responseAuth;
//
//  }
//
//
//  private AuthUserInfoResponse getUserInfo(int id) {
//    AuthUserInfoResponse authUserInfoResponse = new AuthUserInfoResponse();
//
//    String name = userRepository.findById(id).get().getName();
//    String email = userRepository.findById(id).get().getEmail();
//    String photo = userRepository.findById(id).get().getPhoto();
//    int modCount = 0;                                                       //пока нет инфы о кол. постов поэтому 0
//
//    boolean moderation = isModerator(id);
//    boolean settings = isModerator(id);                                     //непонятно что за настройка
//
//    authUserInfoResponse.setId(id);
//    authUserInfoResponse.setName(name);
//    authUserInfoResponse.setPhoto(photo);
//    authUserInfoResponse.setEmail(email);
//    authUserInfoResponse.setModeration(moderation);
//    authUserInfoResponse.setModerationCount(modCount);
//    authUserInfoResponse.setSettings(settings);
//
//    return authUserInfoResponse;
//  }
//
//  private boolean isModerator(int id) {
//    boolean isModer = false;
//    User user = userRepository.findById(id).get();
//    if (user.isModerator() == 1) {
//      isModer = true;
//    }
//
//    return isModer;
//  }
//
//
//}
