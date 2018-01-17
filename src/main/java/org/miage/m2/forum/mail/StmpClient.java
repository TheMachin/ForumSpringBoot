package org.miage.m2.forum.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class StmpClient {



    public void sendEmail(MailBean mailBean, JavaMailSender sender) throws javax.mail.MessagingException {
        /*MimeMessage msg = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);*/
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailBean.getTo());
        simpleMailMessage.setFrom(mailBean.getFrom());
        simpleMailMessage.setText(mailBean.getMessage());
        simpleMailMessage.setSubject(mailBean.getSubject());

        sender.send(simpleMailMessage);
    }

}
