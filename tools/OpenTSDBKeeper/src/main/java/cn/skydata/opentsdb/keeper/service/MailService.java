package cn.skydata.opentsdb.keeper.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Created by nichenhao on 2017/8/23.
 */
@Service
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${mail.receiver}")
    private String[] receiver;

    @Value("${mail.enabled}")
    private Boolean enabled;


    public void sendMail(String subject, String content) {
        if (enabled) {
            Arrays.stream(receiver).forEach(to -> sendSimpleMail(sender, to, subject, content));
        } else {
            log.warn("Mail won't be sent because this module was disabled");
        }
    }

    private void sendSimpleMail(String from, String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
            log.info("Mail has sent to {} for [subject] {} and [content] {}", from, subject, content);
        } catch (MailException e) {
            log.info("Mail sent to {} for [subject] {} and [content] {} failed", from, subject, content, e);

        }
    }
}
