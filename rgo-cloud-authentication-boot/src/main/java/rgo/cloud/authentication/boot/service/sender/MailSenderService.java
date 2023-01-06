package rgo.cloud.authentication.boot.service.sender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rgo.cloud.authentication.boot.config.properties.MailSenderProperties;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailSenderService implements MailSender {
    private final static String SUBJECT = "Complete registration";
    private final static String TEXT = "To confirm your account, please enter the code in the registration field: ";

    private final JavaMailSender jms;
    private final MailSenderProperties config;
    private final ExecutorService executor;

    public MailSenderService(JavaMailSender jms, MailSenderProperties config) {
        this.jms = jms;
        this.config = config;
        this.executor = Executors.newFixedThreadPool(config.getMaxPoolSize());
    }

    @Override
    public void send(ConfirmationToken token) {
        executor.submit(() -> sendMessage(token));
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
