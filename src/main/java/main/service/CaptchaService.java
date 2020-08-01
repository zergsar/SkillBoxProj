package main.service;

import main.controllers.responsetemplate.CaptchaInfoResponse;
import main.model.CaptchaCodes;
import main.model.CaptchaCodesRepository;
import main.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class CaptchaService {

    @Autowired
    CaptchaCodesRepository captchaCodesRepository;

    @Value("${captcha.timeout}")
    private int deleteCaptchaTimeout;


    public void runCaptchaDeletingThread()
    {
        new Thread(this::deleteOldCaptcha).start();
    }

    public CaptchaInfoResponse generateCaptcha()
    {
        CaptchaInfoResponse captchaInfoResponse = new CaptchaInfoResponse();
        int visibleLength = 7;
        int secretLength = 17;

        String secretCode = Generator.generateRandomString(secretLength);
        String visibleCode = Generator.generateRandomString(visibleLength);
        String imageString64 = Generator.generateCaptchaImageString(visibleCode);

        captchaInfoResponse.setSecret(secretCode);
        captchaInfoResponse.setImage("data:image/png;base64, " + imageString64);

        saveCaptchaToBase(visibleCode, secretCode);

        return captchaInfoResponse;

    }

    private void saveCaptchaToBase(String visibleCode, String secretCode)
    {
        CaptchaCodes captchaCodes = new CaptchaCodes(visibleCode, secretCode);
        captchaCodesRepository.save(captchaCodes);
    }


    public boolean validateCaptcha(String code, String secretCode)
    {
        boolean isRight = false;

        if(captchaCodesRepository.findByCode(code).isPresent())
        {
            CaptchaCodes captchaCodes = captchaCodesRepository.findByCode(code).get();

            if(captchaCodes.getSecretCode().equals(secretCode))
            {
                isRight = true;
            }
        }

        return isRight;
    }

    private void deleteOldCaptcha()
    {
        int threadSleepTimeInSec = 30;

        while(true)
        {
            captchaCodesRepository.findAll().forEach(code -> {

                Calendar currentTime = Calendar.getInstance();
                Calendar captchaGenerationTime = code.getTime();

                captchaGenerationTime.add(Calendar.MINUTE, deleteCaptchaTimeout);

                if (currentTime.after(captchaGenerationTime)) {
                    captchaCodesRepository.deleteById(code.getId());
                }
            });

            try {
                Thread.sleep(threadSleepTimeInSec * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
