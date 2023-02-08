package rgo.cloud.authentication.boot.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import rgo.cloud.authentication.boot.api.decorator.AuthorizationFacadeDecorator;
import rgo.cloud.authentication.boot.api.decorator.ClientServiceDecorator;
import rgo.cloud.authentication.boot.config.properties.TokenProperties;
import rgo.cloud.authentication.boot.facade.AuthorizationFacade;
import rgo.cloud.authentication.boot.service.ClientService;
import rgo.cloud.authentication.boot.service.ConfirmationTokenService;
import rgo.cloud.authentication.db.config.PersistenceConfig;
import rgo.cloud.authentication.mail.api.MailSender;
import rgo.cloud.authentication.mail.api.MailSenderService;
import rgo.cloud.authentication.mail.api.MailSenderStub;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.authentication.mail.api.properties.MailSenderProperties;
import rgo.cloud.common.spring.config.AspectConfig;
import rgo.cloud.security.config.SecurityConfig;
import rgo.cloud.security.config.jwt.JwtProvider;

import java.util.Properties;

@Configuration
@ConfigurationPropertiesScan
@Import(value = { SecurityConfig.class, AspectConfig.class, PersistenceConfig.class})
public class ApplicationConfig {

    @Bean
    public ClientService clientService(ClientRepository clientRepository, PasswordEncoder encoder) {
        return new ClientService(clientRepository, encoder);
    }

    @Bean
    public ConfirmationTokenService tokenService(ConfirmationTokenRepository tokenRepository, TokenProperties config) {
        return new ConfirmationTokenService(tokenRepository, config);
    }

    @Bean
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

    @Bean
    public AuthorizationFacade authorizationFacade(
            ClientService clientService,
            ConfirmationTokenService tokenService,
            MailSender sender,
            JwtProvider jwtProvider,
            AuthenticationManager authenticationManager
    ) {
        return new AuthorizationFacade(clientService, tokenService, sender, jwtProvider, authenticationManager);
    }

    @Bean
    public AuthorizationFacadeDecorator authorizationDecorator(AuthorizationFacade facade) {
        return new AuthorizationFacadeDecorator(facade);
    }

    @Bean
    public ClientServiceDecorator clientDecorator(ClientService service) {
        return new ClientServiceDecorator(service);
    }
}
