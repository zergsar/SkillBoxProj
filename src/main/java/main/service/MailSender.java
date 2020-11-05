package main.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailSender {

  @Value("${mail.username}")
  private String username;

  private final JavaMailSender mailSender;

  public MailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendMail(String emailTo, String subj, String message){
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessage.setContent(message, "text/html");
      helper.setTo(emailTo);
      helper.setSubject(subj);
      helper.setFrom(username);

      mailSender.send(mimeMessage);

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }


}
