package in.coempt.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class SendMailUtil {

    private final JavaMailSender mail1;
    @Value("${from.mail}")
    private String fromMail;
    public  SendMailUtil(JavaMailSender mail1) {
        this.mail1 = mail1;
    }


    public void sendMail(String to, String subject, String msg) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromMail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);

        mail1.send(message);

    }

    public void sendMailForForgotPassword(String from, String to, String subject, String msg, int proritymail) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);

        mail1.send(message);


    }

    public void sendMailWithAttachment(String from, String to, String subject, String body, String fileToAttach) {
        try {

            MimeMessage Mimemessage = mail1.createMimeMessage();

            MimeMessageHelper message = new MimeMessageHelper(Mimemessage, true);

            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            //message.setText("My alternative text", true);
            //message.addBcc("BCC email");
            //message.addCc("CC email");

            FileSystemResource file = new FileSystemResource(fileToAttach);
            message.addAttachment(file.getFilename(), file);

            mail1.send(Mimemessage);

        } catch (MessagingException e) {
            throw new MailParseException(e);
        }
    }
    public void sendHtmlMail(String to, String subject, String body, String filePath) {
        try {
            MimeMessage mimeMessage = mail1.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

            messageHelper.setFrom(fromMail);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true); // true for HTML content

            // Add attachment
            if (filePath != null && !filePath.isEmpty()) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                messageHelper.addAttachment(file.getFilename(), file);
            }

            mail1.send(mimeMessage);
            System.out.println("Email sent successfully with attachment.");

        } catch (MessagingException e) {
            throw new MailParseException(e);
        }
    }

    public void sendHtmlmail(String to, String subject, String body) {
        try {

            MimeMessage Mimemessage = mail1.createMimeMessage();

            MimeMessageHelper message = new MimeMessageHelper(Mimemessage, true);

            message.setFrom(fromMail);
            message.setTo(to);
            message.setSubject(subject);
            //   message.setText("<html><body><a href=\"https://www.w3schools.com\">Visit W3Schools.com!</a></body></html>");
            message.setText(subject, body);
            //message.addBcc("BCC email");
            //message.addCc("CC email");

//            FileSystemResource file = new FileSystemResource(fileToAttach);
//            message.addAttachment(file.getFilename(), file);

            mail1.send(Mimemessage);

        } catch (MessagingException e) {
            throw new MailParseException(e);
        }
    }
}