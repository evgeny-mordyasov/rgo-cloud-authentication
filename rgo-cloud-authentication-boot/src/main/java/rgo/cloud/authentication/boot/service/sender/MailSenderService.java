package rgo.cloud.authentication.boot.service.sender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rgo.cloud.authentication.boot.config.properties.MailSenderProperties;
import rgo.cloud.authentication.internal.api.mail.MailMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailSenderService implements MailSender {
    private final JavaMailSender jms;
    private final MailSenderProperties config;
    private final ExecutorService executor;

    public MailSenderService(JavaMailSender jms, MailSenderProperties config) {
        this.jms = jms;
        this.config = config;
        this.executor = Executors.newFixedThreadPool(config.getMaxPoolSize());
    }

    @Override
    public void send(MailMessage msg) {
        executor.submit(() -> sendMessage(msg));
    }

    private void sendMessage(MailMessage msg) {
        SimpleMailMessage smm = createMessage(msg);
        jms.send(smm);
    }

    private SimpleMailMessage createMessage(MailMessage msg) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(config.getSender());
        smm.setTo(msg.getAddressee());
        smm.setSubject(msg.getHeader());
        smm.setText(msg.getMessage());

        return smm;
    }
}
