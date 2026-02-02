package org.example.email;

import org.example.model.Usuario;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SaludoSimple implements BookExSaluda {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    @Override
    public void saludar(Usuario user) {

        // Do the business calculations...

        // Call the collaborators to persist the order...

        // Create a thread-safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(user.getCorreo());
        msg.setText(
                "Hola " + user.getNombre()
                        + ", Bienvenido a BookEx, tu plataforma de libros favorita!\n Es un placer tenerte con nosotros.\n\n");
        try {
            this.mailSender.send(msg);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

}
