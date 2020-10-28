package main.service;

import java.util.Optional;
import main.api.response.captcha.CaptchaInfoResponse;
import main.config.CaptchaConfig;
import main.model.CaptchaCodes;
import main.model.repository.CaptchaCodesRepository;
import main.utils.Generator;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

  private final CaptchaConfig captchaConfig;
  private final CaptchaCodesRepository captchaCodesRepository;


  public CaptchaService(CaptchaConfig captchaConfig,
      CaptchaCodesRepository captchaCodesRepository) {
    this.captchaConfig = captchaConfig;
    this.captchaCodesRepository = captchaCodesRepository;
  }

  public CaptchaInfoResponse generateCaptcha() {
    CaptchaInfoResponse captchaInfoResponse = new CaptchaInfoResponse();

    String secretCode = Generator.generateRandomString(captchaConfig.getSecretLength());
    String visibleCode = Generator.generateRandomString(captchaConfig.getVisibleLength());
    String imageString64 = Generator.generateCaptchaImageString(visibleCode, captchaConfig);

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
    captchaCodesRepository.deleteAllOldCaptcha(captchaConfig.getTimeout());
  }
}
