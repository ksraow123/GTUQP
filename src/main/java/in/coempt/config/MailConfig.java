package in.coempt.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl emailId=new JavaMailSenderImpl();
        emailId.setHost("email-smtp.ap-south-1.amazonaws.com");
        emailId.setPort(587);
        emailId.setUsername("AKIATQR75FPXDOD65E4G");
        emailId.setPassword("BMKh4tNVxlpTfwd6QDkbcfIa03WFaP508dr0nHB4v/Us");
//create properyties file object creation here  set values to pros
        Properties props = new Properties();
//it will take key as string and  value as also string

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.debug","false");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.ssl.trust", "email-smtp.ap-south-1.amazonaws.com");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        emailId.setJavaMailProperties(props);

        return  emailId;
    }
}
