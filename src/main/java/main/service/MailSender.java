package main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSender {

  @Value("${mail.username}")
  private String username;

  private JavaMailSender mailSender;

  public MailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendMail(String emailTo, String subj, String message){
    SimpleMailMessage mailMessage = new SimpleMailMessage();

    mailMessage.setFrom(username);
    mailMessage.setTo(emailTo);
    mailMessage.setSubject(subj);
    mailMessage.setText(message);

    mailSender.send(mailMessage);
  }


}
