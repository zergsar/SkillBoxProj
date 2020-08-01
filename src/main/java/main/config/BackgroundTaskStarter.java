package main.config;

import main.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BackgroundTaskStarter {

    @Autowired
    private CaptchaService captchaService;

    @PostConstruct
    public void init(){
        captchaService.runCaptchaDeletingThread();
    }
}

