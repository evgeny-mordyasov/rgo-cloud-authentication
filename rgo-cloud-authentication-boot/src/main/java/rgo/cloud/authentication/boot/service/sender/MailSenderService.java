package rgo.cloud.authentication.boot.service.sender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rgo.cloud.authentication.boot.config.properties.MailSenderProperties;
import rgo.cloud.authentication.boot.service.sender.MailSender;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

// TODO: перейти на ExecutorService
public class MailSenderService implements MailSender {
    private final static String SUBJECT = "Complete registration";
    private final static String TEXT = "To confirm your account, please enter the code in the registration field: ";

    private final JavaMailSender jms;
    private final MailSenderProperties config;

    public MailSenderService(JavaMailSender jms, MailSenderProperties config) {
        this.jms = jms;
        this.config = config;
    }

    @Override
    public void send(ConfirmationToken token) {
        new Thread(() -> sendMessage(token))
                .start();
    }

    private void sendMessage(ConfirmationToken token) {
        SimpleMailMessage mailMessage = createMessage(token.getClient().getMail(), token.getToken());
        jms.send(mailMessage);
    }

    private SimpleMailMessage createMessage(String mail, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mail);
        mailMessage.setSubject(SUBJECT);
        mailMessage.setFrom(config.getSender());
        mailMessage.setText(TEXT + text);

        return mailMessage;
    }
}
