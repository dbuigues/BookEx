package org.example.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class SaludoConfig {

    @Bean
    public BookExSaluda bookExSaluda(MailSender mailSender) {
        SaludoSimple saludo = new SaludoSimple();
        saludo.setMailSender(mailSender);

        SimpleMailMessage template = new SimpleMailMessage();
        template.setSubject("¡Bienvenido a BookEx!");
        template.setFrom("bookexconnect@gmail.com");
        saludo.setTemplateMessage(template);

        return saludo;
    }
}