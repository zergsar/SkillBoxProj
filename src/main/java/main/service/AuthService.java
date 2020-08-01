package main.service;

import main.controllers.requesttemplate.RequestInfo;
import main.controllers.responsetemplate.ErrorsInfoResponse;
import main.controllers.responsetemplate.Response;
import main.controllers.responsetemplate.UserInfoResponse;
import main.model.User;
import main.model.UserRepository;
import main.model.cache.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.HashMap;

@Service
public class AuthService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisCache redisCache;

    private Response response;
    private ErrorsInfoResponse errorsInfoResponse;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Response verifyInfoNewUser(RequestInfo user)
    {
        response = new Response();
        errorsInfoResponse = new ErrorsInfoResponse();

        boolean isInputInfoRight = true;

        if(isExistEmail(user.getEmail()))
        {
            errorsInfoResponse.setEmail("Этот e-mail уже зарегистрирован");
            isInputInfoRight = false;
        }
        if(!isNameNotNull(user.getName()))
        {
            errorsInfoResponse.setName("Имя указано неверно");
            isInputInfoRight = false;
        }
        if(!isLenCondPass(user.getPassword()))
        {
            errorsInfoResponse.setPassword("Пароль короче 6-ти символов");
            isInputInfoRight = false;
        }
        if(!isRightCaptcha(user.getCaptcha(), user.getSecretCode()))
        {
            errorsInfoResponse.setCaptcha("Код с картинки введён неверно");
            isInputInfoRight = false;
        }

        response.setResult(isInputInfoRight);

        if(!isInputInfoRight) {
            response.setErrors(errorsInfoResponse);
        }

        return response;

    }

    public Response saveNewUserToBase(RequestInfo user)
    {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String email = user.getEmail();
        String name = user.getName();
        String password = user.getPassword();

        response = verifyInfoNewUser(user);

        if(response.getResult())
        {
            User newUser = new User((byte) 0, Calendar.getInstance(), name, email,
                    bCryptPasswordEncoder.encode(password));
            userRepository.save(newUser);
        }

        return response;

    }

    private boolean isExistEmail(String email)
    {
        boolean isExist = false;

        if(userRepository.findByEmail(email).isPresent())
        {
            isExist = true ;
        }

        return isExist;
    }

    private boolean isNameNotNull(String name)
    {
        boolean isNotNull = false;

        if(name.length() > 0)
        {
            isNotNull = true;
        }

        return isNotNull;
    }

    private boolean isLenCondPass(String password)
    {
        boolean isLenCondPass = false;

        if(password.length() >= 6)
        {
            isLenCondPass = true;
        }

        return isLenCondPass;
    }

    private boolean isRightCaptcha(String captcha, String secretCode)
    {
        return captchaService.validateCaptcha(captcha, secretCode);
    }

    public Response authentication(RequestInfo user, String sessionId)
    {
        boolean isAllow = false;
        bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String email = user.getEmail();
        String password = user.getPassword();

        response = new Response();
        errorsInfoResponse = new ErrorsInfoResponse();

        if(isExistEmail(email))
        {
            int id = userRepository.findByEmail(email).get().getId();
            String dbPass = userRepository.findByEmail(email).get().getPassword();

            if(bCryptPasswordEncoder.matches(password, dbPass))
            {
                response.setUser(getUserInfo(id));
                isAllow = true;
                redisCache.saveSessionToCache(sessionId, id);
            }
        }

        response.setResult(isAllow);

        return response;
    }

    private boolean isModerator(int id)
    {
        boolean isModer = false;
        User user = userRepository.findById(id).get();
        if(user.getIsModerator() == 1)
        {
            isModer = true;
        }

        return isModer;
    }

    public Response logout(String sessionId)
    {
        response = new Response();
        redisCache.deleteSessionFromCache(sessionId);
        response.setResult(true);
        return response;
    }


    public Response isActiveSession(String sessionId)
    {
        response = new Response();
        boolean isLogin = false;

        if(redisCache.isCacheSession(sessionId))
        {
            int id = redisCache.findUserIdBySessionId(sessionId);
            response.setUser(getUserInfo(id));
            isLogin = true;
        }

        response.setResult(isLogin);

        return response;

    }

    private UserInfoResponse getUserInfo(int id)
    {
        UserInfoResponse userInfoResponse = new UserInfoResponse();

        String name = userRepository.findById(id).get().getName();
        String email = userRepository.findById(id).get().getEmail();
        String photo = userRepository.findById(id).get().getPhoto();
        int modCount = 0;                                                       //пока нет инфы о кол. постов поэтому 0

        boolean moderation = isModerator(id);
        boolean settings = isModerator(id);                                     //непонятно что за настройка

        userInfoResponse.setId(id);
        userInfoResponse.setName(name);
        userInfoResponse.setPhoto(photo);
        userInfoResponse.setEmail(email);
        userInfoResponse.setModeration(moderation);
        userInfoResponse.setModerationCount(modCount);
        userInfoResponse.setSettings(settings);

        return userInfoResponse;
    }


    public Response profileSetup(HttpSession httpSession, RequestInfo requestInfo) {
        response = new Response();
        errorsInfoResponse = new ErrorsInfoResponse();
        String sessionId = httpSession.getId();

        boolean isActive = isActiveSession(sessionId).getResult();
        boolean isErrors = false;

        if (isActive) {
            int id = redisCache.findUserIdBySessionId(sessionId);
            User user = userRepository.findById(id).get();
            String email = requestInfo.getEmail();
            String name = requestInfo.getName();

            HashMap<String, Object> fields = requestInfo.getProfileFieldsMap();

            for(String field : fields.keySet())
            {
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


//            for(String param : requestInfo.keySet())
//            {
//                String value = jsonObject.get(param).toString();
//
//                switch(param) {
//                    case "name":
//                        if (isNameNotNull(value)) {
//                            user.setName(value);
//                        } else {
//                            response.put(param, "Имя указано неверно");
//                            isErrors = true;
//                        }
//                        break;
//
//                    case "email":
//                        if (isExistEmail(value)) {
//                            user.setEmail(value);
//                        }else {
//                            response.put(param, "Этот e-mail уже зарегистрирован");
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
//            response.put("result", "unauthorized");
//        }
        }
        return response;
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
