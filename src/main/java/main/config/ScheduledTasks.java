package main.config;

import main.service.CaptchaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

  private final CaptchaService captchaService;

  public ScheduledTasks(CaptchaService captchaService) {
    this.captchaService = captchaService;
  }

  @Scheduled(fixedRate = 30000)
  public void init() {
    captchaService.deleteOldCaptcha();
  }
}

