package main.service;

import java.util.Optional;
import main.api.response.captcha.CaptchaInfoResponse;
import main.model.CaptchaCodes;
import main.model.repository.CaptchaCodesRepository;
import main.utils.Generator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

  private final CaptchaCodesRepository captchaCodesRepository;

  @Value("${captcha.timeout}")
  private int deleteCaptchaTimeout;
  @Value("${captcha.visible.length}")
  private int visibleLength;
  @Value("${captcha.secret.length}")
  private int secretLength;

  public CaptchaService(CaptchaCodesRepository captchaCodesRepository) {
    this.captchaCodesRepository = captchaCodesRepository;
  }

  public CaptchaInfoResponse generateCaptcha() {
    CaptchaInfoResponse captchaInfoResponse = new CaptchaInfoResponse();

    String secretCode = Generator.generateRandomString(secretLength);
    String visibleCode = Generator.generateRandomString(visibleLength);
    String imageString64 = Generator.generateCaptchaImageString(visibleCode);

    captchaInfoResponse.setSecret(secretCode);
    captchaInfoResponse.setImage("data:image/png;base64, " + imageString64);

    saveCaptchaToBase(visibleCode, secretCode);

    return captchaInfoResponse;

  }

  private void saveCaptchaToBase(String visibleCode, String secretCode) {
    CaptchaCodes captchaCodes = new CaptchaCodes(visibleCode, secretCode);
    captchaCodesRepository.save(captchaCodes);
  }


  public boolean validateCaptcha(String code, String secretCode) {
    Optional<CaptchaCodes> captchaCode = captchaCodesRepository.findByCode(code);

    if (!captchaCode.isPresent()) {
      return false;
    }

    return captchaCode.get().getSecretCode().equals(secretCode);
  }

  public void deleteOldCaptcha() {
    captchaCodesRepository.deleteAllOldCaptcha(deleteCaptchaTimeout);
  }
}
