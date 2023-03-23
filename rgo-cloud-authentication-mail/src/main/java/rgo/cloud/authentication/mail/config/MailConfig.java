package rgo.cloud.authentication.mail.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import rgo.cloud.authentication.mail.MailSender;
import rgo.cloud.authentication.mail.MailSenderService;
import rgo.cloud.authentication.mail.MailSenderStub;
import rgo.cloud.authentication.mail.config.properties.MailSenderProperties;

import java.util.Properties;

@Configuration
@ConfigurationPropertiesScan
public class MailConfig {

    @Bean
    @Profile("!test")
    public JavaMailSender javaMailSender(MailSenderProperties config) {
        JavaMailSenderImpl jms = new JavaMailSenderImpl();
        jms.setHost(config.host());
        jms.setPort(config.port());
        jms.setProtocol(config.protocol());

        jms.setUsername(config.sender());
        jms.setPassword(config.password());

        Properties props = jms.getJavaMailProperties();
        props.put("mail.transport.protocol", config.protocol());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return jms;
    }

    @Bean
    @Profile("test")
    public MailSender mailSenderStub() {
        return new MailSenderStub();
    }

    @Bean
    @Profile("!test")
    public MailSender mailSender(JavaMailSender jms, MailSenderProperties config) {
        return new MailSenderService(jms, config);
    }
}
